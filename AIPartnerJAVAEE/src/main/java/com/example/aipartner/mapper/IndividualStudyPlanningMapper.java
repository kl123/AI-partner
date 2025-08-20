package com.example.aipartner.mapper;

import com.example.aipartner.pojo.KnowledgePoints;
import com.example.aipartner.pojo.LearningPaths;
import com.example.aipartner.pojo.LearningPathsAndKnowledgePoints;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface IndividualStudyPlanningMapper {


    /**
     * 创建个人学习计划
     * @param learningPaths
     * @return
     */
    void createIndividualStudyPlanning(LearningPaths learningPaths);

    /**
     * 创建知识点
     * @param knowledgePoints
     * @param pathId
     */
    void createKnowledgePoints(@Param("list") List<KnowledgePoints> knowledgePoints, @Param("pathId") Long pathId);

    List<LearningPathsAndKnowledgePoints> GetLearnPlaning(Long userId);

    List<LearningPaths> listLearnPlaning(Long userId);

    List<KnowledgePoints> listKnowledgePoints(Long pathId, Long userId);
}
