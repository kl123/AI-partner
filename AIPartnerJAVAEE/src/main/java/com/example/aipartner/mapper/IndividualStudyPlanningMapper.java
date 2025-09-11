package com.example.aipartner.mapper;

import com.example.aipartner.pojo.IndividualPlaning.KnowledgePoints;
import com.example.aipartner.pojo.IndividualPlaning.LearningPaths;
import com.example.aipartner.pojo.IndividualPlaning.LearningPathsAndKnowledgePoints;
import com.example.aipartner.pojo.IndividualPlaning.UserCourse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

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

    void updateLearningPathProgress(Integer pathId, double progress, String userId);

    void updateKnowledgePointsProficiency(Integer conceptId, Integer userId, double proficiency);

    List<KnowledgePoints> listKnowledgePointsByConceptId(Integer conceptId, Integer pathId);


    void createUserCourse(@Param("userCourse") Map<String, List<UserCourse.UserCourse_CourseMap>> userCourse, @Param("userId") String userId);

    UserCourse listCourse(String userId);
}
