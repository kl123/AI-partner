package com.example.aipartner.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestConcepts {

  private Long id;
  private Long testId;
  private Long conceptId;
  private double coverage;
  private double accuracy;


}
