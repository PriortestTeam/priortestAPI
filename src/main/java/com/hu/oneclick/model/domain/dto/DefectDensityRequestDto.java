
package com.hu.oneclick.model.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 缺陷密度计算请求DTO
 */
@Data
@Schema(description = "缺陷密度计算请求参数")
public class DefectDensityRequestDto {
    
    @NotBlank(message = "项目ID不能为空")
    @Schema(description = "项目ID", example = "1874424342973054977")
    private String projectId;
    
    @NotBlank(message = "目标版本号不能为空")
    @Schema(description = "目标版本号", example = "1.0.0.0")
    private String majorVersion;
    
    @NotNull(message = "包含版本列表不能为空")
    @Schema(description = "包含的测试周期版本列表", example = "[\"1.0.0.0\"]")
    private List<String> includeVersions;
    
    @Schema(description = "特定测试周期ID列表(可选)", example = "[\"12934858693434\"]")
    private List<String> testCycleIds;
    
    @Schema(description = "计算方式: CASE_BASED(基于用例), EXECUTION_BASED(基于执行次数), WEIGHTED(加权计算)", 
            example = "CASE_BASED", defaultValue = "CASE_BASED")
    private String calculationType = "CASE_BASED";
    
    @Schema(description = "是否启用自动去重", defaultValue = "true")
    private Boolean enableDeduplication = true;
    
    @Schema(description = "相似度阈值(0-100)", example = "80", defaultValue = "80")
    private Integer similarityThreshold = 80;
    
    @Schema(description = "环境特定缺陷权重(0.0-1.0)", example = "0.8", defaultValue = "0.8")
    private Double environmentSpecificWeight = 0.8;
}
