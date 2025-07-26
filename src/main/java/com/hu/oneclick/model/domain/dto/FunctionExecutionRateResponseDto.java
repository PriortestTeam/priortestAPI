
package com.hu.oneclick.model.domain.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 功能执行率报表响应DTO
 */
@Data
public class FunctionExecutionRateResponseDto {
    
    /**
     * 查询的版本列表
     */
    private List<String> versions;
    
    /**
     * 计划测试用例总数
     */
    private Integer totalPlannedCount;
    
    /**
     * 实际执行数量
     */
    private Integer actualExecutedCount;
    
    /**
     * 执行率百分比
     */
    private BigDecimal executionRate;
    
    /**
     * 详细执行信息
     */
    private List<ExecutionDetailDto> executionDetails;
}
