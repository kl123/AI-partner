package com.example.aipartner.mapper;

import com.example.aipartner.pojo.Users;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    Long login(Users users);

    Long register(Users users);

    void updateLastLoginTime(Long userId);
}
