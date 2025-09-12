package com.example.aipartner.service;

import com.example.aipartner.pojo.IndividualPlaning.UserCourse;
import com.example.aipartner.pojo.result.Result;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface IndividualStudyPlanningService {
    Result Create(Map<String, Object> request, Map<String, String> map);

    Result GetLearnPlaning(Map<String, String> map);

    Result listLearnPlaning(Map<String, String> map);

    Result listKnowledgePoints(Map<String, Long> request, Map<String, String> map);

    Result updateProgressOfTheLearningPath(Map<String, Object> request, Map<String, String> map);

    Result uploadFile(MultipartFile file, Map<String, String> map, String token) throws IOException;

    Result listCourse(Map<String, String> map);

    Result AddCourse(Map<String, List<UserCourse.UserCourse_CourseMap>> request, Map<String, String> map);

    void updateRisk();
}
