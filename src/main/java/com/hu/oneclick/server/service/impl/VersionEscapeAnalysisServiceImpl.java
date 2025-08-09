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

        // 构建逃逸率统计 - 只保留核心功能
        VersionEscapeAnalysisResponseDto.EscapeRateStats escapeRateStats =
                buildEscapeRateStats(escapeStats);
        responseDto.setEscapeRateStats(escapeRateStats);

        // 所有其他字段设为null或空，简化响应
        responseDto.setDiscoveryTiming(null);
        responseDto.setLegacyDefectAnalysis(null);
        responseDto.setQualityAssessment(null);
        responseDto.setVersionGroups(new ArrayList<>());
        responseDto.setSeverityGroups(new ArrayList<>());
        responseDto.setDefectDetails(new ArrayList<>());

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
                BigDecimal escapeRateBigDecimal = getBigDecimalValue(stats, "escapeRate");
                double escapeRateDouble = escapeRateBigDecimal.doubleValue();
                String qualityLevel;
                if (escapeRateDouble <= 5.0) {
                    qualityLevel = "优秀";
                } else if (escapeRateDouble <= 15.0) {
                    qualityLevel = "良好";
                } else if (escapeRateDouble <= 30.0) {
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
        double escapeRateValue = escapeRate.doubleValue();
        String qualityLevel;
        if (escapeRateValue <= 5.0) {
            qualityLevel = "优秀";
        } else if (escapeRateValue <= 15.0) {
            qualityLevel = "良好";
        } else if (escapeRateValue <= 30.0) {
            qualityLevel = "一般";
        } else {
            qualityLevel = "需改进";
        }
        stats.setQualityLevel(qualityLevel);

        return stats;
    }

    // 所有复杂的分析方法已移除，只保留核心的逃逸率统计功能

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