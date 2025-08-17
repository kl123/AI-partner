package com.example.aipartner.controller;


import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.aipartner.pojo.Result;
import com.example.aipartner.pojo.User;
import com.example.aipartner.utils.jwt.JWTUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@Slf4j
@RestController
public class test {
    @GetMapping("/test")
    public Result test() {
        return Result.success("测试成功");
    }

    @PostMapping("/user/login")
    public Result login(@RequestBody User user) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Map<String, String> map = BeanUtils.describe(user);
        String token = JWTUtils.getToken(map);
        return Result.success(token);
    }
}
