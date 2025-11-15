package com.example.aipartner.mapper;

import com.example.aipartner.pojo.monitor.StudyMonitor;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StudyMonitorMapper {

    void insert(StudyMonitor studyMonitor);

    StudyMonitor findLatestByDevId(String devId);

    void update(StudyMonitor studyMonitor);

    void deleteByDevId(String devId);

}