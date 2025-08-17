package com.example.aipartner.utils.jwt;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(InterceptorConfig.class);
    @Bean
    public JWTInterceptors jwtInterceptors() {
        return new JWTInterceptors();
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        logger.info("JWT拦截器已成功部署，开始拦截请求");
        registry.addInterceptor(new JWTInterceptors())
                .addPathPatterns("/**")  // 全局拦截所有接口
                .excludePathPatterns("/user/login", "/user/register", "/static/**");  // 排除登录、注册、静态资源等接口
    }
}
