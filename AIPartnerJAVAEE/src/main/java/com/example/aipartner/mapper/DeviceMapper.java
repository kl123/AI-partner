package com.example.aipartner.mapper;

import com.example.aipartner.pojo.device.Device;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DeviceMapper {

    void insert(Device device);

    Device findById(String devId);

    void updateBinding(@Param("devId") String devId, @Param("userid") Integer userid, @Param("password") String password);

    String findDevId(@Param("nowUserId") String nowUserId, @Param("nowPassword") String nowPassword, @Param("nowUsername") String nowUsername);
}