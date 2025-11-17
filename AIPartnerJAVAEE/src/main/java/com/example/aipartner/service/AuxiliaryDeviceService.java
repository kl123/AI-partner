package com.example.aipartner.service;

import com.example.aipartner.pojo.device.AuxiliaryDevice;
import com.example.aipartner.pojo.result.Result;

import java.util.Map;

/**
 * 辅助设备服务接口
 * 定义辅助设备的业务方法
 */
public interface AuxiliaryDeviceService {
    /**
     * 通过 device_id 添加辅助设备
     * 逻辑：校验参数 -> 校验设备存在 -> 校验设备归属 -> 插入记录
     * @param auxiliaryDevice 辅助设备实体（包含 deviceId、state、assistDeviceName、imgUrl）
     * @param auth 认证信息（从 Authorization 中解析的 userId 等）
     * @return 处理结果
     */
    Result addAuxiliaryDevice(AuxiliaryDevice auxiliaryDevice, Map<String, String> auth);

    Result listAuxiliaryDevicesByDevId(String deviceId, Map<String, String> auth);

    Result bindAuxiliaryDevice(String auxiliaryDeviceId, String deviceId, Map<String, String> auth);

    Result initAuxiliaryDevice(String auxiliaryDeviceId);
}