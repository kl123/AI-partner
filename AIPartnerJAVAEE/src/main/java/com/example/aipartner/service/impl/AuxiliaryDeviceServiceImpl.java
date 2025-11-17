package com.example.aipartner.service.impl;

import com.example.aipartner.mapper.AuxiliaryDeviceMapper;
import com.example.aipartner.mapper.DeviceMapper;
import com.example.aipartner.pojo.device.AuxiliaryDevice;
import com.example.aipartner.pojo.device.Device;
import com.example.aipartner.pojo.result.Result;
import com.example.aipartner.service.AuxiliaryDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.List;

/**
 * 辅助设备服务实现
 * 负责业务校验与调用持久层完成数据入库
 */
@Service
public class AuxiliaryDeviceServiceImpl implements AuxiliaryDeviceService {

    @Autowired
    private AuxiliaryDeviceMapper auxiliaryDeviceMapper;

    @Autowired
    private DeviceMapper deviceMapper;

    @Override
    public Result addAuxiliaryDevice(AuxiliaryDevice auxiliaryDevice, Map<String, String> auth) {
        // 1. 基本参数校验
        if (auxiliaryDevice == null) {
            return Result.error("参数为空");
        }
        String devId = auxiliaryDevice.getDeviceId();
        if (devId == null || devId.isEmpty()) {
            return Result.error("device_id必填");
        }

        // 2. 校验设备是否存在
        Device device = deviceMapper.findById(devId);
        if (device == null) {
            return Result.error("设备不存在");
        }

        // 3. 校验设备归属权限（当前用户必须是设备的绑定用户）
        try {
            String userIdStr = auth != null ? auth.get("userId") : null;
            if (userIdStr != null && device.getUserid() != null) {
                try {
                    Integer userId = Integer.parseInt(userIdStr);
                    if (!userId.equals(device.getUserid())) {
                        return Result.error("无权操作该设备");
                    }
                } catch (NumberFormatException ignored) {}
            }
        } catch (Exception ignored) {}

        // 4. 入库
        auxiliaryDeviceMapper.insert(auxiliaryDevice);
        return Result.success();
    }

    @Override
    public Result listAuxiliaryDevicesByDevId(String deviceId, Map<String, String> auth) {
        if (deviceId == null || deviceId.isEmpty()) {
            return Result.error("device_id必填");
        }
        Device device = deviceMapper.findById(deviceId);
        if (device == null) {
            return Result.error("设备不存在");
        }
        try {
            String userIdStr = auth != null ? auth.get("userId") : null;
            if (userIdStr != null && device.getUserid() != null) {
                Integer userId = Integer.parseInt(userIdStr);
                if (!userId.equals(device.getUserid())) {
                    return Result.error("无权操作该设备");
                }
            }
        } catch (Exception ignored) {}
        List<AuxiliaryDevice> list = auxiliaryDeviceMapper.listByDeviceId(deviceId);
        return Result.success(list);
    }

    @Override
    public Result bindAuxiliaryDevice(String auxiliaryDeviceId, String deviceId, Map<String, String> auth) {
        if (auxiliaryDeviceId == null || auxiliaryDeviceId.isEmpty()) {
            return Result.error("auxiliary_device_id必填");
        }
        if (deviceId == null || deviceId.isEmpty()) {
            return Result.error("device_id必填");
        }
        Device device = deviceMapper.findById(deviceId);
        if (device == null) {
            return Result.error("设备不存在");
        }
        try {
            String userIdStr = auth != null ? auth.get("userId") : null;
            if (userIdStr != null && device.getUserid() != null) {
                Integer userId = Integer.parseInt(userIdStr);
                if (!userId.equals(device.getUserid())) {
                    return Result.error("无权操作该设备");
                }
            }
        } catch (Exception ignored) {}

        AuxiliaryDevice aux = auxiliaryDeviceMapper.findByAuxiliaryDeviceId(auxiliaryDeviceId);
        if (aux == null) {
            return Result.error("辅助设备不存在");
        }
        int updated = auxiliaryDeviceMapper.bindToDeviceByAuxId(auxiliaryDeviceId, deviceId);
        if (updated <= 0) {
            return Result.error("绑定失败");
        }
        return Result.success();
    }

    @Override
    public Result initAuxiliaryDevice(String auxiliaryDeviceId) {
        if (auxiliaryDeviceId == null || auxiliaryDeviceId.isEmpty()) {
            return Result.error("auxiliary_device_id必填");
        }
        AuxiliaryDevice aux = auxiliaryDeviceMapper.findByAuxiliaryDeviceId(auxiliaryDeviceId);
        if (aux == null) {
            return Result.error("辅助设备不存在");
        }
        int updated = auxiliaryDeviceMapper.initByAuxiliaryDeviceId(auxiliaryDeviceId);
        if (updated <= 0) {
            return Result.error("初始化失败");
        }
        return Result.success();
    }
}