package com.example.aipartner.service;

import com.example.aipartner.pojo.User.Users;
import com.example.aipartner.pojo.result.Result;

import java.lang.reflect.InvocationTargetException;

public interface UserService {
    Result login(Users users) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException;

    Result register(Users users);
}
