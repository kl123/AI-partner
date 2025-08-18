package com.example.aipartner.controller;

import com.example.aipartner.pojo.Users;
import com.example.aipartner.pojo.result.Result;
import com.example.aipartner.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;

/**
 * 用户
 */
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 登录
     * @param users
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     */
    @PostMapping("/user/login")
    public Result login(@RequestBody Users users) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        return userService.login(users);
    }

    /**
     *  注册
     * @param users
     * @return
     */
    @PostMapping("/user/register")
    public Result register(@RequestBody Users users){
        return userService.register(users);
    }
}
