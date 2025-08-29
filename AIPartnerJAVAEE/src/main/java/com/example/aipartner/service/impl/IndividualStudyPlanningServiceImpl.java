package com.example.aipartner.service.impl;

import com.example.aipartner.mapper.IndividualStudyPlanningMapper;
import com.example.aipartner.pojo.ErrorQuestions;
import com.example.aipartner.pojo.KnowledgePoints;
import com.example.aipartner.pojo.LearningPaths;
import com.example.aipartner.pojo.LearningPathsAndKnowledgePoints;
import com.example.aipartner.pojo.result.Result;
import com.example.aipartner.service.IndividualStudyPlanningService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class IndividualStudyPlanningServiceImpl implements IndividualStudyPlanningService {
    @Autowired
    private IndividualStudyPlanningMapper individualStudyPlanningMapper;

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
    public Result addWrongQuestion(ErrorQuestions errorQuestion, Map<String, String> map) {
        String userId = map.get("userId");
        individualStudyPlanningMapper.addWrongQuestion(errorQuestion, userId);
        return Result.success();
    }

    @Override
    public Result listWrongQuestions(Map<String, String> map) {
        String userId = map.get("userId");
        List<ErrorQuestions> errorQuestionsList = individualStudyPlanningMapper.listWrongQuestions(userId);
        return Result.success(errorQuestionsList);
    }

    public double sumProficiencyPoints(List<KnowledgePoints> knowledgePointsList) {
        return knowledgePointsList.stream()
            .mapToDouble(KnowledgePoints::getProficiency)
            .sum();
    }

}
