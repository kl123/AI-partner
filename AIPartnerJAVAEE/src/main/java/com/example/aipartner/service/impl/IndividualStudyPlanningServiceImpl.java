package com.example.aipartner.service.impl;

import com.example.aipartner.mapper.IndividualStudyPlanningMapper;
import com.example.aipartner.pojo.KnowledgePoints;
import com.example.aipartner.pojo.LearningPaths;
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
        individualStudyPlanningMapper.createKnowledgePoints(knowledgePoints);
        return null;
    }
}
