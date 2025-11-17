package com.example.aipartner.controller;

import com.example.aipartner.pojo.User.Users;
import com.example.aipartner.pojo.device.Device;
import com.example.aipartner.pojo.device.DeviceInfo;
import com.example.aipartner.pojo.monitor.StudyMonitor;
import com.example.aipartner.pojo.result.Result;
import com.example.aipartner.service.DeviceDataService;
import com.example.aipartner.task.DeviceSyncTask;
import com.example.aipartner.utils.jwt.JWTUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;


/**
 * DeviceController
 * 设备控制器
 */
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

    /**
     * 获取用户绑定的设备列表
     * @param request
     * @return
     */
    @GetMapping("/device/list")
    public Result list(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        Map<String, String> map = JWTUtils.getTokenInfo(token);
        List<Device> devices = deviceDataService.listUserDevices(map.get("userId"));
        return Result.success(devices);
    }

    /**
     * 获取设备详情（含图片和名称）
     * 从 Authorization 解析 userId 并进行连表查询
     * @param request
     * @return
     */
    @GetMapping("/device/details")
    public Result detailsByUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        Map<String, String> map = JWTUtils.getTokenInfo(token);
        List<DeviceInfo> infos = deviceDataService.listUserDevicesInfo(map.get("userId"));
        return Result.success(infos);
    }

    /**
     * 获取设备的学习监控数据
     * @param devId
     * @return
     */
    @GetMapping("/device/monitor/list")
    public Result listMonitor(@RequestParam("devId") String devId) {
        List<StudyMonitor> monitors = deviceDataService.listStudyMonitorByDevId(devId);
        return Result.success(monitors);
    }

    /**
     * 更新设备的用户自定义名称
     * @param devId 设备编号
     * @param deviceNameUser 用户自定义设备名称
     * @return 更新结果
     */
    @PutMapping("/device/name")
    public Result updateDeviceName(@RequestParam("devId") String devId,
                                   @RequestParam("deviceNameUser") String deviceNameUser,
                                   HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        Map<String, String> map = JWTUtils.getTokenInfo(token);
        boolean ok = deviceDataService.updateDeviceNameUser(devId, deviceNameUser, map.get("userId"));
        return ok ? Result.success() : Result.error("更新失败: 设备不存在或无权限");
    }


}
