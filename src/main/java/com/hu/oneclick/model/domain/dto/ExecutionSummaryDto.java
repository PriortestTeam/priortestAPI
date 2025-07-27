
package com.hu.oneclick.model.domain.dto;

import lombok.Data;

/**
 * 执行摘要DTO
 */
@Data
public class ExecutionSummaryDto {
    
    /**
     * 通过数量
     */
    private Integer passCount;
    
    /**
     * 失败数量
     */
    private Integer failCount;
    
    /**
     * 阻塞数量
     */
    private Integer blockedCount;
    
    /**
     * 跳过数量
     */
    private Integer skippedCount;
    
    /**
     * 未执行数量
     */
    private Integer notExecutedCount;
}
