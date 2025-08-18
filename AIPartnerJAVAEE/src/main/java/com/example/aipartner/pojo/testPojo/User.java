package com.example.aipartner.pojo.testPojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String username;
    private String password;
}
