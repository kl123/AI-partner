package com.example.aipartner.service;

import com.example.aipartner.pojo.ErrorQuestions;
import com.example.aipartner.pojo.result.Result;

import java.util.Map;

public interface IndividualStudyPlanningService {
    Result Create(Map<String, Object> request, Map<String, String> map);

    Result GetLearnPlaning(Map<String, String> map);

    Result listLearnPlaning(Map<String, String> map);

    Result listKnowledgePoints(Map<String, Long> request, Map<String, String> map);

    Result updateProgressOfTheLearningPath(Map<String, Object> request, Map<String, String> map);
}
