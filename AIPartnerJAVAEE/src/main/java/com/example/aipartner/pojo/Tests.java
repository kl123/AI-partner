package com.example.aipartner.pojo;

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
  private String title;
  private double score;
  private LocalDateTime testTime;
  private Long duration;
  private String testData;


}
