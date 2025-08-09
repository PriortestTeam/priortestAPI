package com.hu.oneclick.model.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 版本缺陷逃逸率分析响应DTO
 */
@Data
@Schema(description = "版本缺陷逃逸率分析响应数据")
public class VersionEscapeAnalysisResponseDto {

    @Schema(description = "分析的版本号")
    private String analysisVersion;

    @Schema(description = "项目ID")
    private String projectId;

    @Schema(description = "分析时间范围")
    private String analysisTimeRange;

    @Schema(description = "逃逸率统计")
    private EscapeRateStats escapeRateStats;

    @Schema(description = "缺陷发现时机分析")
    private DiscoveryTimingAnalysis discoveryTiming;

    @Schema(description = "遗留缺陷分析")
    private LegacyDefectAnalysis legacyDefectAnalysis;

    @Schema(description = "按发现版本分组的详情")
    private List<VersionDefectGroup> versionGroups;

    @Schema(description = "按严重程度分组的详情")
    private List<SeverityDefectGroup> severityGroups;

    @Schema(description = "详细缺陷列表")
    private List<EscapeDefectDetail> defectDetails;

    @Schema(description = "质量评估和改进建议")
    private QualityAssessment qualityAssessment;

    /**
     * 逃逸率统计
     */
    @Data
    @Schema(description = "逃逸率统计数据")
    public static class EscapeRateStats {
        @Schema(description = "总缺陷数（该版本引入的所有缺陷）")
        private Integer totalDefectsIntroduced;

        @Schema(description = "当前版本发现的缺陷数")
        private Integer currentVersionFound;

        @Schema(description = "逃逸缺陷数（后续版本才发现）")
        private Integer escapedDefects;

        @Schema(description = "缺陷逃逸率（%）")
        private BigDecimal escapeRate;

        @Schema(description = "测试有效性（%）")
        private BigDecimal detectionEffectiveness;

        @Schema(description = "质量等级评价")
        private String qualityLevel;
    }

    /**
     * 缺陷发现时机分析
     */
    @Data
    @Schema(description = "缺陷发现时机分析")
    public static class DiscoveryTimingAnalysis {
        @Schema(description = "版本内发现的缺陷数")
        private Integer inVersionCount;

        @Schema(description = "版本内发现的缺陷比例（%）")
        private BigDecimal inVersionPercentage;

        @Schema(description = "逃逸的缺陷数")
        private Integer escapedCount;

        @Schema(description = "逃逸的缺陷比例（%）")
        private BigDecimal escapedPercentage;

        @Schema(description = "分析描述")
        private String description;
    }

    /**
     * 发现时机分布
     */
    @Data
    @Schema(description = "发现时机分布")
    public static class TimingDistribution {
        @Schema(description = "时机类型（版本内/发布前/发布后）")
        private String timingType;

        @Schema(description = "缺陷数量")
        private Integer count;

        @Schema(description = "占比（%）")
        private BigDecimal percentage;

        @Schema(description = "描述")
        private String description;
    }

    /**
     * 遗留缺陷分析
     */
    @Data
    @Schema(description = "遗留缺陷分析")
    public static class LegacyDefectAnalysis {
        @Schema(description = "遗留缺陷总数")
        private Integer totalLegacyDefects;

        @Schema(description = "遗留缺陷率（%）")
        private BigDecimal legacyDefectRate;

        @Schema(description = "平均逃逸天数")
        private Integer averageEscapeDays;

        @Schema(description = "分析描述")
        private String description;

        @Schema(description = "发布前发现的遗留缺陷数")
        private Integer preReleaseLegacyFound;

        @Schema(description = "发布后发现的遗留缺陷数")
        private Integer postReleaseLegacyFound;

        @Schema(description = "遗留缺陷逃逸率（%）")
        private BigDecimal legacyEscapeRate;

        @Schema(description = "遗留缺陷来源版本分析")
        private List<LegacySourceVersion> sourceVersions;
    }

    /**
     * 遗留缺陷来源版本分析
     */
    @Data
    @Schema(description = "遗留缺陷来源版本")
    public static class LegacySourceVersion {
        @Schema(description = "来源版本")
        private String sourceVersion;

        @Schema(description = "该版本遗留的缺陷数")
        private Integer count;

        @Schema(description = "影响描述")
        private String impactDescription;
    }

    /**
     * 按版本分组的缺陷详情
     */
    @Data
    @Schema(description = "按发现版本分组的缺陷")
    public static class VersionDefectGroup {
        @Schema(description = "发现版本")
        private String foundVersion;

        @Schema(description = "该版本发现的缺陷数")
        private Integer count;

        @Schema(description = "逃逸天数（距离引入版本发布的天数）")
        private Integer escapeDays;

        @Schema(description = "严重程度分布")
        private Map<String, Integer> severityDistribution;

        @Schema(description = "是否为逃逸缺陷")
        private Boolean isEscaped;

        @Schema(description = "影响描述")
        private String impactDescription;
    }

    /**
     * 按严重程度分组的缺陷详情
     */
    @Data
    @Schema(description = "按严重程度分组的缺陷")
    public static class SeverityDefectGroup {
        @Schema(description = "严重程度")
        private String severity;

        @Schema(description = "总数")
        private Integer totalCount;

        @Schema(description = "版本内发现数")
        private Integer inVersionCount;

        @Schema(description = "逃逸数")
        private Integer escapedCount;

        @Schema(description = "该严重程度的逃逸率（%）")
        private BigDecimal escapeRate;

        @Schema(description = "风险评估")
        private String riskAssessment;
    }

    /**
     * 缺陷详情
     */
    @Data
    @Schema(description = "逃逸缺陷详情")
    public static class EscapeDefectDetail {
        @Schema(description = "缺陷ID")
        private String defectId;

        @Schema(description = "缺陷标题")
        private String title;

        @Schema(description = "严重程度")
        private String severity;

        @Schema(description = "优先级")
        private String priority;

        @Schema(description = "引入版本")
        private String introducedVersion;

        @Schema(description = "发现版本")
        private String foundVersion;

        @Schema(description = "逃逸天数")
        private Integer escapeDays;

        @Schema(description = "是否为逃逸缺陷")
        private Boolean isEscaped;

        @Schema(description = "修复状态")
        private String fixStatus;

        @Schema(description = "影响描述")
        private String impactDescription;
    }

    /**
     * 质量评估和改进建议
     */
    @Data
    @Schema(description = "质量评估和改进建议")
    public static class QualityAssessment {
        @Schema(description = "总体质量等级")
        private String overallQualityLevel;

        @Schema(description = "风险等级")
        private String riskLevel;

        @Schema(description = "改进建议")
        private List<String> recommendations;

        @Schema(description = "关键指标")
        private Map<String, BigDecimal> keyMetrics;

        @Schema(description = "测试覆盖评估")
        private String testCoverageAssessment;

        @Schema(description = "关键发现")
        private List<String> keyFindings;
    }

    /**
     * 对比基准
     */
    @Data
    @Schema(description = "对比基准数据")
    public static class ComparisonBenchmark {
        @Schema(description = "行业标准逃逸率（%）")
        private BigDecimal industryStandardEscapeRate;

        @Schema(description = "项目历史平均逃逸率（%）")
        private BigDecimal projectAverageEscapeRate;

        @Schema(description = "相比项目平均的改善情况")
        private String improvementStatus;

        @Schema(description = "排名（在项目历史版本中的排名）")
        private String ranking;
    }
}