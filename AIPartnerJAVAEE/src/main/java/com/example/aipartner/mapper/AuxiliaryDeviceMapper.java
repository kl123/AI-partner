package com.example.aipartner.mapper;

import com.example.aipartner.pojo.device.AuxiliaryDevice;
import org.apache.ibatis.annotations.Mapper;

/**
 * 辅助设备 Mapper
 * 提供对辅助设备表的基本数据库操作接口
 */
@Mapper
public interface AuxiliaryDeviceMapper {
    /**
     * 插入一条辅助设备记录
     * @param auxiliaryDevice 辅助设备实体
     */
    void insert(AuxiliaryDevice auxiliaryDevice);

    java.util.List<AuxiliaryDevice> listByDeviceId(String deviceId);

    AuxiliaryDevice findByAuxiliaryDeviceId(String auxiliaryDeviceId);

    int bindToDeviceByAuxId(String auxiliaryDeviceId, String deviceId);

    int initByAuxiliaryDeviceId(String auxiliaryDeviceId);
}