package com.example.aipartner.controller;

import com.example.aipartner.pojo.device.AuxiliaryDevice;
import com.example.aipartner.utils.AliOSSUtils;
import com.example.aipartner.pojo.result.Result;
import com.example.aipartner.service.AuxiliaryDeviceService;
import com.example.aipartner.utils.jwt.JWTUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 辅助设备控制器
 */
@RestController
public class AuxiliaryDeviceController {

    @Autowired
    private AuxiliaryDeviceService auxiliaryDeviceService;
    @Autowired
    private AliOSSUtils aliOSSUtils;

    /**
     * 绑定辅助设备到设备
     * @param devId 设备编号
     * @param auxiliaryDeviceId 辅助设备编号
     * @param request 请求对象，用于获取 Authorization
     * @return 操作结果
     */
    @PostMapping("/device/auxiliary/add")
    public Result add(@RequestParam("devId") String devId,
                      @RequestParam("auxiliaryDeviceId") String auxiliaryDeviceId,
                      HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        Map<String, String> map = JWTUtils.getTokenInfo(token);
        return auxiliaryDeviceService.bindAuxiliaryDevice(auxiliaryDeviceId, devId, map);
    }

    /**
     * 通过 device_id 查询辅助设备
     * @param devId 设备编号
     * @param request 请求对象，用于获取 Authorization
     * @return 辅助设备列表
     */
    @GetMapping("/device/auxiliary/list")
    public Result listByDevId(@RequestParam("devId") String devId, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        Map<String, String> map = JWTUtils.getTokenInfo(token);
        return auxiliaryDeviceService.listAuxiliaryDevicesByDevId(devId, map);
    }

    /**
     * 初始化辅助设备
     * 根据唯一标识 auxiliaryDeviceId 将辅助设备重置为初始状态：
     * device_id 设置为 "admin"，state 设置为 "false"
     * @param auxiliaryDeviceId 辅助设备唯一标识码
     * @return 操作结果
     */
    @PostMapping("/device/auxiliary/init")
    public Result init(@RequestParam("auxiliaryDeviceId") String auxiliaryDeviceId) {
        return auxiliaryDeviceService.initAuxiliaryDevice(auxiliaryDeviceId);
    }
}