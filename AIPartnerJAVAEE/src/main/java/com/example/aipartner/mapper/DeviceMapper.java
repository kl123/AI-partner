package com.example.aipartner.mapper;

import com.example.aipartner.pojo.device.Device;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DeviceMapper {

    void insert(Device device);

    Device findById(String devId);

}