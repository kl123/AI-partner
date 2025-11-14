package com.example.aipartner.controller;

import com.example.aipartner.pojo.result.Result;
import com.example.aipartner.service.DeviceDataService;
import com.example.aipartner.task.DeviceSyncTask;
import com.example.aipartner.utils.jwt.JWTUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeviceController {
    @Autowired
    private DeviceDataService deviceDataService;

    @Autowired
    private DeviceSyncTask deviceSyncTask;

    @GetMapping("/device/sync")
    public Result sync(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        java.util.Map<String, String> map = JWTUtils.getTokenInfo(token);
        deviceDataService.setAuth(map.get("userId"), map.get("password"));
        deviceSyncTask.enable();
        return Result.success();
    }
}
