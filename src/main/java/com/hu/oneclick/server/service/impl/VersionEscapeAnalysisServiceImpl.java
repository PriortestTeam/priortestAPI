
package com.hu.oneclick.server.service.impl;

import com.hu.oneclick.dao.IssueDao;
import com.hu.oneclick.model.domain.dto.VersionEscapeAnalysisRequestDto;
import com.hu.oneclick.model.domain.dto.VersionEscapeAnalysisResponseDto;
import com.hu.oneclick.server.service.VersionEscapeAnalysisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 版本缺陷逃逸率分析服务实现类
 */
@Slf4j
@Service
public class VersionEscapeAnalysisServiceImpl implements VersionEscapeAnalysisService {

    @Resource
    private IssueDao issueDao;

    @Override
    public VersionEscapeAnalysisResponseDto analyzeVersionEscapeRate(VersionEscapeAnalysisRequestDto requestDto) {
        log.info("开始分析版本缺陷逃逸率，项目：{}，版本：{}", 
                requestDto.getProjectId(), requestDto.getAnalysisVersion());

        // 构建查询条件
        Map<String, Object> queryParams = buildQueryParams(requestDto);

        // 查询缺陷统计数据
        Map<String, Object> escapeStats = queryEscapeStatistics(queryParams);

        VersionEscapeAnalysisResponseDto responseDto = new VersionEscapeAnalysisResponseDto();
        responseDto.setAnalysisVersion(requestDto.getAnalysisVersion());
        responseDto.setProjectId(requestDto.getProjectId());
        responseDto.setAnalysisTimeRange(buildAnalysisTimeRange(requestDto));

        // 构建逃逸率统计
        VersionEscapeAnalysisResponseDto.EscapeRateStats escapeRateStats = 
                buildEscapeRateStats(escapeStats);
        responseDto.setEscapeRateStats(escapeRateStats);

        // 构建发现时机分析
        VersionEscapeAnalysisResponseDto.DiscoveryTimingAnalysis discoveryTiming = 
                buildDiscoveryTimingAnalysis(escapeRateStats);
        responseDto.setDiscoveryTiming(discoveryTiming);

        // 构建遗留缺陷分析
        VersionEscapeAnalysisResponseDto.LegacyDefectAnalysis legacyDefectAnalysis = 
                buildLegacyDefectAnalysis(escapeRateStats);
        responseDto.setLegacyDefectAnalysis(legacyDefectAnalysis);

        // 初始化其他字段
        responseDto.setVersionGroups(new ArrayList<>());
        responseDto.setSeverityGroups(new ArrayList<>());
        responseDto.setDefectDetails(new ArrayList<>());

        VersionEscapeAnalysisResponseDto.QualityAssessment qualityAssessment = 
                buildQualityAssessment(escapeRateStats);
        responseDto.setQualityAssessment(qualityAssessment);

        log.info("版本缺陷逃逸率分析完成，版本：{}，逃逸率：{}%", 
                requestDto.getAnalysisVersion(), 
                escapeRateStats.getEscapeRate());

        return responseDto;
    }

    @Override
    public Object getEscapeRateTrend(String projectId, List<String> versions) {
        log.info("开始获取逃逸率趋势，项目：{}，版本数量：{}", projectId, versions.size());

        try {
            Map<String, Object> trendData = new HashMap<>();
            List<Map<String, Object>> versionTrends = new ArrayList<>();

            for (String version : versions) {
                Map<String, Object> params = new HashMap<>();
                params.put("projectId", projectId);
                params.put("analysisVersion", version);

                Map<String, Object> stats = queryEscapeStatistics(params);

                Map<String, Object> versionTrend = new HashMap<>();
                versionTrend.put("version", version);
                versionTrend.put("totalDefects", stats.get("totalDefectsIntroduced"));
                versionTrend.put("escapedDefects", stats.get("escapedDefects"));
                versionTrend.put("escapeRate", stats.get("escapeRate"));

                // 计算质量等级
                BigDecimal escapeRate = getBigDecimalValue(stats, "escapeRate");
                String qualityLevel;
                if (escapeRate.compareTo(BigDecimal.valueOf(5.0)) <= 0) {
                    qualityLevel = "优秀";
                } else if (escapeRate.compareTo(BigDecimal.valueOf(15.0)) <= 0) {
                    qualityLevel = "良好";
                } else if (escapeRate.compareTo(BigDecimal.valueOf(30.0)) <= 0) {
                    qualityLevel = "一般";
                } else {
                    qualityLevel = "需改进";
                }
                versionTrend.put("qualityLevel", qualityLevel);

                versionTrends.add(versionTrend);
            }

            trendData.put("versions", versions);
            trendData.put("trendData", versionTrends);

            return trendData;

        } catch (Exception e) {
            log.error("获取逃逸率趋势失败", e);

            Map<String, Object> errorData = new HashMap<>();
            errorData.put("versions", versions);
            errorData.put("trendData", new ArrayList<>());

            return errorData;
        }
    }

    @Override
    public String exportEscapeAnalysisReport(VersionEscapeAnalysisRequestDto requestDto) {
        log.info("开始导出逃逸率分析报告，项目：{}，版本：{}", 
                requestDto.getProjectId(), requestDto.getAnalysisVersion());

        // TODO: 实现报告导出逻辑
        String reportPath = "/tmp/escape_analysis_report_" + System.currentTimeMillis() + ".xlsx";

        return reportPath;
    }

    /**
     * 构建查询参数
     */
    private Map<String, Object> buildQueryParams(VersionEscapeAnalysisRequestDto requestDto) {
        Map<String, Object> params = new HashMap<>();
        params.put("projectId", requestDto.getProjectId());
        params.put("analysisVersion", requestDto.getAnalysisVersion());

        if (StringUtils.hasText(requestDto.getStartDate())) {
            params.put("startDate", requestDto.getStartDate());
        }

        if (StringUtils.hasText(requestDto.getEndDate())) {
            params.put("endDate", requestDto.getEndDate());
        }

        return params;
    }

    /**
     * 查询缺陷逃逸统计数据
     */
    private Map<String, Object> queryEscapeStatistics(Map<String, Object> params) {
        try {
            Map<String, Object> result = issueDao.queryVersionEscapeStatistics(
                (String) params.get("projectId"),
                (String) params.get("analysisVersion"),
                (String) params.get("startDate"),
                (String) params.get("endDate")
            );

            log.info("查询结果: {}", result);
            return result;

        } catch (Exception e) {
            log.error("查询缺陷逃逸统计失败", e);
            // 返回默认值
            Map<String, Object> defaultResult = new HashMap<>();
            defaultResult.put("totalDefectsIntroduced", 0);
            defaultResult.put("currentVersionFound", 0);
            defaultResult.put("escapedDefects", 0);
            defaultResult.put("escapeRate", BigDecimal.ZERO);
            return defaultResult;
        }
    }

    /**
     * 构建逃逸率统计
     */
    private VersionEscapeAnalysisResponseDto.EscapeRateStats buildEscapeRateStats(Map<String, Object> escapeStats) {
        VersionEscapeAnalysisResponseDto.EscapeRateStats stats = 
                new VersionEscapeAnalysisResponseDto.EscapeRateStats();

        stats.setTotalDefectsIntroduced(getIntValue(escapeStats, "totalDefectsIntroduced"));
        stats.setCurrentVersionFound(getIntValue(escapeStats, "currentVersionFound"));
        stats.setEscapedDefects(getIntValue(escapeStats, "escapedDefects"));

        BigDecimal escapeRate = getBigDecimalValue(escapeStats, "escapeRate");
        stats.setEscapeRate(escapeRate.doubleValue());

        // 计算检测有效性
        double detectionEffectiveness = 100.0 - escapeRate.doubleValue();
        stats.setDetectionEffectiveness(detectionEffectiveness);

        // 计算质量等级
        String qualityLevel;
        if (escapeRate.compareTo(BigDecimal.valueOf(5)) <= 0) {
            qualityLevel = "优秀";
        } else if (escapeRate.compareTo(BigDecimal.valueOf(15)) <= 0) {
            qualityLevel = "良好";
        } else if (escapeRate.compareTo(BigDecimal.valueOf(30)) <= 0) {
            qualityLevel = "一般";
        } else {
            qualityLevel = "需改进";
        }
        stats.setQualityLevel(qualityLevel);

        return stats;
    }

    /**
     * 构建发现时机分析
     */
    private VersionEscapeAnalysisResponseDto.DiscoveryTimingAnalysis buildDiscoveryTimingAnalysis(
            VersionEscapeAnalysisResponseDto.EscapeRateStats escapeRateStats) {

        VersionEscapeAnalysisResponseDto.DiscoveryTimingAnalysis discoveryTiming = 
                new VersionEscapeAnalysisResponseDto.DiscoveryTimingAnalysis();

        int totalDefects = escapeRateStats.getTotalDefectsIntroduced();
        int currentVersionFound = escapeRateStats.getCurrentVersionFound();
        int escapedDefects = escapeRateStats.getEscapedDefects();

        discoveryTiming.setInVersionCount(currentVersionFound);
        discoveryTiming.setEscapedCount(escapedDefects);

        if (totalDefects > 0) {
            double inVersionPercentage = currentVersionFound * 100.0 / totalDefects;
            double escapedPercentage = escapedDefects * 100.0 / totalDefects;
            
            discoveryTiming.setInVersionPercentage(BigDecimal.valueOf(inVersionPercentage));
            discoveryTiming.setEscapedPercentage(BigDecimal.valueOf(escapedPercentage));
        } else {
            discoveryTiming.setInVersionPercentage(BigDecimal.ZERO);
            discoveryTiming.setEscapedPercentage(BigDecimal.ZERO);
        }

        discoveryTiming.setDescription(String.format("版本内发现%d个缺陷，逃逸%d个缺陷", 
                currentVersionFound, escapedDefects));

        return discoveryTiming;
    }

    /**
     * 构建遗留缺陷分析
     */
    private VersionEscapeAnalysisResponseDto.LegacyDefectAnalysis buildLegacyDefectAnalysis(
            VersionEscapeAnalysisResponseDto.EscapeRateStats escapeRateStats) {

        VersionEscapeAnalysisResponseDto.LegacyDefectAnalysis legacyAnalysis = 
                new VersionEscapeAnalysisResponseDto.LegacyDefectAnalysis();

        // 使用逃逸缺陷作为遗留缺陷
        int totalLegacyDefects = escapeRateStats.getEscapedDefects();
        double legacyDefectRate = escapeRateStats.getEscapeRate();

        legacyAnalysis.setTotalLegacyDefects(totalLegacyDefects);
        legacyAnalysis.setLegacyDefectRate(BigDecimal.valueOf(legacyDefectRate));
        legacyAnalysis.setAverageEscapeDays(30); // 默认值
        legacyAnalysis.setDescription("遗留缺陷平均逃逸30天");

        return legacyAnalysis;
    }

    /**
     * 构建质量评估
     */
    private VersionEscapeAnalysisResponseDto.QualityAssessment buildQualityAssessment(
            VersionEscapeAnalysisResponseDto.EscapeRateStats escapeRateStats) {

        VersionEscapeAnalysisResponseDto.QualityAssessment assessment = 
                new VersionEscapeAnalysisResponseDto.QualityAssessment();

        double escapeRate = escapeRateStats.getEscapeRate();

        // 整体质量等级
        String overallQualityLevel;
        String riskLevel;
        List<String> recommendations = new ArrayList<>();

        if (escapeRate <= 5) {
            overallQualityLevel = "优秀";
            riskLevel = "低风险";
            recommendations.add("保持当前测试质量");
        } else if (escapeRate <= 15) {
            overallQualityLevel = "良好";
            riskLevel = "中低风险";
            recommendations.add("继续优化测试用例");
        } else if (escapeRate <= 30) {
            overallQualityLevel = "一般";
            riskLevel = "中风险";
            recommendations.add("加强测试覆盖");
            recommendations.add("优化测试策略");
        } else {
            overallQualityLevel = "需改进";
            riskLevel = "高风险";
            recommendations.add("加强测试覆盖");
            recommendations.add("完善回归测试");
        }

        assessment.setOverallQualityLevel(overallQualityLevel);
        assessment.setRiskLevel(riskLevel);
        assessment.setRecommendations(recommendations);

        // 关键指标
        Map<String, BigDecimal> keyMetrics = new HashMap<>();
        keyMetrics.put("legacyDefectRate", BigDecimal.valueOf(escapeRate));
        keyMetrics.put("highSeverityEscapeRate", BigDecimal.valueOf(escapeRate));
        keyMetrics.put("escapeRate", BigDecimal.valueOf(escapeRate));

        assessment.setKeyMetrics(keyMetrics);

        return assessment;
    }

    /**
     * 构建分析时间范围
     */
    private String buildAnalysisTimeRange(VersionEscapeAnalysisRequestDto requestDto) {
        if (StringUtils.hasText(requestDto.getStartDate()) && StringUtils.hasText(requestDto.getEndDate())) {
            return requestDto.getStartDate() + " 至 " + requestDto.getEndDate();
        }
        return "";
    }

    /**
     * 安全获取整数值
     */
    private Integer getIntValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return 0;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * 安全获取BigDecimal值
     */
    private BigDecimal getBigDecimalValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof Number) {
            return BigDecimal.valueOf(((Number) value).doubleValue());
        }
        try {
            return new BigDecimal(value.toString());
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    
}
