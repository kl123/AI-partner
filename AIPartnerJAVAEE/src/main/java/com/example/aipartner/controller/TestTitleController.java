package com.example.aipartner.controller;

import com.example.aipartner.pojo.ErrorQuestions;
import com.example.aipartner.pojo.result.Result;
import com.example.aipartner.service.TestTitleService;
import com.example.aipartner.utils.jwt.JWTUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 题目管理
 */
@RestController
public class TestTitleController {
    @Autowired
    private TestTitleService testTitleService;

    /**
     * 批量添加错题
     *
     * @param errorQuestionsList
     * @param httpServletRequest
     * @return
     */
    @PostMapping("/title/addErrorTitle")
    public Result addWrongQuestion(@RequestBody List<ErrorQuestions> errorQuestionsList, HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("Authorization");
        Map<String, String> map = JWTUtils.getTokenInfo(token);
        for (ErrorQuestions errorQuestion : errorQuestionsList) {
            testTitleService.addWrongQuestion(errorQuestion, map);
        }
        return Result.success();
    }

    /**
     * 获取错题
     *
     * @param httpServletRequest
     * @return
     */
    @GetMapping("/title/listErrorTitles")
    public Result listWrongQuestions(HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("Authorization");
        Map<String, String> map = JWTUtils.getTokenInfo(token);
        return testTitleService.listWrongQuestions(map);
    }

    /**
     * 获取所有测试
     *
     * @param httpServletRequest
     * @return
     */
    @GetMapping("/title/listTestsAll")
    public Result listTestsAll(HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("Authorization");
        Map<String, String> map = JWTUtils.getTokenInfo(token);
        return testTitleService.listTestsAll(map);
    }

    /**
     * 根据testId获取题目
     * @param request
     * @param httpServletRequest
     * @return
     */
    @PostMapping("/title/listTitlesByTestId")
    public Result listTitleByTestId(@RequestBody Map<String, Long> request, HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("Authorization");
        Map<String, String> map = JWTUtils.getTokenInfo(token);
        return testTitleService.listTitleByTestId(request, map);
    }

//    @PostMapping("/title/addTestAndTitlesList")

}
