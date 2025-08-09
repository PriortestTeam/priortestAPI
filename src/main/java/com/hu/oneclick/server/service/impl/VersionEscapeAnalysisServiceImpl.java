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

        // 构建质量评估
        VersionEscapeAnalysisResponseDto.QualityAssessment qualityAssessment = 
                buildQualityAssessment(escapeRateStats);
        responseDto.setQualityAssessment(qualityAssessment);

        // 查询详细缺陷信息
        List<VersionEscapeAnalysisResponseDto.EscapeDefectDetail> defectDetails = 
                queryDefectDetails(queryParams);
        responseDto.setDefectDetails(defectDetails);

        // 所有其他字段设为null或空，简化响应
        responseDto.setDiscoveryTiming(null);
        responseDto.setLegacyDefectAnalysis(null);
        responseDto.setVersionGroups(new ArrayList<>());
        responseDto.setSeverityGroups(new ArrayList<>());

        log.info("版本缺陷逃逸率分析完成，版本：{}，逃逸率：{}%",
                requestDto.getAnalysisVersion(),
                escapeRateStats.getEscapeRate());

        return responseDto;
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
        stats.setEscapeRate(escapeRate);

        // 计算检测有效性 - 使用BigDecimal运算
        BigDecimal hundred = BigDecimal.valueOf(100.0);
        BigDecimal detectionEffectiveness = hundred.subtract(escapeRate);
        stats.setDetectionEffectiveness(detectionEffectiveness);

        // 计算质量等级 - 使用BigDecimal比较避免精度误差
        BigDecimal threshold5 = BigDecimal.valueOf(5.0);
        BigDecimal threshold15 = BigDecimal.valueOf(15.0);
        BigDecimal threshold30 = BigDecimal.valueOf(30.0);
        
        String qualityLevel;
        if (escapeRate.compareTo(threshold5) <= 0) {
            qualityLevel = "优秀";
        } else if (escapeRate.compareTo(threshold15) <= 0) {
            qualityLevel = "良好";
        } else if (escapeRate.compareTo(threshold30) <= 0) {
            qualityLevel = "一般";
        } else {
            qualityLevel = "需改进";
        }
        stats.setQualityLevel(qualityLevel);

        return stats;
    }

    /**
     * 构建质量评估
     */
    private VersionEscapeAnalysisResponseDto.QualityAssessment buildQualityAssessment(
            VersionEscapeAnalysisResponseDto.EscapeRateStats stats) {
        
        VersionEscapeAnalysisResponseDto.QualityAssessment assessment = 
                new VersionEscapeAnalysisResponseDto.QualityAssessment();

        BigDecimal escapeRate = stats.getEscapeRate();
        BigDecimal detectionEffectiveness = stats.getDetectionEffectiveness();
        
        // 预定义的比较阈值
        BigDecimal threshold5 = BigDecimal.valueOf(5.0);
        BigDecimal threshold15 = BigDecimal.valueOf(15.0);
        BigDecimal threshold30 = BigDecimal.valueOf(30.0);
        BigDecimal threshold70 = BigDecimal.valueOf(70.0);
        BigDecimal threshold85 = BigDecimal.valueOf(85.0);
        BigDecimal threshold95 = BigDecimal.valueOf(95.0);
        
        // 整体质量等级
        assessment.setOverallQualityLevel(stats.getQualityLevel());
        
        // 风险等级评估
        String riskLevel;
        if (escapeRate.compareTo(threshold5) <= 0) {
            riskLevel = "低风险";
        } else if (escapeRate.compareTo(threshold15) <= 0) {
            riskLevel = "中等风险";
        } else if (escapeRate.compareTo(threshold30) <= 0) {
            riskLevel = "高风险";
        } else {
            riskLevel = "极高风险";
        }
        assessment.setRiskLevel(riskLevel);
        
        // 改进建议
        List<String> recommendations = new ArrayList<>();
        if (escapeRate.compareTo(threshold30) > 0) {
            recommendations.add("紧急加强测试覆盖，重点关注回归测试");
            recommendations.add("建立缺陷预防机制，加强代码审查");
            recommendations.add("完善测试用例设计，增加边界条件测试");
        } else if (escapeRate.compareTo(threshold15) > 0) {
            recommendations.add("优化测试策略，加强集成测试");
            recommendations.add("建立缺陷分析机制，识别常见缺陷模式");
            recommendations.add("加强自动化测试覆盖");
        } else if (escapeRate.compareTo(threshold5) > 0) {
            recommendations.add("持续优化测试用例质量");
            recommendations.add("加强探索性测试");
        } else {
            recommendations.add("保持当前测试质量水平");
            recommendations.add("可考虑分享最佳实践给其他团队");
        }
        assessment.setRecommendations(recommendations);
        
        // 关键指标 - 使用BigDecimal类型匹配setter要求
        Map<String, BigDecimal> keyMetrics = new HashMap<>();
        keyMetrics.put("escapeRate", escapeRate);
        keyMetrics.put("detectionEffectiveness", detectionEffectiveness);
        keyMetrics.put("totalDefects", BigDecimal.valueOf(stats.getTotalDefectsIntroduced()));
        keyMetrics.put("foundInVersion", BigDecimal.valueOf(stats.getCurrentVersionFound()));
        keyMetrics.put("escapedDefects", BigDecimal.valueOf(stats.getEscapedDefects()));
        assessment.setKeyMetrics(keyMetrics);
        
        // 测试覆盖评估
        String testCoverageAssessment;
        if (detectionEffectiveness.compareTo(threshold95) >= 0) {
            testCoverageAssessment = "测试覆盖优秀，能够有效发现版本内缺陷";
        } else if (detectionEffectiveness.compareTo(threshold85) >= 0) {
            testCoverageAssessment = "测试覆盖良好，但仍有提升空间";
        } else if (detectionEffectiveness.compareTo(threshold70) >= 0) {
            testCoverageAssessment = "测试覆盖一般，需要加强测试深度";
        } else {
            testCoverageAssessment = "测试覆盖不足，存在明显测试盲区";
        }
        assessment.setTestCoverageAssessment(testCoverageAssessment);
        
        // 关键发现
        List<String> keyFindings = new ArrayList<>();
        if (stats.getTotalDefectsIntroduced() == 0) {
            keyFindings.add("该版本未发现引入任何缺陷");
        } else {
            keyFindings.add(String.format("该版本共引入 %d 个缺陷", stats.getTotalDefectsIntroduced()));
            keyFindings.add(String.format("版本内发现 %d 个缺陷，逃逸 %d 个缺陷", 
                    stats.getCurrentVersionFound(), stats.getEscapedDefects()));
            
            if (stats.getEscapedDefects() == 0) {
                keyFindings.add("所有缺陷均在版本内被发现，测试效果优秀");
            } else {
                // 格式化显示时才转换为double
                keyFindings.add(String.format("检测有效性为 %.1f%%，%s", 
                        detectionEffectiveness.doubleValue(), 
                        detectionEffectiveness.compareTo(threshold85) >= 0 ? "测试效果良好" : "测试效果有待提升"));
            }
        }
        assessment.setKeyFindings(keyFindings);
        
        return assessment;
    }

    /**
     * 查询详细缺陷信息
     */
    private List<VersionEscapeAnalysisResponseDto.EscapeDefectDetail> queryDefectDetails(Map<String, Object> params) {
        try {
            List<Map<String, Object>> defectList = issueDao.queryVersionDefectDetails(
                (String) params.get("projectId"),
                (String) params.get("analysisVersion"),
                (String) params.get("startDate"),
                (String) params.get("endDate")
            );

            List<VersionEscapeAnalysisResponseDto.EscapeDefectDetail> detailList = new ArrayList<>();
            
            for (Map<String, Object> defect : defectList) {
                VersionEscapeAnalysisResponseDto.EscapeDefectDetail detail = 
                        new VersionEscapeAnalysisResponseDto.EscapeDefectDetail();
                
                // 基本信息
                detail.setDefectId(getStringValue(defect, "id"));
                detail.setTitle(getStringValue(defect, "title"));
                detail.setSeverity(getStringValue(defect, "severity"));
                detail.setPriority(getStringValue(defect, "priority"));
                
                // 版本信息
                detail.setIntroducedVersion(getStringValue(defect, "introduced_version"));
                detail.setFoundVersion(getStringValue(defect, "issue_version"));
                
                // 判断是否为逃逸缺陷
                String introducedVersion = getStringValue(defect, "introduced_version");
                String foundVersion = getStringValue(defect, "issue_version");
                boolean isEscaped = !introducedVersion.equals(foundVersion);
                detail.setIsEscaped(isEscaped);
                
                // 计算逃逸天数（如果是逃逸缺陷）
                if (isEscaped) {
                    // 这里可以根据版本发布时间计算逃逸天数
                    // 暂时设置为0，后续可以完善
                    detail.setEscapeDays(0);
                } else {
                    detail.setEscapeDays(0);
                }
                
                // 影响描述
                if (isEscaped) {
                    detail.setImpactDescription(
                        String.format("该缺陷在版本 %s 中引入，但直到版本 %s 才被发现，存在逃逸风险", 
                                introducedVersion, foundVersion));
                } else {
                    detail.setImpactDescription("该缺陷在引入版本内被及时发现，未发生逃逸");
                }
                
                // 修复状态评估
                String status = getStringValue(defect, "status");
                String fixStatus;
                if ("RESOLVED".equalsIgnoreCase(status) || "CLOSED".equalsIgnoreCase(status)) {
                    fixStatus = "已修复";
                } else if ("IN_PROGRESS".equalsIgnoreCase(status)) {
                    fixStatus = "修复中";
                } else {
                    fixStatus = "待修复";
                }
                detail.setFixStatus(fixStatus);
                
                detailList.add(detail);
            }
            
            log.info("查询到 {} 条详细缺陷信息", detailList.size());
            return detailList;
            
        } catch (Exception e) {
            log.error("查询详细缺陷信息失败", e);
            return new ArrayList<>();
        }
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

    /**
     * 安全获取字符串值
     */
    private String getStringValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : "";
    }


}