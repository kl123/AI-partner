package com.example.aipartner.service.impl;

import com.example.aipartner.mapper.UserMapper;
import com.example.aipartner.pojo.Users;
import com.example.aipartner.pojo.result.Result;
import com.example.aipartner.service.UserService;
import com.example.aipartner.utils.jwt.JWTUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public Result login(Users users) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Long userId = userMapper.login(users);
        users.setUserId(userId);
        if (userId == null){
            return Result.error("用户名或密码错误");
        }else {
            Map<String, String> map = BeanUtils.describe(users);
            String token = JWTUtils.getToken(map);
            userMapper.updateLastLoginTime(userId);
            return Result.success(token);
        }
    }

    @Override
    public Result register(Users users) {
        Long userId = userMapper.register(users);
        return Result.success(userId);
    }
}
