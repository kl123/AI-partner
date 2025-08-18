package com.example.aipartner.controller;

import com.example.aipartner.pojo.result.Result;
import com.example.aipartner.service.IndividualStudyPlanningService;
import com.example.aipartner.utils.jwt.JWTUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
}
