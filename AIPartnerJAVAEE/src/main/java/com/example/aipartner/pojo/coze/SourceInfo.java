package com.example.aipartner.pojo.coze;

import lombok.Data;

/**
 * 文件的元数据信息
 */
@Data
public class SourceInfo {
    /**
     * 本地文件的 Base64 编码
     */
    private String file_base64;
    
    /**
     * 本地文件格式，即文件后缀，例如 txt。格式支持 pdf、txt、doc、docx 类型
     */
    private String file_type;
    
    /**
     * 文档来源（可选字段）
     */
    private String document_source;
}