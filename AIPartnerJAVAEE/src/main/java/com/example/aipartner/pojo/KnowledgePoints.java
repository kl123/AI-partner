package com.example.aipartner.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgePoints {

  private Long conceptId;
  private Long dayNum;
  private String displayName;
  private String description;
  private Long difficulty;
  private String subject;
  private Long pathId;


}
