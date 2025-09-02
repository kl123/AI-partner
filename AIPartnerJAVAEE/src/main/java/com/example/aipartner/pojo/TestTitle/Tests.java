package com.example.aipartner.pojo.TestTitle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tests {

  private Long testId;
  private Long userId;
  private Long titleId;
  private String title;
  private double score;
  private LocalDateTime testTime;
  private Long duration;
  private String testData;


}
