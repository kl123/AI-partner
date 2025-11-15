package com.example.aipartner.service;

import org.apache.ibatis.annotations.Param;

public interface DeviceDataService {

    void fetchDataAndProcess();

    boolean syncDeviceData();

    void setAuth(String userId, String password);

    boolean bindDevice(String devId, String username, String password);

    boolean initDevice(String devId, String username, String password);

    String findDevId(@Param("nowUserId") String nowUserId, @Param("nowPassword") String nowPassword, @Param("nowUsername") String nowUsername);
}