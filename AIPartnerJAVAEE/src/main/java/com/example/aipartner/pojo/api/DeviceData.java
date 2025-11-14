package com.example.aipartner.pojo.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceData {
    private Boolean end_if;
    private String sleep_time;
    private String study_time;
    private String walk_time;
    private String phone_time;
    private String attention_time;
    private String id;
}
