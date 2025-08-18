package com.example.aipartner.mapper;

import com.example.aipartner.pojo.KnowledgePoints;
import com.example.aipartner.pojo.LearningPaths;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IndividualStudyPlanningMapper {
    /**
     * 创建个人学习计划
     * @param learningPaths
     */
    void createIndividualStudyPlanning(LearningPaths learningPaths);

    /**
     * 创建知识点
     * @param knowledgePoints
     */
    void createKnowledgePoints(List<KnowledgePoints> knowledgePoints);
}
