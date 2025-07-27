package com.hu.oneclick.model.domain.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

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
     * 主版本号列表 - 用于过滤测试用例版本
     */
    @NotEmpty(message = "主版本号不能为空")
    private List<String> majorVersion;

    /**
     * 包含的版本列表 - 用于过滤测试周期
     */
    @Schema(description = "包含的版本列表，用于过滤测试周期")
    private List<String> includeVersions;

    /**
     * 测试周期ID数组（可选）
     */
    private List<Long> testCycleIds;
}