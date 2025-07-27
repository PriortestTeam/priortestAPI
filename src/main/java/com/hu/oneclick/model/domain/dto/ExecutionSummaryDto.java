
package com.hu.oneclick.model.domain.dto;

import lombok.Data;
import java.math.BigDecimal;

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
    
    /**
     * 通过率 (%)
     */
    private BigDecimal passRate;
    
    /**
     * 失败率 (%)
     */
    private BigDecimal failRate;
    
    /**
     * 阻塞率 (%)
     */
    private BigDecimal blockedRate;
    
    /**
     * 跳过率 (%)
     */
    private BigDecimal skippedRate;
    
    /**
     * 未执行率 (%)
     */
    private BigDecimal notExecutedRate;
}
