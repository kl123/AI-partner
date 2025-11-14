package com.example.aipartner.service;

public interface DeviceDataService {

    void fetchDataAndProcess();

    boolean syncDeviceData();

    void setAuth(String userId, String password);

}