package com.example.aipartner.mapper;

import com.example.aipartner.pojo.monitor.StudyMonitor;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface StudyMonitorMapper {

    void insert(StudyMonitor studyMonitor);

    StudyMonitor findLatestByDevId(String devId);

    void update(StudyMonitor studyMonitor);

    void deleteByDevId(String devId);

    List<StudyMonitor> listByDevId(String devId);

}