
package com.hu.oneclick.server.service.impl;

import com.hu.oneclick.dao.IssueDao;
import com.hu.oneclick.dao.TestCaseDao;
import com.hu.oneclick.model.domain.dto.DefectDensityRequestDto;
import com.hu.oneclick.model.domain.dto.DefectDensityResponseDto;
import com.hu.oneclick.server.service.DefectDensityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 缺陷密度服务实现
 */
@Slf4j
@Service
public class DefectDensityServiceImpl implements DefectDensityService {

    @Resource
    private IssueDao issueDao;
    
    @Resource
    private TestCaseDao testCaseDao;
    
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public DefectDensityResponseDto calculateDefectDensity(DefectDensityRequestDto requestDto) {
        log.info("开始计算缺陷密度，项目ID：{}，版本：{}", requestDto.getProjectId(), requestDto.getMajorVersion());
        
        try {
            // 1. 查询执行详情和相关缺陷信息
            List<Map<String, Object>> executionDetails = testCaseDao.queryExecutionDetails(
                    requestDto.getProjectId(), 
                    requestDto.getMajorVersion(), 
                    requestDto.getIncludeVersions(), 
                    requestDto.getTestCycleIds()
            );
            
            // 2. 统计基础数据
            DefectDensityResponseDto.StatisticsData statistics = calculateStatistics(executionDetails, requestDto);
            
            // 3. 查询缺陷详情并关联测试用例
            List<DefectDensityResponseDto.DefectDetail> defectDetails = buildDefectDetails(executionDetails, requestDto);
            
            // 4. 计算缺陷密度
            double defectDensity = calculateDensity(statistics, requestDto.getCalculationType(), requestDto.getEnvironmentSpecificWeight());
            
            // 5. 确定质量等级
            String qualityLevel = determineQualityLevel(defectDensity);
            
            // 6. 构建响应结果
            DefectDensityResponseDto responseDto = new DefectDensityResponseDto();
            responseDto.setDefectDensity(Math.round(defectDensity * 100.0) / 100.0);
            responseDto.setQualityLevel(qualityLevel);
            responseDto.setCalculationType(requestDto.getCalculationType());
            responseDto.setStatistics(statistics);
            responseDto.setDefectDetails(defectDetails);
            responseDto.setConfig(buildCalculationConfig(requestDto));
            
            log.info("缺陷密度计算完成，密度：{}%，缺陷数：{}个", defectDensity, defectDetails.size());
            return responseDto;
            
        } catch (Exception e) {
            log.error("计算缺陷密度时发生错误", e);
            throw new RuntimeException("计算缺陷密度失败：" + e.getMessage(), e);
        }
    }
    
    /**
     * 计算统计数据
     */
    private DefectDensityResponseDto.StatisticsData calculateStatistics(
            List<Map<String, Object>> executionDetails, DefectDensityRequestDto requestDto) {
        
        DefectDensityResponseDto.StatisticsData statistics = new DefectDensityResponseDto.StatisticsData();
        
        // 统计独立测试用例数
        Set<String> uniqueTestCaseIds = executionDetails.stream()
                .map(detail -> String.valueOf(detail.get("testCaseId")))
                .collect(Collectors.toSet());
        statistics.setUniqueTestCases(uniqueTestCaseIds.size());
        
        // 统计总执行次数
        statistics.setTotalExecutions(executionDetails.size());
        
        // 统计测试周期数
        Set<String> uniqueCycleIds = executionDetails.stream()
                .map(detail -> String.valueOf(detail.get("testCycleId")))
                .collect(Collectors.toSet());
        statistics.setTotalCycles(uniqueCycleIds.size());
        
        // 统计环境数
        Set<String> uniqueEnvironments = executionDetails.stream()
                .map(detail -> String.valueOf(detail.get("testCycleEnv")))
                .filter(env -> env != null && !env.equals("null"))
                .collect(Collectors.toSet());
        statistics.setTotalEnvironments(uniqueEnvironments.size());
        
        // 查询关联的缺陷信息
        List<Map<String, Object>> defectData = queryDefectData(executionDetails);
        
        // 统计缺陷数据
        statistics.setTotalDefectInstances(defectData.size());
        
        // 根据去重策略统计独立缺陷数和环境特定缺陷数
        Map<String, List<Map<String, Object>>> defectGroups = groupDefectsByDeduplication(defectData, requestDto);
        statistics.setUniqueDefects(defectGroups.get("unique").size());
        statistics.setEnvironmentSpecificDefects(defectGroups.get("environmentSpecific").size());
        
        // 统计发现缺陷的环境数
        Set<String> environmentsWithDefects = defectData.stream()
                .map(defect -> String.valueOf(defect.get("env")))
                .filter(env -> env != null && !env.equals("null"))
                .collect(Collectors.toSet());
        statistics.setEnvironmentsWithDefects(environmentsWithDefects.size());
        
        // 计算环境覆盖率
        double environmentCoverage = statistics.getTotalEnvironments() > 0 ? 
                (double) statistics.getEnvironmentsWithDefects() / statistics.getTotalEnvironments() * 100 : 0.0;
        statistics.setEnvironmentCoverage(Math.round(environmentCoverage * 100.0) / 100.0);
        
        return statistics;
    }
    
    /**
     * 查询缺陷数据
     */
    private List<Map<String, Object>> queryDefectData(List<Map<String, Object>> executionDetails) {
        List<Map<String, Object>> defectData = new ArrayList<>();
        
        for (Map<String, Object> execution : executionDetails) {
            String runCaseId = String.valueOf(execution.get("runCaseId"));
            if (runCaseId != null && !runCaseId.equals("null")) {
                // 根据runCaseId查询关联的缺陷
                try {
                    List<Map<String, Object>> defects = issueDao.queryDefectsByRunCaseId(runCaseId);
                    for (Map<String, Object> defect : defects) {
                        // 将执行信息合并到缺陷数据中
                        Map<String, Object> combinedData = new HashMap<>(defect);
                        combinedData.putAll(execution);
                        defectData.add(combinedData);
                    }
                } catch (Exception e) {
                    log.warn("查询RunCaseId={}的缺陷信息时出错：{}", runCaseId, e.getMessage());
                }
            }
        }
        
        return defectData;
    }
    
    /**
     * 根据去重策略分组缺陷
     */
    private Map<String, List<Map<String, Object>>> groupDefectsByDeduplication(
            List<Map<String, Object>> defectData, DefectDensityRequestDto requestDto) {
        
        Map<String, List<Map<String, Object>>> groups = new HashMap<>();
        groups.put("unique", new ArrayList<>());
        groups.put("environmentSpecific", new ArrayList<>());
        
        if (!requestDto.getEnableDeduplication()) {
            // 不启用去重，所有缺陷都算作独立缺陷
            groups.get("unique").addAll(defectData);
            return groups;
        }
        
        // 按缺陷标题和严重程度分组
        Map<String, List<Map<String, Object>>> titleSeverityGroups = defectData.stream()
                .collect(Collectors.groupingBy(defect -> 
                        String.valueOf(defect.get("title")) + "|" + String.valueOf(defect.get("severity"))));
        
        for (List<Map<String, Object>> group : titleSeverityGroups.values()) {
            if (group.size() == 1) {
                // 单个缺陷，算作独立缺陷
                groups.get("unique").addAll(group);
            } else {
                // 多个相似缺陷，检查是否为环境特定
                if (isEnvironmentSpecific(group)) {
                    groups.get("environmentSpecific").addAll(group);
                } else {
                    // 选择一个代表作为独立缺陷
                    groups.get("unique").add(group.get(0));
                }
            }
        }
        
        return groups;
    }
    
    /**
     * 判断是否为环境特定缺陷
     */
    private boolean isEnvironmentSpecific(List<Map<String, Object>> defectGroup) {
        // 检查是否在不同环境中发现
        Set<String> environments = defectGroup.stream()
                .map(defect -> String.valueOf(defect.get("env")))
                .filter(env -> env != null && !env.equals("null"))
                .collect(Collectors.toSet());
        
        return environments.size() > 1;
    }
    
    /**
     * 构建缺陷详情列表
     */
    private List<DefectDensityResponseDto.DefectDetail> buildDefectDetails(
            List<Map<String, Object>> executionDetails, DefectDensityRequestDto requestDto) {
        
        List<Map<String, Object>> defectData = queryDefectData(executionDetails);
        Map<String, List<Map<String, Object>>> defectGroups = new HashMap<>();
        
        // 按缺陷ID分组
        for (Map<String, Object> defect : defectData) {
            String defectId = String.valueOf(defect.get("id"));
            defectGroups.computeIfAbsent(defectId, k -> new ArrayList<>()).add(defect);
        }
        
        List<DefectDensityResponseDto.DefectDetail> defectDetails = new ArrayList<>();
        
        for (Map.Entry<String, List<Map<String, Object>>> entry : defectGroups.entrySet()) {
            String defectId = entry.getKey();
            List<Map<String, Object>> defectInstances = entry.getValue();
            
            // 取第一个实例作为缺陷基本信息
            Map<String, Object> primaryDefect = defectInstances.get(0);
            
            DefectDensityResponseDto.DefectDetail defectDetail = new DefectDensityResponseDto.DefectDetail();
            defectDetail.setDefectId(defectId);
            defectDetail.setDefectTitle(String.valueOf(primaryDefect.get("title")));
            defectDetail.setDefectDescription(String.valueOf(primaryDefect.get("description")));
            defectDetail.setSeverity(String.valueOf(primaryDefect.get("severity")));
            defectDetail.setPriority(String.valueOf(primaryDefect.get("priority")));
            defectDetail.setDefectStatus(getDefectStatusText(primaryDefect.get("issue_status")));
            defectDetail.setEnvironment(String.valueOf(primaryDefect.get("env")));
            defectDetail.setBrowser(String.valueOf(primaryDefect.get("browser")));
            defectDetail.setTestDevice(String.valueOf(primaryDefect.get("test_device")));
            defectDetail.setCreateTime(formatDate(primaryDefect.get("create_time")));
            defectDetail.setIsEnvironmentSpecific(isEnvironmentSpecific(defectInstances));
            
            // 构建关联的测试用例信息
            List<DefectDensityResponseDto.RelatedTestCase> relatedTestCases = defectInstances.stream()
                    .map(this::buildRelatedTestCase)
                    .collect(Collectors.toList());
            defectDetail.setRelatedTestCases(relatedTestCases);
            
            defectDetails.add(defectDetail);
        }
        
        return defectDetails;
    }
    
    /**
     * 构建关联测试用例信息
     */
    private DefectDensityResponseDto.RelatedTestCase buildRelatedTestCase(Map<String, Object> data) {
        DefectDensityResponseDto.RelatedTestCase relatedTestCase = new DefectDensityResponseDto.RelatedTestCase();
        
        relatedTestCase.setTestCaseId(String.valueOf(data.get("testCaseId")));
        relatedTestCase.setTestCaseTitle(String.valueOf(data.get("testCaseTitle")));
        relatedTestCase.setTestCaseVersion(String.valueOf(data.get("version")));
        relatedTestCase.setTestCycleId(String.valueOf(data.get("testCycleId")));
        relatedTestCase.setTestCycleTitle(String.valueOf(data.get("testCycleTitle")));
        relatedTestCase.setTestCycleEnv(String.valueOf(data.get("testCycleEnv")));
        relatedTestCase.setTestCycleVersion(String.valueOf(data.get("testCycleVersion")));
        relatedTestCase.setExecutionStatus(getExecutionStatusText(data.get("executionStatus")));
        relatedTestCase.setExecutionTime(formatDate(data.get("executionTime")));
        relatedTestCase.setRunCount(getIntValue(data.get("runCount")));
        relatedTestCase.setRunCaseId(String.valueOf(data.get("runCaseId")));
        
        return relatedTestCase;
    }
    
    /**
     * 计算缺陷密度
     */
    private double calculateDensity(DefectDensityResponseDto.StatisticsData statistics, 
                                   String calculationType, Double environmentSpecificWeight) {
        
        double density = 0.0;
        
        switch (calculationType) {
            case "CASE_BASED":
                // 基于独立用例计算
                if (statistics.getUniqueTestCases() > 0) {
                    density = (double) statistics.getTotalDefectInstances() / statistics.getUniqueTestCases() * 100;
                }
                break;
                
            case "EXECUTION_BASED":
                // 基于执行次数计算
                if (statistics.getTotalExecutions() > 0) {
                    density = (double) statistics.getTotalDefectInstances() / statistics.getTotalExecutions() * 100;
                }
                break;
                
            case "WEIGHTED":
                // 加权计算
                if (statistics.getUniqueTestCases() > 0) {
                    double weightedDefects = statistics.getUniqueDefects() * 1.0 + 
                                           statistics.getEnvironmentSpecificDefects() * environmentSpecificWeight;
                    density = weightedDefects / statistics.getUniqueTestCases() * 100;
                }
                break;
                
            default:
                // 默认使用基于用例的计算
                if (statistics.getUniqueTestCases() > 0) {
                    density = (double) statistics.getTotalDefectInstances() / statistics.getUniqueTestCases() * 100;
                }
        }
        
        return density;
    }
    
    /**
     * 确定质量等级
     */
    private String determineQualityLevel(double defectDensity) {
        if (defectDensity < 5) {
            return "优秀";
        } else if (defectDensity < 10) {
            return "良好";
        } else if (defectDensity < 15) {
            return "一般";
        } else {
            return "需改进";
        }
    }
    
    /**
     * 构建计算配置
     */
    private DefectDensityResponseDto.CalculationConfig buildCalculationConfig(DefectDensityRequestDto requestDto) {
        DefectDensityResponseDto.CalculationConfig config = new DefectDensityResponseDto.CalculationConfig();
        config.setEnableDeduplication(requestDto.getEnableDeduplication());
        config.setSimilarityThreshold(requestDto.getSimilarityThreshold());
        config.setEnvironmentSpecificWeight(requestDto.getEnvironmentSpecificWeight());
        
        Map<String, Object> queryConditions = new HashMap<>();
        queryConditions.put("projectId", requestDto.getProjectId());
        queryConditions.put("majorVersion", requestDto.getMajorVersion());
        queryConditions.put("includeVersions", requestDto.getIncludeVersions());
        queryConditions.put("testCycleIds", requestDto.getTestCycleIds());
        config.setQueryConditions(queryConditions);
        
        return config;
    }
    
    /**
     * 工具方法：格式化日期
     */
    private String formatDate(Object dateObj) {
        if (dateObj == null) return null;
        try {
            if (dateObj instanceof Date) {
                return dateFormat.format((Date) dateObj);
            } else {
                return String.valueOf(dateObj);
            }
        } catch (Exception e) {
            return String.valueOf(dateObj);
        }
    }
    
    /**
     * 工具方法：获取整数值
     */
    private Integer getIntValue(Object obj) {
        if (obj == null) return 0;
        try {
            return Integer.parseInt(String.valueOf(obj));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    /**
     * 工具方法：获取缺陷状态文本
     */
    private String getDefectStatusText(Object status) {
        if (status == null) return "未知";
        
        int statusCode = getIntValue(status);
        switch (statusCode) {
            case 1: return "打开";
            case 2: return "已修复";
            case 3: return "已关闭";
            case 4: return "重新打开";
            default: return "未知";
        }
    }
    
    /**
     * 工具方法：获取执行状态文本
     */
    private String getExecutionStatusText(Object status) {
        if (status == null) return "未知";
        
        String statusStr = String.valueOf(status);
        switch (statusStr) {
            case "1": return "通过";
            case "2": return "失败";
            case "3": return "阻塞";
            case "4": return "跳过";
            case "4.1": return "部分通过";
            case "4.2": return "需重测";
            case "4.3": return "待评审";
            case "4.4": return "无法测试";
            case "4.5": return "延迟测试";
            default: return "未知";
        }
    }
}
