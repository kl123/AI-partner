package com.example.aipartner.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorQuestions {

  private Long errorId;
  private Long userId;
  private String question;
  private String userAnswer;
  private String correctAnswer;
  private String errorReason;
  private LocalDateTime occurrenceTime;
  private Long isResolved;

}
