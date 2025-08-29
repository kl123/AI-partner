package com.example.aipartner.controller;

import com.example.aipartner.pojo.ErrorQuestions;
import com.example.aipartner.pojo.result.Result;
import com.example.aipartner.service.IndividualStudyPlanningService;
import com.example.aipartner.utils.jwt.JWTUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
/**
 * 个人学习计划
 */
@Slf4j
@RestController
public class IndividualStudyPlanningController {

    @Autowired
    private IndividualStudyPlanningService individualStudyPlanningService;

    /**
     * 创建个人学习计划
     * @param request
     * @return
     */
    @PostMapping("/IndividualPlaning/Create")
//    public Result Create(@RequestBody Map<String, List<Map<String,Object>>> request) {
    public Result Create(@RequestBody Map<String, Object> request, HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("Authorization");
        Map<String, String> map = JWTUtils.getTokenInfo(token);
        return individualStudyPlanningService.Create(request,map);
    }

    /**
     * 获取个人学习计划包括知识点（写错了补药管这个）
     * @param httpServletRequest
     * @return
     */
    @GetMapping("/IndividualPlaning/listAll")
    public Result GetLearnPlaning(HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("Authorization");
        Map<String, String> map = JWTUtils.getTokenInfo(token);
        return individualStudyPlanningService.GetLearnPlaning(map);
    }

    /**
     * 获取个人学习计划
     * @param httpServletRequest
     * @return
     */
    @GetMapping("/IndividualPlaning/listLearnPlaning")
    public Result listLearnPlaning(HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("Authorization");
        Map<String, String> map = JWTUtils.getTokenInfo(token);
        return individualStudyPlanningService.listLearnPlaning(map);
    }

    /**
     * 获取个人学习计划下的知识点s
     * @param request
     * @param httpServletRequest
     * @return
     */
    @PostMapping("/IndividualPlaning/listKnowledgePoints")
    public Result listKnowledgePoints(@RequestBody Map<String, Long> request,HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("Authorization");
        Map<String, String> map = JWTUtils.getTokenInfo(token);
        return individualStudyPlanningService.listKnowledgePoints(request,map);
    }

    /**
     * 更新个人学习计划进度
     * @param request
     * @param httpServletRequest
     * @return
     */
    @PostMapping("/IndividualPlaning/updateProgressOfTheLearningPath")
    public Result updateProgressOfTheLearningPath(@RequestBody Map<String, Object> request, HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("Authorization");
        Map<String, String> map = JWTUtils.getTokenInfo(token);
        return individualStudyPlanningService.updateProgressOfTheLearningPath(request,map);
    }


    /**
     * 批量添加错题
     * @param errorQuestionsList
     * @param httpServletRequest
     * @return
     */
    @PostMapping("/IndividualPlaning/addWrongQuestion")
    public Result addWrongQuestion(@RequestBody List<ErrorQuestions> errorQuestionsList, HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("Authorization");
        Map<String, String> map = JWTUtils.getTokenInfo(token);
        for (ErrorQuestions errorQuestion : errorQuestionsList){
            individualStudyPlanningService.addWrongQuestion(errorQuestion,map);
        }
        return Result.success();
    }

    /**
     * 获取错题
     * @param httpServletRequest
     * @return
     */
    @GetMapping("/IndividualPlaning/listWrongQuestions")
    public Result listWrongQuestions(HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("Authorization");
        Map<String, String> map = JWTUtils.getTokenInfo(token);
        return individualStudyPlanningService.listWrongQuestions(map);
    }

    /**
     * 获取所有测试
     * @param httpServletRequest
     * @return
     */
    @GetMapping("/IndividualPlaning/listTestsAll")
    public Result listTestsAll(HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("Authorization");
        Map<String, String> map = JWTUtils.getTokenInfo(token);
        return individualStudyPlanningService.listTestsAll(map);
    }

    /**
     * 根据testId获取题目
     * @param request
     * @param httpServletRequest
     * @return
     */
    @PostMapping("/IndividualPlaning/listTitleByTestId")
    public Result listTitleByTestId(@RequestBody Map<String, Long> request, HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("Authorization");
        Map<String, String> map = JWTUtils.getTokenInfo(token);
        return individualStudyPlanningService.listTitleByTestId(request,map);
    }


}
