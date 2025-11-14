package com.example.aipartner.pojo.api;

import lombok.Data;

@Data
public class BemfaResponse {
    private Integer code;
    private String msg;
    private DeviceData data;
}
