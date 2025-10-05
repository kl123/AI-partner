package com.example.aipartner.pojo.coze;

import lombok.Data;

/**
 * 文档基础信息
 */
@Data
public class DocumentBase {
    /**
     * 文件名称
     */
    private String name;
    
    /**
     * 文件的元数据信息
     */
    private SourceInfo source_info;
}