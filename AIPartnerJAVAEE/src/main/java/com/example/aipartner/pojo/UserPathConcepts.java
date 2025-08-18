package com.example.aipartner.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPathConcepts {

  private Long userId;
  private Long pathId;
  private Long conceptId;
  private Long index;
  private double proficiency;
  private LocalDateTime lastInteracted;

}
