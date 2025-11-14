package com.example.aipartner.pojo.api;

import lombok.Data;

@Data
public class BemfaApiResponse {
    private Integer code;
    private String msg;
    private TopicInfoData data;
}