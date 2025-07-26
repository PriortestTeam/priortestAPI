
package com.hu.oneclick.model.domain.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 执行详情DTO
 */
@Data
public class ExecutionDetailDto {
    
    /**
     * 测试用例ID
     */
    private Long testCaseId;
    
    /**
     * 测试用例标题
     */
    private String testCaseTitle;
    
    /**
     * 测试用例版本
     */
    private String version;
    
    /**
     * 是否已执行
     */
    private Boolean executed;
    
    /**
     * 执行次数
     */
    private Integer executionCount;
    
    /**
     * 最后执行时间
     */
    private LocalDateTime lastExecutionTime;
    
    /**
     * 测试周期执行历史
     */
    private List<TestCycleExecutionDto> testCycleExecutions;
}
