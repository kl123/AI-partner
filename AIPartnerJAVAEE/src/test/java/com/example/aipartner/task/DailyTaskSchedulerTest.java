package com.example.aipartner.task;

import com.example.aipartner.pojo.IndividualPlaning.LearningPathsAndKnowledgePoints;
import com.example.aipartner.service.impl.IndividualStudyPlanningServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class DailyTaskSchedulerTest {
    @Autowired
    private IndividualStudyPlanningServiceImpl individualStudyPlanningService;

    @Test
    void executeDailyTask() {
        individualStudyPlanningService.updateRisk();
    }
}