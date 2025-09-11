package com.example.aipartner.service.impl;

import com.example.aipartner.mapper.IndividualStudyPlanningMapper;
import com.example.aipartner.pojo.IndividualPlaning.KnowledgePoints;
import com.example.aipartner.pojo.IndividualPlaning.LearningPaths;
import com.example.aipartner.pojo.IndividualPlaning.LearningPathsAndKnowledgePoints;
import com.example.aipartner.pojo.IndividualPlaning.UserCourse;
import com.example.aipartner.pojo.result.Result;
import com.example.aipartner.service.IndividualStudyPlanningService;
import com.example.aipartner.utils.AliOSSUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class IndividualStudyPlanningServiceImpl implements IndividualStudyPlanningService {
    @Autowired
    private IndividualStudyPlanningMapper individualStudyPlanningMapper;

    @Autowired
    private AliOSSUtils aliOSSUtils;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Result Create(Map<String, Object> request, Map<String, String> map) {
        List<KnowledgePoints> knowledgePoints = (List<KnowledgePoints>) request.get("all_nodes");
        Object object = request.get("learn_path");
        LearningPaths learningPaths = new ObjectMapper().convertValue(object, LearningPaths.class);
        Long userId = Long.parseLong(map.get("userId"));
        learningPaths.setUserId(userId);
        individualStudyPlanningMapper.createIndividualStudyPlanning(learningPaths);
        individualStudyPlanningMapper.createKnowledgePoints(knowledgePoints, learningPaths.getPathId());
        return Result.success();
    }

    @Override
    public Result GetLearnPlaning(Map<String, String> map) {
        Long UserId = Long.parseLong(map.get("userId"));
        List<LearningPathsAndKnowledgePoints> plans = individualStudyPlanningMapper.GetLearnPlaning(UserId);
        return Result.success(plans);
    }

    @Override
    public Result listLearnPlaning(Map<String, String> map) {
        Long UserId = Long.parseLong(map.get("userId"));
        List<LearningPaths> plans = individualStudyPlanningMapper.listLearnPlaning(UserId);
        return Result.success(plans);
    }

    @Override
    public Result listKnowledgePoints(Map<String, Long> request, Map<String, String> map) {
        Long pathId = request.get("pathId");
        Long UserId = Long.parseLong(map.get("userId"));
        List<KnowledgePoints> plans = individualStudyPlanningMapper.listKnowledgePoints(pathId, UserId);
        return Result.success(plans);
    }

    @Override
    public Result updateProgressOfTheLearningPath(Map<String, Object> request, Map<String, String> map) {
        Integer pathId = (Integer) request.get("pathId");
        double proficiency = (double) request.get("progress");
        Integer conceptId = (Integer) request.get("concept_id");
        String userId = map.get("userId");


        individualStudyPlanningMapper.updateKnowledgePointsProficiency(conceptId, pathId, proficiency);
        List<KnowledgePoints> knowledgePointsList = individualStudyPlanningMapper.listKnowledgePointsByConceptId(conceptId, pathId);
        double proficientCount = sumProficiencyPoints(knowledgePointsList);
        double knowledgePointsListSize = knowledgePointsList.size();
        log.info("proficientCount: {}", proficientCount);
        log.info("knowledgePointsListSize: {}", knowledgePointsListSize);
        double progress = proficientCount / knowledgePointsListSize;
        individualStudyPlanningMapper.updateLearningPathProgress(pathId, progress, userId);
        return Result.success();
    }

    @Override
    public Result uploadFile(MultipartFile file, Map<String, String> map, String token) throws IOException {
        Integer userId = Integer.valueOf(map.get("userId"));


        String imgUrl = aliOSSUtils.upload(file);
        String url = "http://localhost:8085/workflow/classTable";

        Map<String, String> request = new HashMap<>();
        request.put("image", imgUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", token);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> responseBody = objectMapper.readValue(response.getBody(), new TypeReference<Map<String, Object>>() {});
//            UserCourse userCourse = new UserCourse(null,userId,null,responseBody);
//            individualStudyPlanningMapper.createUserCourse(userCourse);
            return Result.success(responseBody);
        } catch (HttpClientErrorException e) {
            log.error("HTTP请求失败: {}", e.getStatusCode(), e);
            return Result.error("请求失败: " + e.getResponseBodyAsString());
        } catch (JsonProcessingException e) {
            log.error("JSON解析失败", e);
            return Result.error("响应数据格式错误");
        } catch (Exception e) {
            log.error("文件上传异常", e);
            return Result.error("上传失败：" + e.getMessage());
        }
    }

    @Override
    public Result listCourse(Map<String, String> map) {
        String userId = map.get("userId");
        UserCourse userCourse = individualStudyPlanningMapper.listCourse(userId);
        return Result.success(userCourse);
    }

    @Override
    public Result AddCourse(Map<String, List<UserCourse.UserCourse_CourseMap>> request, Map<String, String> map) {
        String userId = map.get("userId");
        individualStudyPlanningMapper.createUserCourse(request,userId);
        return Result.success();
    }


    public double sumProficiencyPoints(List<KnowledgePoints> knowledgePointsList) {
        return knowledgePointsList.stream()
            .mapToDouble(KnowledgePoints::getProficiency)
            .sum();
    }

}
