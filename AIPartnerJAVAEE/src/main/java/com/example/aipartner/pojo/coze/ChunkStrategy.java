package com.example.aipartner.pojo.coze;

import lombok.Data;

/**
 * 分块策略
 */
@Data
public class ChunkStrategy {
    /**
     * 分块类型，固定值为0
     */
    private Integer chunk_type = 0;
}