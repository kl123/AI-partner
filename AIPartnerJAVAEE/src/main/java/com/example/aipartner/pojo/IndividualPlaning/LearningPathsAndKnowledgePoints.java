package com.example.aipartner.pojo.IndividualPlaning;

import com.example.aipartner.pojo.IndividualPlaning.KnowledgePoints;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LearningPathsAndKnowledgePoints{
    private Long pathId;
    private Long userId;
    private String title;
    private String description;
    private double progress;
    private Long isCompleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    List<KnowledgePoints> knowledgePoints;
}
