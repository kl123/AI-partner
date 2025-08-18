package com.example.aipartner.service;

import com.example.aipartner.pojo.result.Result;

import java.util.Map;

public interface IndividualStudyPlanningService {
    Result Create(Map<String, Object> request, Map<String, String> map);
}
