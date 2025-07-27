package com.hu.oneclick.model.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * 缺陷密度计算响应DTO
 */
@Data
@Schema(description = "缺陷密度计算结果")
public class DefectDensityResponseDto {

    @Schema(description = "缺陷密度值(%)", example = "12.5")
    private Double defectDensity;

    @Schema(description = "质量等级", example = "良好")
    private String qualityLevel;

    @Schema(description = "计算方式", example = "CASE_BASED")
    private String calculationType;

    @Schema(description = "统计数据")
    private StatisticsData statistics;

    @Schema(description = "缺陷详情列表")
    private List<DefectDetail> defectDetails;

    @Schema(description = "计算配置")
    private CalculationConfig config;

    @Data
    @Schema(description = "统计数据")
    public static class StatisticsData {
        @Schema(description = "独立测试用例数 - 参与测试的不重复测试用例总数", example = "50")
        private Integer uniqueTestCases;

        @Schema(description = "总执行次数 - 所有测试用例的执行次数总和(包括重复执行)", example = "120")
        private Integer totalExecutions;

        @Schema(description = "测试周期数 - 涉及的测试周期总数", example = "3")
        private Integer totalCycles;

        @Schema(description = "独立缺陷数 - 去重后的缺陷数量(同一缺陷在多环境下只计算1次)", example = "8")
        private Integer uniqueDefects;

        @Schema(description = "环境特定缺陷数 - 在特定环境下才出现的缺陷数量", example = "5")
        private Integer environmentSpecificDefects;

        @Schema(description = "缺陷实例总数 - 所有缺陷实例的总数(同一缺陷在不同环境下分别计算)", example = "13")
        private Integer totalDefectInstances;

        @Schema(description = "发现缺陷的环境数 - 至少发现1个缺陷的测试环境数量", example = "2")
        private Integer environmentsWithDefects;

        @Schema(description = "总测试环境数 - 参与测试的所有环境总数", example = "3")
        private Integer totalEnvironments;

        @Schema(description = "环境覆盖率(%) - 发现缺陷的环境数/总环境数*100", example = "66.7")
        private Double environmentCoverage;

        @Schema(description = "是否有有效数据 - 判断是否有足够的数据进行分析", example = "true")
        private boolean hasValidData;

        @Schema(description = "数据说明 - 解释当前统计结果的含义", example = "数据充足，分析结果可信")
        private String dataExplanation;

        public boolean isHasValidData() {
            return hasValidData;
        }
    }

    @Data
    @Schema(description = "缺陷详情")
    public static class DefectDetail {
        @Schema(description = "缺陷ID", example = "123")
        private String defectId;

        @Schema(description = "缺陷标题", example = "登录功能异常")
        private String defectTitle;

        @Schema(description = "缺陷描述")
        private String defectDescription;

        @Schema(description = "严重程度", example = "严重")
        private String severity;

        @Schema(description = "优先级", example = "高")
        private String priority;

        @Schema(description = "缺陷状态", example = "已修复")
        private String defectStatus;

        @Schema(description = "发现环境", example = "测试环境")
        private String environment;

        @Schema(description = "浏览器", example = "Chrome")
        private String browser;

        @Schema(description = "测试设备", example = "Windows")
        private String testDevice;

        @Schema(description = "创建时间", example = "2024-01-15 10:30:00")
        private String createTime;

        @Schema(description = "是否为环境特定缺陷")
        private Boolean isEnvironmentSpecific;

        @Schema(description = "关联的测试用例信息")
        private List<RelatedTestCase> relatedTestCases;
    }

    @Data
    @Schema(description = "关联的测试用例信息")
    public static class RelatedTestCase {
        @Schema(description = "测试用例ID", example = "456")
        private String testCaseId;

        @Schema(description = "测试用例标题", example = "用户登录正常流程")
        private String testCaseTitle;

        @Schema(description = "测试用例版本", example = "1.0.0.0")
        private String testCaseVersion;

        @Schema(description = "测试周期ID", example = "789")
        private String testCycleId;

        @Schema(description = "测试周期标题", example = "第一轮系统测试")
        private String testCycleTitle;

        @Schema(description = "测试周期环境", example = "测试环境")
        private String testCycleEnv;

        @Schema(description = "测试周期版本", example = "1.0.0.0")
        private String testCycleVersion;

        @Schema(description = "执行状态", example = "失败")
        private String executionStatus;

        @Schema(description = "执行时间", example = "2024-01-15 14:20:00")
        private String executionTime;

        @Schema(description = "运行次数", example = "2")
        private Integer runCount;

        @Schema(description = "RunCase ID", example = "101112")
        private String runCaseId;
    }

    @Data
    @Schema(description = "计算配置")
    public static class CalculationConfig {
        @Schema(description = "是否启用自动去重")
        private Boolean enableDeduplication;

        @Schema(description = "相似度阈值")
        private Integer similarityThreshold;

        @Schema(description = "环境特定缺陷权重")
        private Double environmentSpecificWeight;

        @Schema(description = "查询条件")
        private Map<String, Object> queryConditions;
    }
}