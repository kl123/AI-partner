package com.example.aipartner.pojo.device;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceInfo {
    private String devId;
    private Integer userid;
    private Integer id;
    private String imgUrl;
    private String deviceNameUser;
    private String deviceNameOrgin;
}