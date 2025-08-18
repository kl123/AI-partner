package com.example.aipartner.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Users {

  private Long userId;
  private String username;
  private String password;
  private String avatar;
  private LocalDateTime createdAt;
  private LocalDateTime lastLogin;


}
