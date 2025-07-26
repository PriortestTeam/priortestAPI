
package com.hu.oneclick.model.domain.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 功能执行率报表请求参数DTO
 */
@Data
public class FunctionExecutionRateRequestDto {
    
    /**
     * 项目ID（必填）
     */
    @NotNull(message = "项目ID不能为空")
    private Long projectId;
    
    /**
     * 功能版本号数组（必填，支持多版本）
     */
    @NotEmpty(message = "版本号不能为空")
    private List<String> versions;
    
    /**
     * 测试周期ID数组（可选）
     */
    private List<Long> testCycleIds;
}
