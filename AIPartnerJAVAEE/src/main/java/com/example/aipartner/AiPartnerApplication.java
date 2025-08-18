package com.example.aipartner;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@MapperScan("com.example.aipartner.mapper")
@ServletComponentScan //开启了SpringBoot对Servlet组件的支持
public class AiPartnerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiPartnerApplication.class, args);
    }

}
