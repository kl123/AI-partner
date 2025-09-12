package com.example.aipartner.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.example.aipartner.pojo.IndividualPlaning.KnowledgePoints;

import org.springframework.stereotype.Component;

/**
 * 风险评估工具类
 */
@Component
public class RiskCalculator {

    /**
     * 计算知识点列表的总体风险评分
     * @param knowledgePoints 知识点列表
     * @return 总体风险评分（0-1）
     */
    public double calculateOverallRisk(List<KnowledgePoints> knowledgePoints) {
        if (knowledgePoints == null || knowledgePoints.isEmpty()) {
            return 0.0;
        }

        double totalRisk = 0.0;
        for (KnowledgePoints point : knowledgePoints) {
            totalRisk += calculateIndividualRisk(point);
        }

        return totalRisk / knowledgePoints.size();
    }

    /**
     * 计算单个知识点的风险评分
     * @param point 知识点
     * @return 风险评分
     */
    private double calculateIndividualRisk(KnowledgePoints point) {
        // 处理空值特殊情况
        if (point.getLastInteracted() == null) {
            return 0.0;
        }

        // 获取规划时间（默认30天）
        long dayNum = point.getDayNum() != null ? point.getDayNum() : 30;
        
        // 获取难度（默认3）
        long difficulty = point.getDifficulty() != null ? point.getDifficulty() : 3;

        // 计算已用天数
        long daysUsed = ChronoUnit.DAYS.between(
            point.getLastInteracted().toLocalDate(),
            LocalDate.now()
        );

        if (point.getProficiency()==1){
            return 0.0;
        }

        // 计算各因子
        double difficultyFactor = difficulty / 5.0; // 难度归一化
        double timeFactor = Math.min(1.0, (double) daysUsed / dayNum); // 时间压力
        double stagnationFactor = Math.min(1.0, (double) daysUsed / 7); // 学习间隔

        // 加权计算
        return 0.4 * difficultyFactor + 0.3 * timeFactor + 0.3 * stagnationFactor;
    }
}
