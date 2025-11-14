package com.example.aipartner.pojo.monitor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 学习监测记录（累计结果）
 *
 * totalTime: 累计总时长（分钟）
 * *_Time: 累计时长，HH:MM 格式
 * devId: 设备编号
 * *_Ratio: 对应占比（0~1）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudyMonitor {
    private Integer id;
    private Double totalTime;
    private String sleepTime;
    private String studyTime;
    private String walkTime;
    private String phoneTime;
    private String attentionTime;
    private String devId;
    private Double studyRatio;
    private Double sleepRadio;
    private Double attentionRadio;
    private Boolean endIf;
}