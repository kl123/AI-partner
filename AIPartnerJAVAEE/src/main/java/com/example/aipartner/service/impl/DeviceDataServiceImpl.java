package com.example.aipartner.service.impl;

import com.example.aipartner.mapper.DeviceMapper;
import com.example.aipartner.mapper.StudyMonitorMapper;
import com.example.aipartner.mapper.UserMapper;
import com.example.aipartner.pojo.User.Users;
import com.example.aipartner.pojo.api.BemfaApiResponse;
import com.example.aipartner.pojo.api.DeviceData;
import com.example.aipartner.pojo.api.TopicInfoData;
import com.example.aipartner.pojo.device.Device;
import com.example.aipartner.pojo.monitor.StudyMonitor;
import com.example.aipartner.service.DeviceDataService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class DeviceDataServiceImpl implements DeviceDataService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private StudyMonitorMapper studyMonitorMapper;

    @Autowired
    private UserMapper userMapper;

    private final Map<String, Boolean> deviceSessionEnded = new ConcurrentHashMap<>();
    private final Map<String, Long> deviceLastUnix = new ConcurrentHashMap<>();
    private final Map<String, DeviceData> deviceLastData = new ConcurrentHashMap<>();
    private static final double STEP_MINUTES = 5.0;
    private volatile String currentUserId;
    private volatile String currentPassword;

    @Override
    @Async
    public void fetchDataAndProcess() {
        syncDeviceData();
    }

    @Override
    public boolean syncDeviceData() {
        try {
            String url = UriComponentsBuilder
                    .fromHttpUrl("http://apis.bemfa.com/vb/api/v2/topicInfo")
                    .queryParam("openID", "6fc94297b1a4771e713523fd16d19702")
                    .queryParam("type", 1)
                    .queryParam("topic", "OrangePi")
                    .toUriString();

            log.info("Requesting Bemfa API: {}", url);
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            String body = response.getBody();
            log.info("Received response body: {}", body);

            BemfaApiResponse apiResponse = objectMapper.readValue(body, BemfaApiResponse.class);
            return processApiResponseAndContinue(apiResponse);
        } catch (Exception e) {
            log.error("Failed to fetch/process device data: {}", e.getMessage(), e);
            return true;
        }
    }

    @Override
    public void setAuth(String userId, String password) {
        this.currentUserId = userId;
        this.currentPassword = password;
    }

    @Override
    public boolean bindDevice(String devId, String username, String password) {
        if (devId == null || devId.isEmpty()) return false;
        Users u = new Users();
        u.setUsername(username);
        u.setPassword(password);
        Long uid = userMapper.login(u);
        if (uid == null) {
            return false;
        }
        Device existing = deviceMapper.findById(devId);
        if (existing == null) {
            Device device = new Device();
            device.setDevId(devId);
            device.setUserid(uid.intValue());
            device.setPassword(password);
            deviceMapper.insert(device);
        } else {
            deviceMapper.updateBinding(devId, uid.intValue(), password);
        }
        return true;
    }

    @Override
    public boolean initDevice(String devId, String username, String password) {
        if (devId == null || devId.isEmpty()) return false;
        try {
            studyMonitorMapper.deleteByDevId(devId);
        } catch (Exception ignored) {}
        Device existing = deviceMapper.findById(devId);
        if (existing == null) {
            Device device = new Device();
            device.setDevId(devId);
            device.setUserid(0);
            device.setPassword("admin");
            deviceMapper.insert(device);
        } else {
            deviceMapper.updateBinding(devId, 0, "admin");
        }
        return true;
    }

    @Override
    public String findDevId(String nowUserId, String nowPassword, String nowUsername) {
        String devId = deviceMapper.findDevId(nowUserId,nowPassword,nowUsername);
        return  devId;
    }

    private boolean processApiResponseAndContinue(BemfaApiResponse apiResponse) {
        if (apiResponse == null) {
            log.error("API response is null");
            return true;
        }
        if (!Integer.valueOf(0).equals(apiResponse.getCode())) {
            log.error("API response code not success: {}", apiResponse.getCode());
            return true;
        }
        if (!"success".equals(apiResponse.getMsg())) {
            log.error("API response msg not success: {}", apiResponse.getMsg());
            return true;
        }
        if (apiResponse.getData() == null) {
            log.error("API response data is null");
            return true;
        }

        TopicInfoData info = apiResponse.getData();
        if (info.getMsg() == null || info.getMsg().isEmpty()) {
            log.error("API response data.msg is empty");
            return true;
        }
        DeviceData data = parseMsgToDeviceData(info.getMsg());
        if (data == null) {
            log.error("Unable to parse device payload from msg: {}", info.getMsg());
            return true;
        }
        if (data.getId() == null || data.getId().isEmpty()) {
            log.error("Device id missing in data: {}", data);
            return true;
        }
        log.info("Processing device data for devId: {}", data.getId());

        Long unix = info.getUnix();
        Boolean endedFlag = deviceSessionEnded.getOrDefault(data.getId(), false);
        if (unix != null) {
            Long last = deviceLastUnix.get(data.getId());
            if (last != null && last.equals(unix) && !Boolean.TRUE.equals(endedFlag)) {
                log.info("Duplicate unix observed for devId={} unix={}", data.getId(), unix);
            }
            deviceLastUnix.put(data.getId(), unix);
        }

        Device device = deviceMapper.findById(data.getId());
        if (device == null) {
            device = new Device();
            device.setDevId(data.getId());
            Integer uid = null;
            try {
                uid = currentUserId != null ? Integer.parseInt(currentUserId) : null;
            } catch (Exception ignored) {}
            device.setUserid(uid);
            device.setPassword(currentPassword);
            deviceMapper.insert(device);
            log.info("Inserted new device: {}", data.getId());
        }

        boolean newIsEnd = Boolean.TRUE.equals(data.getEnd_if());
        StudyMonitor latestMonitor = studyMonitorMapper.findLatestByDevId(data.getId());
        if (latestMonitor == null) {
            createNewMonitor(data);
            deviceSessionEnded.put(data.getId(), newIsEnd);
            return !newIsEnd;
        }

        Boolean ended = endedFlag;
        if (ended) {
            createNewMonitor(data);
            deviceSessionEnded.put(data.getId(), newIsEnd);
            return !newIsEnd;
        }
        if (newIsEnd) {
            updateExistingMonitor(latestMonitor, data);
            deviceSessionEnded.put(data.getId(), true);
            return false;
        } else {
            updateExistingMonitor(latestMonitor, data);
            return true;
        }
    }

    private void updateExistingMonitor(StudyMonitor existingMonitor, DeviceData newData) {
        DeviceData last = deviceLastData.get(newData.getId());
        int deltaSleep = Math.max(0, toSeconds(nullToZero(newData.getSleep_time())) - toSeconds(last == null ? "00:00" : nullToZero(last.getSleep_time())));
        int deltaStudy = Math.max(0, toSeconds(nullToZero(newData.getStudy_time())) - toSeconds(last == null ? "00:00" : nullToZero(last.getStudy_time())));
        int deltaWalk = Math.max(0, toSeconds(nullToZero(newData.getWalk_time())) - toSeconds(last == null ? "00:00" : nullToZero(last.getWalk_time())));
        int deltaPhone = Math.max(0, toSeconds(nullToZero(newData.getPhone_time())) - toSeconds(last == null ? "00:00" : nullToZero(last.getPhone_time())));

        existingMonitor.setTotalTime(addMinutes(existingMonitor.getTotalTime(), STEP_MINUTES));
        existingMonitor.setSleepTime(addHhMm(existingMonitor.getSleepTime(), formatMmSs(deltaSleep)));
        existingMonitor.setStudyTime(addHhMm(existingMonitor.getStudyTime(), formatMmSs(deltaStudy)));
        existingMonitor.setWalkTime(addHhMm(existingMonitor.getWalkTime(), formatMmSs(deltaWalk)));
        existingMonitor.setPhoneTime(addHhMm(existingMonitor.getPhoneTime(), formatMmSs(deltaPhone)));

        double totalMin = existingMonitor.getTotalTime();
        int totalSec = (int) Math.round(totalMin * 60);
        int studySec = toSeconds(existingMonitor.getStudyTime());
        int sleepSec = toSeconds(existingMonitor.getSleepTime());
        int attentionSec = Math.max(0, totalSec - studySec);

        existingMonitor.setAttentionTime(formatMmSs(attentionSec));
        existingMonitor.setStudyRatio(safeDiv(studySec, totalSec));
        existingMonitor.setSleepRadio(safeDiv(sleepSec, totalSec));
        existingMonitor.setAttentionRadio(safeDiv(attentionSec, totalSec));

        studyMonitorMapper.update(existingMonitor);
        deviceLastData.put(newData.getId(), newData);
        log.info("Updated study monitor id={} for devId={}", existingMonitor.getId(), newData.getId());
    }

    private void createNewMonitor(DeviceData data) {
        StudyMonitor monitor = new StudyMonitor();
        monitor.setDevId(data.getId());
        monitor.setTotalTime(STEP_MINUTES);
        monitor.setSleepTime(nullToZero(data.getSleep_time()));
        monitor.setStudyTime(nullToZero(data.getStudy_time()));
        monitor.setWalkTime(nullToZero(data.getWalk_time()));
        monitor.setPhoneTime(nullToZero(data.getPhone_time()));

        double totalMin = monitor.getTotalTime();
        int totalSec = (int) Math.round(totalMin * 60);
        int studySec = toSeconds(monitor.getStudyTime());
        int sleepSec = toSeconds(monitor.getSleepTime());
        int attentionSec = Math.max(0, totalSec - studySec);

        monitor.setAttentionTime(formatMmSs(attentionSec));
        monitor.setStudyRatio(safeDiv(studySec, totalSec));
        monitor.setSleepRadio(safeDiv(sleepSec, totalSec));
        monitor.setAttentionRadio(safeDiv(attentionSec, totalSec));

        studyMonitorMapper.insert(monitor);
        deviceLastData.put(data.getId(), data);
        log.info("Inserted new study monitor for devId={}", data.getId());
    }

    private String nullToZero(String hhmm) {
        return hhmm == null || hhmm.isEmpty() ? "00:00" : hhmm;
    }

    private String addHhMm(String time1, String time2) {
        int total = toSeconds(nullToZero(time1)) + toSeconds(nullToZero(time2));
        int minutes = total / 60;
        int seconds = total % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private int toSeconds(String mmss) {
        if (mmss == null || mmss.isEmpty()) return 0;
        String[] parts = mmss.split(":");
        if (parts.length != 2) return 0;
        int m = Integer.parseInt(parts[0]);
        int s = Integer.parseInt(parts[1]);
        return m * 60 + s;
    }

    private String formatMmSs(int seconds) {
        int m = Math.max(0, seconds) / 60;
        int s = Math.max(0, seconds) % 60;
        return String.format("%02d:%02d", m, s);
    }

    private double safeDiv(int numSec, int denSec) {
        if (denSec <= 0) return 0.0;
        return (double) numSec / (double) denSec;
    }

    private double addMinutes(double minutes1, double minutes2) {
        return minutes1 + minutes2;
    }

    private DeviceData parseMsgToDeviceData(String msg) {
        try {
            String trimmed = msg.trim();
            if (trimmed.startsWith("{") || trimmed.startsWith("[")) {
                return objectMapper.readValue(trimmed, DeviceData.class);
            }
            log.warn("Msg is not JSON: {}", msg);
            return null;
        } catch (Exception e) {
            log.error("Failed to parse msg as DeviceData: {}", e.getMessage(), e);
            return null;
        }
    }
}