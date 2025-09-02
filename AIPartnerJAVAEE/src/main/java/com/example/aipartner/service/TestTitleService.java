package com.example.aipartner.service;

import com.example.aipartner.pojo.ErrorQuestions;
import com.example.aipartner.pojo.result.Result;

import java.util.Map;

public interface TestTitleService {
    Result addWrongQuestion(ErrorQuestions errorQuestion, Map<String, String> map);

    Result listWrongQuestions(Map<String, String> map);

    Result listTestsAll(Map<String, String> map);

    Result listTitleByTestId(Map<String, Long> request, Map<String, String> map);

}
