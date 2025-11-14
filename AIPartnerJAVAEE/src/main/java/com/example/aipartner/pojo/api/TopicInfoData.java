package com.example.aipartner.pojo.api;

import lombok.Data;

@Data
public class TopicInfoData {
    private String name;
    private String msg;
    private Boolean online;
    private Integer onlineNum;
    private Boolean pubOnline;
    private String deviceType;
    private Boolean share;
    private String group;
    private String room;
    private String time;
    private Long unix;
    private String createTime;
}