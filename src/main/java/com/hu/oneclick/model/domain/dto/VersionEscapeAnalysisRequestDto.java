
package com.hu.oneclick.model.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 版本缺陷逃逸率分析请求DTO
 */
@Data
@Schema(description = "版本缺陷逃逸率分析请求参数")
public class VersionEscapeAnalysisRequestDto {
    
    @NotBlank(message = "项目ID不能为空")
    @Schema(description = "项目ID", example = "1874424342973054977")
    private String projectId;
    
    @NotBlank(message = "分析版本不能为空")
    @Schema(description = "要分析的版本号（引入版本）", example = "0.9.0.0")
    private String analysisVersion;
    
    @Schema(description = "分析时间范围开始日期", example = "2024-01-01")
    private String startDate;
    
    @Schema(description = "分析时间范围结束日期", example = "2024-12-31")
    private String endDate;
    
    @Schema(description = "是否包含遗留缺陷分析", defaultValue = "true")
    private Boolean includeLegacyAnalysis = true;
    
    @Schema(description = "是否按严重程度分组", defaultValue = "true")
    private Boolean groupBySeverity = true;
    
    @Schema(description = "是否按发现版本分组", defaultValue = "true")
    private Boolean groupByFoundVersion = true;
    
    @Schema(description = "缺陷状态过滤列表（可选）", example = "[\"已关闭\", \"已解决\"]")
    private List<String> includeStatuses;
    
    @Schema(description = "严重程度过滤列表（可选）", example = "[\"致命\", \"严重\", \"一般\", \"轻微\"]")
    private List<String> includeSeverities;
}
