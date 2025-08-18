package com.example.aipartner.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorQuestionConcepts {

  private Long id;
  private Long errorId;
  private Long conceptId;
  private double weight;
  private double accuracy;

}
