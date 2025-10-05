package com.example.aipartner.pojo.coze;

import lombok.Data;

/**
 * 扣子文档创建响应体
 */
@Data
public class CozeDocumentResponse {
    /**
     * 响应码
     */
    private Integer code;
    
    /**
     * 响应消息
     */
    private String msg;
    
    /**
     * 响应数据
     */
    private Object data;
    
    /**
     * 请求是否成功
     */
    private Boolean success;
}