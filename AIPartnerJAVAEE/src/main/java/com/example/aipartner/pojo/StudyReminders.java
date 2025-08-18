package com.example.aipartner.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudyReminders {

  private Long reminderId;
  private Long userId;
  private String content;
  private LocalDateTime triggerTime;
  private Long status;
  private LocalDateTime createdAt;
  private String reminderType;


}
