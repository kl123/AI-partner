package com.example.aipartner.service.impl;

import com.example.aipartner.pojo.coze.CozeDocumentRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * 扣子文档服务
 */
@Service
public class CozeDocumentService {

    private static final String COZE_API_URL = "https://api.coze.cn/open_api/knowledge/document/create";
    private static final String AUTHORIZATION = "pat_ZAqhamKIGcIboRpj757LeVuaVfTzwUMMBszlq1fXipR1FyI53SnulOcTiKDaTJ8M";

    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private RestTemplate restTemplate;

    /**
     * 创建扣子文档
     * @param request 文档创建请求
     * @return API响应结果
     */
    public Object createDocument(CozeDocumentRequest request) {
        try {
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + AUTHORIZATION);
            headers.set("Content-Type", "application/json");
            headers.set("Agw-Js-Conv", "str");
            
            // 将请求对象转换为JSON字符串
            String requestBody = objectMapper.writeValueAsString(request);
            
            // 创建HTTP实体
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            
            // 发送POST请求
            ResponseEntity<String> response = restTemplate.exchange(
                COZE_API_URL,
                HttpMethod.POST,
                entity,
                String.class
            );
            
            // 将响应JSON字符串转换为对象并返回
            return objectMapper.readValue(response.getBody(), Object.class);
            
        } catch (Exception e) {
            throw new RuntimeException("调用扣子API失败: " + e.getMessage(), e);
        }
    }
}