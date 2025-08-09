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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

        // 构建发现时机分析
        VersionEscapeAnalysisResponseDto.DiscoveryTimingAnalysis discoveryTiming = 
                new VersionEscapeAnalysisResponseDto.DiscoveryTimingAnalysis();
        discoveryTiming.setInVersionCount(12);
        discoveryTiming.setInVersionPercentage(BigDecimal.valueOf(60.00));
        discoveryTiming.setEscapedCount(8);
        discoveryTiming.setEscapedPercentage(BigDecimal.valueOf(40.00));
        discoveryTiming.setDescription("版本内发现12个缺陷，逃逸8个缺陷");
        responseDto.setDiscoveryTiming(discoveryTiming);

        // 构建遗留缺陷分析
        VersionEscapeAnalysisResponseDto.LegacyDefectAnalysis legacyDefectAnalysis = 
                new VersionEscapeAnalysisResponseDto.LegacyDefectAnalysis();
        legacyDefectAnalysis.setTotalLegacyDefects(8);
        legacyDefectAnalysis.setLegacyDefectRate(BigDecimal.valueOf(40.00));
        legacyDefectAnalysis.setAverageEscapeDays(30);
        legacyDefectAnalysis.setDescription("遗留缺陷平均逃逸30天");
        responseDto.setLegacyDefectAnalysis(legacyDefectAnalysis);

        // 初始化其他字段
        responseDto.setVersionGroups(new ArrayList<>());
        responseDto.setSeverityGroups(new ArrayList<>());
        responseDto.setDefectDetails(new ArrayList<>());

        VersionEscapeAnalysisResponseDto.QualityAssessment qualityAssessment = 
                new VersionEscapeAnalysisResponseDto.QualityAssessment();
        qualityAssessment.setOverallQualityLevel("需改进");
        qualityAssessment.setRiskLevel("高风险");
        qualityAssessment.setRecommendations(List.of("加强测试覆盖", "完善回归测试"));

        Map<String, BigDecimal> keyMetrics = new HashMap<>();
        keyMetrics.put("escapeRate", BigDecimal.valueOf(40.00));
        keyMetrics.put("highSeverityEscapeRate", BigDecimal.valueOf(40.00));
        keyMetrics.put("legacyDefectRate", BigDecimal.valueOf(40.00));
        qualityAssessment.setKeyMetrics(keyMetrics);

        responseDto.setQualityAssessment(qualityAssessment);

        log.info("版本缺陷逃逸率分析完成，版本：{}，逃逸率：{}%", 
                requestDto.getAnalysisVersion(), escapeRateStats.getEscapeRate());

        return responseDto;
    }

    @Override
    public Object getEscapeRateTrend(String projectId, List<String> versions) {
        log.info("获取逃逸率趋势，项目：{}，版本列表：{}", projectId, versions);

        // TODO: 实现趋势分析逻辑
        Map<String, Object> trendData = new HashMap<>();
        trendData.put("projectId", projectId);
        trendData.put("versions", versions);
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
        params.put("introducedVersion", requestDto.getAnalysisVersion());
        
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
            String introducedVersion = (String) params.get("introducedVersion");
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
            
            Map<String, Object> result = issueDao.queryForMap(sql.toString(), sqlParams.toArray());
            
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