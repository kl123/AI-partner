package com.example.aipartner.pojo.coze;

import lombok.Data;
import java.util.List;

/**
 * 扣子文档创建请求体
 */
@Data
public class CozeDocumentRequest {
    /**
     * 数据集ID，固定值
     */
    private String dataset_id = "7556464338367414282";
    
    /**
     * 文档基础信息列表
     */
    private List<DocumentBase> document_bases;
    
    /**
     * 分块策略
     */
    private ChunkStrategy chunk_strategy;
    
    /**
     * 知识库类型。取值包括：0：文本类型；2：图片类型
     */
    private Integer format_type;
}