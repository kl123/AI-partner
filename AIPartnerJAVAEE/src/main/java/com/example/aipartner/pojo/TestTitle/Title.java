package com.example.aipartner.pojo.TestTitle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Title {

  private Integer id;
  private Integer userId;
  private Integer testId;
  private String type;
  private String userAnswer;
  private Integer showAnswer;
  private Integer isSubmitted;
  private Integer isCorrect;
  private String text;
  private String analysis;
}
