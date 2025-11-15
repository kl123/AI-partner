package com.example.aipartner.controller;

import com.example.aipartner.pojo.User.Users;
import com.example.aipartner.pojo.result.Result;
import com.example.aipartner.service.DeviceDataService;
import com.example.aipartner.task.DeviceSyncTask;
import com.example.aipartner.utils.jwt.JWTUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
public class DeviceController {
    @Autowired
    private DeviceDataService deviceDataService;

    @Autowired
    private DeviceSyncTask deviceSyncTask;

    @GetMapping("/device/sync")
    public Result sync(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        Map<String, String> map = JWTUtils.getTokenInfo(token);
        deviceDataService.setAuth(map.get("userId"), map.get("password"));
        deviceSyncTask.enable();
        return Result.success();
    }

    /**
     * 绑定设备
     * @param users
     * @return
     */
    @PostMapping("/device/bind")
    public Result bind(@RequestBody Users users, HttpServletRequest request, @RequestParam("devId")String devId) {
        String token = request.getHeader("Authorization");
        Map<String, String> map = JWTUtils.getTokenInfo(token);
        String username = users.getUsername();
        String password = users.getPassword();
        boolean ok = deviceDataService.bindDevice(devId, username, password);
        return ok ? Result.success() : Result.error("绑定失败: 账户或设备不存在");
    }

    /**
     * 初始化设备
     * @param users
     * @return
     */
    @PostMapping("/device/init")
    public Result init(@RequestBody Users users, @RequestParam("devId")String devId) {
        String username = users.getUsername();
        String password = users.getPassword();
        boolean ok = deviceDataService.initDevice(devId, username, password);
        return ok ? Result.success() : Result.error("初始化失败: 设备编号无效");
    }
    
    
}
