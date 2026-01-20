package com.example.aipartner.task;

import com.example.aipartner.service.DeviceDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DeviceSyncTask {
    private static final Logger log = LoggerFactory.getLogger(DeviceSyncTask.class);

    @Autowired
    private DeviceDataService deviceDataService;

    private volatile boolean enabled = false;

    public void enable() {
        enabled = true;
        log.info("Device sync enabled");
        run();
    }

    public void disable() {
        enabled = false;
        log.info("Device sync disabled");
    }

    
    // 每5分钟执行一次
    // @Scheduled(cron = "0 */5 * * * ?")

    // 每5秒执行一次
    @Scheduled(cron = "*/15 * * * * ?")   
    public void run() {
        if (!enabled) {
            return;
        }
        try {
            boolean continueSync = deviceDataService.syncDeviceData();
            if (!continueSync) {
                disable();
            }
        } catch (Exception e) {
            log.error("device sync task failed", e);
        }
    }
}
// {"end_if":"false","id":"BE20041006","sleep_time":"0:30","study_time":"4:34","walk_time":"0:30","phone_time":"0:25"}

