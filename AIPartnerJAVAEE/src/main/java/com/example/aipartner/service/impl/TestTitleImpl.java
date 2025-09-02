package com.example.aipartner.service.impl;

import com.example.aipartner.mapper.TestTitleMapper;
import com.example.aipartner.pojo.ErrorQuestions;
import com.example.aipartner.pojo.TestTitle.Tests;
import com.example.aipartner.pojo.TestTitle.Title;
import com.example.aipartner.pojo.result.Result;
import com.example.aipartner.service.TestTitleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TestTitleImpl implements TestTitleService {
    @Autowired
    private TestTitleMapper testTitleMapper;
    @Override
    public Result addWrongQuestion(ErrorQuestions errorQuestion, Map<String, String> map) {
        String userId = map.get("userId");
        testTitleMapper.addWrongQuestion(errorQuestion, userId);
        return Result.success();
    }

    @Override
    public Result listWrongQuestions(Map<String, String> map) {
        String userId = map.get("userId");
        List<ErrorQuestions> errorQuestionsList = testTitleMapper.listWrongQuestions(userId);
        return Result.success(errorQuestionsList);
    }

    @Override
    public Result listTestsAll(Map<String, String> map) {
        String userId = map.get("userId");
        List<Tests> Tests = testTitleMapper.listTestsAll(userId);
        return Result.success(Tests);
    }

    @Override
    public Result listTitleByTestId(Map<String, Long> request, Map<String, String> map) {
        Long testId = request.get("testId");
        String userId = map.get("userId");
        List<Title> titles = testTitleMapper.listTitleByTestId(testId,userId);
        return Result.success(titles);
    }
}
