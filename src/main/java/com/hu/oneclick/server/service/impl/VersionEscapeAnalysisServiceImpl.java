package com.hu.oneclick.server.service.impl;

import com.hu.oneclick.dao.IssueDao;
import com.hu.oneclick.model.domain.dto.VersionEscapeAnalysisRequestDto;
import com.hu.oneclick.model.domain.dto.VersionEscapeAnalysisResponseDto;
import com.hu.oneclick.server.service.VersionEscapeAnalysisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.util.StringUtils; // Added for StringUtils
import java.math.RoundingMode; // Added for RoundingMode

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

        // 实现具体的分析逻辑
        // 1. 查询所有 introduced_version = analysisVersion 的缺陷
        // 2. 按发现版本分组统计
        // 3. 计算逃逸率

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

        // 构建发现时机分析 - 使用真实数据
        VersionEscapeAnalysisResponseDto.DiscoveryTimingAnalysis discoveryTiming =
                buildDiscoveryTimingAnalysis(escapeRateStats);
        responseDto.setDiscoveryTiming(discoveryTiming);

        // 构建遗留缺陷分析 - 使用真实数据
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
                requestDto.getAnalysisVersion(), escapeRateStats.getEscapeRate());

        return responseDto;
    }

    @Override
    public Object getEscapeRateTrend(String projectId, List<String> versions) {
        log.info("获取逃逸率趋势，项目：{}，版本列表：{}", projectId, versions);

        try {
            List<Map<String, Object>> trendList = new ArrayList<>();
            
            for (String version : versions) {
                Map<String, Object> stats = issueDao.queryVersionEscapeStatistics(
                    projectId, version, null, null);
                
                Map<String, Object> versionTrend = new HashMap<>();
                versionTrend.put("version", version);
                versionTrend.put("totalDefectsIntroduced", stats.get("totalDefectsIntroduced"));
                versionTrend.put("currentVersionFound", stats.get("currentVersionFound"));
                versionTrend.put("escapedDefects", stats.get("escapedDefects"));
                versionTrend.put("escapeRate", stats.get("escapeRate"));
                
                // 计算质量等级
                BigDecimal escapeRate = new BigDecimal(stats.get("escapeRate").toString());
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
                versionTrend.put("qualityLevel", qualityLevel);
                
                trendList.add(versionTrend);
            }
            
            Map<String, Object> trendData = new HashMap<>();
            trendData.put("projectId", projectId);
            trendData.put("versions", versions);
            trendData.put("trendList", trendList);
            trendData.put("analysisTime", new Date());
            
            return trendData;
            
        } catch (Exception e) {
            log.error("获取逃逸率趋势失败", e);
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("projectId", projectId);
            errorData.put("versions", versions);
            errorData.put("error", e.getMessage());
            errorData.put("trendList", new ArrayList<>());
            
            return errorData;
        }
    }ns", versions);
        trendData.put("trendData", new ArrayList<>());

        return trendData;
    }

    @Override
    public String exportEscapeAnalysisReport(VersionEscapeAnalysisRequestDto requestDto) {
        log.info("导出逃逸率分析报告，项目：{}，版本：{}",
                requestDto.getProjectId(), requestDto.getAnalysisVersion());

        // TODO: 实现报告导出逻辑
        return "/reports/escape-analysis-" + requestDto.getAnalysisVersion() + ".xlsx";
    }

    /**
     * 构建查询参数
     */
    private Map<String, Object> buildQueryParams(VersionEscapeAnalysisRequestDto requestDto) {
        Map<String, Object> params = new HashMap<>();
        params.put("projectId", requestDto.getProjectId());
        params.put("analysisVersion", requestDto.getAnalysisVersion());

        // 如果有时间范围限制，添加时间条件
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
            // 构建SQL查询
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT ");
            sql.append("COUNT(*) as totalDefectsIntroduced, ");
            sql.append("COUNT(CASE WHEN issue_version = ? THEN 1 END) as currentVersionFound, ");
            sql.append("COUNT(CASE WHEN issue_version != ? THEN 1 END) as escapedDefects, ");
            sql.append("ROUND(COUNT(CASE WHEN issue_version != ? THEN 1 END) * 100.0 / COUNT(*), 2) as escapeRate ");
            sql.append("FROM issue ");
            sql.append("WHERE project_id = ? ");
            sql.append("AND introduced_version = ? ");

            List<Object> sqlParams = new ArrayList<>();
            String introducedVersion = (String) params.get("analysisVersion");
            sqlParams.add(introducedVersion);
            sqlParams.add(introducedVersion);
            sqlParams.add(introducedVersion);
            sqlParams.add(params.get("projectId"));
            sqlParams.add(introducedVersion);

            // 添加时间范围条件
            if (params.containsKey("startDate")) {
                sql.append("AND found_after_release >= ? ");
                sqlParams.add(params.get("startDate"));
            }
            if (params.containsKey("endDate")) {
                sql.append("AND found_after_release <= ? ");
                sqlParams.add(params.get("endDate"));
            }

            // 使用IssueDao的查询方法获取真实数据
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
            BigDecimal inVersionPercentage = BigDecimal.valueOf(currentVersionFound * 100.0 / totalDefects)
                    .setScale(2, RoundingMode.HALF_UP);
            BigDecimal escapedPercentage = BigDecimal.valueOf(escapedDefects * 100.0 / totalDefects)
                    .setScale(2, RoundingMode.HALF_UP);
            
            discoveryTiming.setInVersionPercentage(inVersionPercentage);
            discoveryTiming.setEscapedPercentage(escapedPercentage);
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
        
        VersionEscapeAnalysisResponseDto.LegacyDefectAnalysis legacyDefectAnalysis =
                new VersionEscapeAnalysisResponseDto.LegacyDefectAnalysis();
        
        int escapedDefects = escapeRateStats.getEscapedDefects();
        int totalDefects = escapeRateStats.getTotalDefectsIntroduced();
        
        legacyDefectAnalysis.setTotalLegacyDefects(escapedDefects);
        
        if (totalDefects > 0) {
            BigDecimal legacyDefectRate = BigDecimal.valueOf(escapedDefects * 100.0 / totalDefects)
                    .setScale(2, RoundingMode.HALF_UP);
            legacyDefectAnalysis.setLegacyDefectRate(legacyDefectRate);
        } else {
            legacyDefectAnalysis.setLegacyDefectRate(BigDecimal.ZERO);
        }
        
        // 暂时使用平均值，后续可以从数据库查询实际逃逸天数
        legacyDefectAnalysis.setAverageEscapeDays(escapedDefects > 0 ? 30 : 0);
        
        if (escapedDefects > 0) {
            legacyDefectAnalysis.setDescription(String.format("遗留缺陷%d个，平均逃逸30天", escapedDefects));
        } else {
            legacyDefectAnalysis.setDescription("无遗留缺陷");
        }
        
        return legacyDefectAnalysis;
    }

    /**
     * 构建质量评估
     */
    private VersionEscapeAnalysisResponseDto.QualityAssessment buildQualityAssessment(
            VersionEscapeAnalysisResponseDto.EscapeRateStats escapeRateStats) {
        
        VersionEscapeAnalysisResponseDto.QualityAssessment qualityAssessment =
                new VersionEscapeAnalysisResponseDto.QualityAssessment();
        
        BigDecimal escapeRate = escapeRateStats.getEscapeRate();
        
        // 根据逃逸率确定质量等级和风险级别
        if (escapeRate.compareTo(BigDecimal.valueOf(10)) <= 0) {
            qualityAssessment.setOverallQualityLevel("优秀");
            qualityAssessment.setRiskLevel("低风险");
            qualityAssessment.setRecommendations(List.of("保持当前测试质量", "继续优化测试流程"));
        } else if (escapeRate.compareTo(BigDecimal.valueOf(20)) <= 0) {
            qualityAssessment.setOverallQualityLevel("良好");
            qualityAssessment.setRiskLevel("中等风险");
            qualityAssessment.setRecommendations(List.of("加强边界测试", "增加回归测试覆盖"));
        } else {
            qualityAssessment.setOverallQualityLevel("需改进");
            qualityAssessment.setRiskLevel("高风险");
            qualityAssessment.setRecommendations(List.of("加强测试覆盖", "完善回归测试", "改进测试用例设计"));
        }

        Map<String, BigDecimal> keyMetrics = new HashMap<>();
        keyMetrics.put("escapeRate", escapeRate);
        keyMetrics.put("detectionEffectiveness", escapeRateStats.getDetectionEffectiveness());
        keyMetrics.put("legacyDefectRate", escapeRate); // 逃逸率等于遗留缺陷率
        qualityAssessment.setKeyMetrics(keyMetrics);

        return qualityAssessment;
    }

    /**
     * 构建逃逸率统计对象
     */
    private VersionEscapeAnalysisResponseDto.EscapeRateStats buildEscapeRateStats(Map<String, Object> stats) {
        VersionEscapeAnalysisResponseDto.EscapeRateStats escapeRateStats =
                new VersionEscapeAnalysisResponseDto.EscapeRateStats();

        // 从查询结果获取数据
        Integer totalDefects = getIntegerValue(stats, "totalDefectsIntroduced");
        Integer currentFound = getIntegerValue(stats, "currentVersionFound");
        Integer escaped = getIntegerValue(stats, "escapedDefects");
        BigDecimal escapeRate = getBigDecimalValue(stats, "escapeRate");

        escapeRateStats.setTotalDefectsIntroduced(totalDefects);
        escapeRateStats.setCurrentVersionFound(currentFound);
        escapeRateStats.setEscapedDefects(escaped);
        escapeRateStats.setEscapeRate(escapeRate);

        // 计算检测有效性
        BigDecimal detectionEffectiveness = BigDecimal.valueOf(100).subtract(escapeRate);
        escapeRateStats.setDetectionEffectiveness(detectionEffectiveness);

        // 根据逃逸率确定质量等级
        String qualityLevel = determineQualityLevel(escapeRate);
        escapeRateStats.setQualityLevel(qualityLevel);

        return escapeRateStats;
    }

    /**
     * 安全获取Integer值
     */
    private Integer getIntegerValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return 0;
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        // 如果是String类型，尝试解析
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                log.warn("无法将值 '{}' 解析为 Integer", value);
                return 0;
            }
        }
        return 0;
    }

    /**
     * 安全获取BigDecimal值
     */
    private BigDecimal getBigDecimalValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return BigDecimal.ZERO;
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof Number) {
            return new BigDecimal(value.toString()).setScale(2, RoundingMode.HALF_UP);
        }
        // 如果是String类型，尝试解析
        if (value instanceof String) {
            try {
                return new BigDecimal((String) value).setScale(2, RoundingMode.HALF_UP);
            } catch (NumberFormatException e) {
                log.warn("无法将值 '{}' 解析为 BigDecimal", value);
                return BigDecimal.ZERO;
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * 根据逃逸率确定质量等级
     */
    private String determineQualityLevel(BigDecimal escapeRate) {
        if (escapeRate.compareTo(BigDecimal.valueOf(10)) <= 0) {
            return "优秀";
        } else if (escapeRate.compareTo(BigDecimal.valueOf(20)) <= 0) {
            return "良好";
        } else if (escapeRate.compareTo(BigDecimal.valueOf(30)) <= 0) {
            return "一般";
        } else {
            return "需改进";
        }
    }

    /**
     * 构建分析时间范围字符串
     * @param requestDto 请求参数
     * @return 时间范围字符串，如果没有提供时间参数则返回null
     */
    private String buildAnalysisTimeRange(VersionEscapeAnalysisRequestDto requestDto) {
        String startDate = requestDto.getStartDate();
        String endDate = requestDto.getEndDate();

        // 如果用户没有提供任何时间参数，返回null表示无时间限制
        if (startDate == null && endDate == null) {
            log.info("用户未提供时间范围，分析将不受时间限制");
            return null;
        }

        // 如果只提供了开始时间
        if (startDate != null && endDate == null) {
            return startDate + " ~ 至今";
        }

        // 如果只提供了结束时间
        if (startDate == null && endDate != null) {
            return "起始 ~ " + endDate;
        }

        // 如果提供了完整的时间范围
        return startDate + " ~ " + endDate;
    }
}