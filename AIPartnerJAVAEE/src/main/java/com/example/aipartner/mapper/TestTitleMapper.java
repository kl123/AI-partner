package com.example.aipartner.mapper;

import com.example.aipartner.pojo.ErrorQuestions;
import com.example.aipartner.pojo.TestTitle.Tests;
import com.example.aipartner.pojo.TestTitle.Title;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TestTitleMapper {
    void addWrongQuestion(@Param("errorQuestion") ErrorQuestions errorQuestion, @Param("userId") String userId);

    List<ErrorQuestions> listWrongQuestions(String userId);

    List<Tests> listTestsAll(String userId);

    List<Title> listTitleByTestId(@Param("testId") Long testId, @Param("userId") String userId);



}
