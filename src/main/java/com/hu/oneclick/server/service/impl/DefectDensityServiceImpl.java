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
            List<Long> testCycleIds = requestDto.getTestCycleIds() != null ? 
                    requestDto.getTestCycleIds().stream()
                            .map(Long::parseLong)
                            .collect(Collectors.toList()) : null;

            List<Map<String, Object>> executionDetails = testCaseDao.queryExecutionDetails(
                    Long.parseLong(requestDto.getProjectId()), 
                    requestDto.getMajorVersion(), 
                    requestDto.getIncludeVersions(), 
                    testCycleIds
            );

            // 2. 统计基础数据
            DefectDensityResponseDto.StatisticsData statistics = calculateStatistics(executionDetails, requestDto);

            // 3. 查询缺陷详情并关联测试用例
            List<DefectDensityResponseDto.DefectDetail> defectDetails = buildDefectDetails(executionDetails, requestDto);

            // 4. 计算缺陷密度
            double density = calculateDensity(statistics, requestDto.getCalculationType(), 
                    requestDto.getEnvironmentSpecificWeight());

            // 5. 判断质量等级
            String qualityLevel = getQualityLevel(density, statistics.isHasValidData());

            // 6. 查询缺陷数据并按runCaseId分组
            Map<String, List<Map<String, Object>>> defectsByRunCaseId = new HashMap<>();
            List<Map<String, Object>> defectData = queryDefectData(executionDetails);
            for (Map<String, Object> defect : defectData) {
                String runCaseId = String.valueOf(defect.get("runCaseId"));
                defectsByRunCaseId.computeIfAbsent(runCaseId, k -> new ArrayList<>()).add(defect);
            }

            // 7. 构建测试用例和测试周期详细信息
            List<DefectDensityResponseDto.TestCaseDetailDto> testCaseDetails = 
                    buildTestCaseDetails(executionDetails, defectsByRunCaseId);
            List<DefectDensityResponseDto.TestCycleDetailDto> testCycleDetails = 
                    buildTestCycleDetails(executionDetails, defectsByRunCaseId);

            // 8. 构建响应结果
            DefectDensityResponseDto responseDto = new DefectDensityResponseDto();
            responseDto.setDefectDensity(Math.round(density * 100.0) / 100.0);
            responseDto.setQualityLevel(qualityLevel);
            responseDto.setCalculationType(requestDto.getCalculationType());
            responseDto.setStatistics(statistics);
            responseDto.setDefectDetails(defectDetails);
            responseDto.setConfig(buildCalculationConfig(requestDto));
            responseDto.setTestCaseDetails(testCaseDetails);
            responseDto.setTestCycleDetails(testCycleDetails);

            log.info("缺陷密度计算完成，密度：{}%，缺陷数：{}个", density, defectDetails.size());
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
        int totalExecutions = executionDetails.stream()
                .mapToInt(detail -> getIntValue(detail.get("runCount")))
                .sum();
        statistics.setTotalExecutions(totalExecutions);

        // 统计测试周期数
        Set<String> uniqueCycleIds = executionDetails.stream()
                .map(detail -> String.valueOf(detail.get("testCycleId")))
                .collect(Collectors.toSet());
        statistics.setTotalCycles(uniqueCycleIds.size());

        // 查询缺陷信息并统计
        List<Map<String, Object>> defectData = queryDefectData(executionDetails);
        Map<String, List<Map<String, Object>>> defectGroups = groupDefectsByDeduplication(defectData, requestDto);

        statistics.setUniqueDefects(defectGroups.get("unique").size());
        statistics.setEnvironmentSpecificDefects(defectGroups.get("environmentSpecific").size());
        statistics.setTotalDefectInstances(defectData.size());

        // 统计环境相关数据
        Set<String> allEnvironments = executionDetails.stream()
                .map(detail -> String.valueOf(detail.get("testCycleEnv")))
                .filter(env -> env != null && !env.equals("null"))
                .collect(Collectors.toSet());

        Set<String> environmentsWithDefects = defectData.stream()
                .map(defect -> String.valueOf(defect.get("env")))
                .filter(env -> env != null && !env.equals("null"))
                .collect(Collectors.toSet());

        statistics.setTotalEnvironments(allEnvironments.size());
        statistics.setEnvironmentsWithDefects(environmentsWithDefects.size());

        double environmentCoverage = allEnvironments.size() > 0 ? 
                (double) environmentsWithDefects.size() / allEnvironments.size() * 100 : 0.0;
        statistics.setEnvironmentCoverage(Math.round(environmentCoverage * 100.0) / 100.0);

        // 判断数据有效性
        boolean hasValidData = statistics.getUniqueTestCases() > 0 && statistics.getTotalExecutions() > 0;
        statistics.setHasValidData(hasValidData);

        // 设置数据说明 - 测试用例执行中的缺陷发现率
        String dataExplanation;
        if (statistics.getTotalExecutions() == 0) {
            if (statistics.getUniqueTestCases() == 0) {
                dataExplanation = "未找到符合条件的测试用例，请检查项目ID、版本号和测试周期ID是否正确";
            } else {
                dataExplanation = "测试用例存在但未执行，无法进行缺陷密度分析";
            }
        } else {
            // 构建详细的执行统计信息（添加环境数量）
            String executionInfo = String.format("%d个独立运行用例 共计执行%d次在%d个周期 %d个环境中", 
                statistics.getUniqueTestCases(), 
                statistics.getTotalExecutions(), 
                statistics.getTotalCycles(),
                statistics.getTotalEnvironments());

            if (statistics.getTotalDefectInstances() == 0) {
                dataExplanation = String.format("测试用例执行中的缺陷发现率：%s，但未发现缺陷，质量状况优秀", executionInfo);
            } else {
                dataExplanation = String.format("测试用例执行中的缺陷发现率：%s，发现%d个缺陷实例", 
                    executionInfo, statistics.getTotalDefectInstances());
            }
        }
        statistics.setDataExplanation(dataExplanation);

        return statistics;
    }

    /**
     * 查询缺陷数据 - 使用2次SQL查询优化性能
     */
    private List<Map<String, Object>> queryDefectData(List<Map<String, Object>> executionDetails) {
        // 第1步：收集所有runCaseId
        List<String> runCaseIds = executionDetails.stream()
                .map(execution -> String.valueOf(execution.get("runCaseId")))
                .filter(runCaseId -> runCaseId != null && !runCaseId.equals("null"))
                .distinct()
                .collect(Collectors.toList());

        if (runCaseIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 第2步：批量查询所有相关缺陷
        List<Map<String, Object>> defects = issueDao.queryDefectsByRunCaseIds(runCaseIds);

        // 构建runCaseId到execution详情的映射，便于后续关联
        Map<String, Map<String, Object>> executionMap = executionDetails.stream()
                .collect(Collectors.toMap(
                    execution -> String.valueOf(execution.get("runCaseId")),
                    execution -> execution,
                    (existing, replacement) -> existing // 如果有重复key，保留第一个
                ));

        // 第3步：为每个缺陷添加测试用例相关信息
        List<Map<String, Object>> allDefects = new ArrayList<>();
        for (Map<String, Object> defect : defects) {
            String runCaseId = String.valueOf(defect.get("runcase_id"));
            Map<String, Object> execution = executionMap.get(runCaseId);

            if (execution != null) {
                // 添加测试用例相关信息到缺陷数据中
                defect.put("testCaseId", execution.get("testCaseId"));
                defect.put("testCaseTitle", execution.get("testCaseTitle"));
                defect.put("version", execution.get("version"));
                defect.put("testCycleId", execution.get("testCycleId"));
                defect.put("testCycleTitle", execution.get("testCycleTitle"));
                defect.put("testCycleEnv", execution.get("testCycleEnv"));
                defect.put("testCycleVersion", execution.get("testCycleVersion"));
                defect.put("executionStatus", execution.get("executionStatus"));
                defect.put("executionTime", execution.get("executionTime"));
                defect.put("runCount", execution.get("runCount"));
                defect.put("runCaseId", runCaseId);

                allDefects.add(defect);
            }
        }

        return allDefects;
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
     * 根据缺陷密度判断质量等级
     */
    private String getQualityLevel(double density, boolean hasValidData) {
        // 如果没有有效数据，返回待分析
        if (!hasValidData) {
            return "待分析";
        }

        // 有有效数据时，根据缺陷密度判断等级
        if (density < 5) {
            return "优秀";
        } else if (density < 10) {
            return "良好";
        } else if (density < 15) {
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

    private String determineQualityLevel(double defectDensity) {
        if (defectDensity == 0.0) {
            return "优秀";
        } else if (defectDensity <= 2.0) {
            return "良好";
        } else if (defectDensity <= 5.0) {
            return "一般";
        } else {
            return "较差";
        }
    }

    /**
     * 构建测试用例详细信息
     */
    private List<DefectDensityResponseDto.TestCaseDetailDto> buildTestCaseDetails(
            List<Map<String, Object>> executionDetails, 
            Map<String, List<Map<String, Object>>> defectsByRunCaseId) {

        // 按测试用例ID分组
        Map<Long, List<Map<String, Object>>> groupedByTestCase = executionDetails.stream()
                .collect(Collectors.groupingBy(detail -> 
                    Long.valueOf(detail.get("testCaseId").toString())));

        List<DefectDensityResponseDto.TestCaseDetailDto> testCaseDetails = new ArrayList<>();

        for (Map.Entry<Long, List<Map<String, Object>>> entry : groupedByTestCase.entrySet()) {
            Long testCaseId = entry.getKey();
            List<Map<String, Object>> testCaseExecutions = entry.getValue();

            DefectDensityResponseDto.TestCaseDetailDto testCaseDetail = new DefectDensityResponseDto.TestCaseDetailDto();
            testCaseDetail.setTestCaseId(testCaseId);

            // 从第一个执行记录获取基本信息
            Map<String, Object> firstExecution = testCaseExecutions.get(0);
            testCaseDetail.setTestCaseTitle((String) firstExecution.get("testCaseTitle"));
            testCaseDetail.setVersion((String) firstExecution.get("version"));
            testCaseDetail.setExecutionCount(testCaseExecutions.size());

            // 获取最新执行时间
            Date latestExecutionTime = testCaseExecutions.stream()
                    .map(exec -> {
                        Object timeObj = exec.get("executionTime");
                        if (timeObj instanceof java.time.LocalDateTime) {
                            // 将LocalDateTime转换为Date
                            java.time.LocalDateTime ldt = (java.time.LocalDateTime) timeObj;
                            return java.sql.Timestamp.valueOf(ldt);
                        } else if (timeObj instanceof Date) {
                            return (Date) timeObj;
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .max(Date::compareTo)
                    .orElse(null);

            Object executionStatus = firstExecution.get("executionStatus");
            testCaseDetail.setLastExecutionStatus(convertExecutionStatusToText(executionStatus));

            // 统计该测试用例的缺陷数量
            int defectCount = 0;
            Set<String> testCycles = new HashSet<>();

            for (Map<String, Object> execution : testCaseExecutions) {
                String runCaseId = execution.get("runCaseId").toString();
                List<Map<String, Object>> defects = defectsByRunCaseId.get(runCaseId);
                if (defects != null) {
                    defectCount += defects.size();
                }

                String testCycleTitle = (String) execution.get("testCycleTitle");
                if (testCycleTitle != null) {
                    testCycles.add(testCycleTitle);
                }
            }

            testCaseDetail.setDefectCount(defectCount);
            testCaseDetail.setTestCycles(new ArrayList<>(testCycles));

            testCaseDetails.add(testCaseDetail);
        }

        log.info("构建了{}个测试用例的详细信息", testCaseDetails.size());
        return testCaseDetails;
    }

    /**
     * 构建测试周期详细信息
     */
    private List<DefectDensityResponseDto.TestCycleDetailDto> buildTestCycleDetails(
            List<Map<String, Object>> executionDetails, 
            Map<String, List<Map<String, Object>>> defectsByRunCaseId) {

        // 按测试周期ID分组
        Map<Long, List<Map<String, Object>>> groupedByTestCycle = executionDetails.stream()
                .collect(Collectors.groupingBy(detail -> 
                    Long.valueOf(detail.get("testCycleId").toString())));

        List<DefectDensityResponseDto.TestCycleDetailDto> testCycleDetails = new ArrayList<>();

        for (Map.Entry<Long, List<Map<String, Object>>> entry : groupedByTestCycle.entrySet()) {
            Long testCycleId = entry.getKey();
            List<Map<String, Object>> executions = entry.getValue();

            DefectDensityResponseDto.TestCycleDetailDto testCycleDetail = new DefectDensityResponseDto.TestCycleDetailDto();
            testCycleDetail.setTestCycleId(testCycleId);

            // 从第一个执行记录获取基本信息
            Map<String, Object> firstExecution = executions.get(0);
            testCycleDetail.setTestCycleTitle((String) firstExecution.get("testCycleTitle"));
            testCycleDetail.setTestCycleVersion((String) firstExecution.get("testCycleVersion"));
            testCycleDetail.setTestCycleEnv((String) firstExecution.get("testCycleEnv"));
            testCycleDetail.setTotalExecutions(executions.size());

            // 统计独立测试用例数量
            Set<Long> uniqueTestCases = executions.stream()
                    .map(execution -> Long.valueOf(execution.get("testCaseId").toString()))
                    .collect(Collectors.toSet());
            testCycleDetail.setTestCaseCount(uniqueTestCases.size());

            // 统计该测试周期的缺陷数量
            int defectCount = 0;
            for (Map<String, Object> execution : executions) {
                String runCaseId = execution.get("runCaseId").toString();
                List<Map<String, Object>> defects = defectsByRunCaseId.get(runCaseId);
                if (defects != null) {
                    defectCount += defects.size();
                }
            }

            testCycleDetail.setDefectCount(defectCount);
            testCycleDetails.add(testCycleDetail);
        }

        log.info("构建了{}个测试周期的详细信息", testCycleDetails.size());
        return testCycleDetails;
    }

    /**
     * 将执行状态转换为文本
     */
    private String convertExecutionStatusToText(Object statusObj) {
        if (statusObj == null) {
            return "未执行";
        }

        String status = statusObj.toString();
        switch (status) {
            case "1": return "通过";
            case "2": return "失败"; 
            case "3": return "跳过";
            case "4": 
            case "4.1":
            case "4.2": 
            case "4.3":
            case "4.4":
            case "4.5": return "阻塞";
            case "5": return "未执行";
            case "6": return "未完成";
            default: return "其他";
        }
    }
}