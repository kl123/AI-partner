package com.example.aipartner.pojo.IndividualPlaning;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgePoints {
  @JsonProperty("concept_id")
  private Long conceptId;
  @JsonProperty("day_num")
  private Long dayNum;
  @JsonProperty("display_name")
  private String displayName;
  private String description;
  private Long difficulty;
  private String subject;
  @JsonProperty("path_id")
  private Long pathId;
  private Long index;
  private double proficiency;
  private LocalDateTime lastInteracted;




}
