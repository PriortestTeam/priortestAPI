
package com.hu.oneclick.model.domain.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 测试周期执行DTO
 */
@Data
public class TestCycleExecutionDto {
    
    /**
     * 测试周期ID
     */
    private Long testCycleId;
    
    /**
     * 测试周期标题
     */
    private String testCycleTitle;
    
    /**
     * 测试周期环境
     */
    private String testCycleEnv;
    
    /**
     * 执行时间
     */
    private LocalDateTime executionTime;
    
    /**
     * 执行状态
     */
    private Integer executionStatus;
    
    /**
     * 执行状态文本
     */
    private String executionStatusText;
}
