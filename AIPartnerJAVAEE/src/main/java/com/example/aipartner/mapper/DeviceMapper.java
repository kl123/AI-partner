package com.example.aipartner.mapper;

import com.example.aipartner.pojo.device.Device;
import com.example.aipartner.pojo.device.DeviceInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface DeviceMapper {

    void insert(Device device);

    Device findById(String devId);

    void updateBinding(@Param("devId") String devId, @Param("userid") Integer userid, @Param("password") String password);

    String findDevId(@Param("nowUserId") String nowUserId, @Param("nowPassword") String nowPassword, @Param("nowUsername") String nowUsername);

    List<Device> listByUserId(String userId);

    List<DeviceInfo> listInfoByUserId(String userId);

    int updateDeviceNameUser(@Param("deviceId") String deviceId, @Param("deviceNameUser") String deviceNameUser);
}