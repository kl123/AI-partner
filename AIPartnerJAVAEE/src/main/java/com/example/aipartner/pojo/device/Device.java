package com.example.aipartner.pojo.device;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Device {
    private String devId;
    private Integer userid;
    @JsonIgnore
    private String password;
}
