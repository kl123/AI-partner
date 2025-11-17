package com.example.aipartner.service;

import org.apache.ibatis.annotations.Param;
import com.example.aipartner.pojo.device.Device;
import com.example.aipartner.pojo.device.DeviceInfo;
import com.example.aipartner.pojo.monitor.StudyMonitor;
import java.util.List;

public interface DeviceDataService {

    void fetchDataAndProcess();

    boolean syncDeviceData();

    void setAuth(String userId, String password);

    boolean bindDevice(String devId, String username, String password);

    boolean initDevice(String devId, String username, String password);

    String findDevId(@Param("nowUserId") String nowUserId, @Param("nowPassword") String nowPassword, @Param("nowUsername") String nowUsername);

    List<Device> listUserDevices(String userId);

    List<DeviceInfo> listUserDevicesInfo(String userId);

    List<StudyMonitor> listStudyMonitorByDevId(String devId);

    boolean updateDeviceNameUser(String devId, String deviceNameUser, String userId);
}