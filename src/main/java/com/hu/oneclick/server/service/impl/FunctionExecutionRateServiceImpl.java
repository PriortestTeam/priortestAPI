package com.hu.oneclick.server.service.impl;

import com.hu.oneclick.dao.TestCaseDao;
import com.hu.oneclick.model.domain.dto.*;
import com.hu.oneclick.server.service.FunctionExecutionRateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 功能执行率报表服务实现
 */
@Service
public class FunctionExecutionRateServiceImpl implements FunctionExecutionRateService {

    private static final Logger logger = LoggerFactory.getLogger(FunctionExecutionRateServiceImpl.class);

    @Resource
    private TestCaseDao testCaseDao;

    @Override
    public FunctionExecutionRateResponseDto getFunctionExecutionRate(FunctionExecutionRateRequestDto request) {
        try {
            Long projectId = request.getProjectId();
            String majorVersionStr = request.getMajorVersion();
            List<String> majorVersion = Arrays.asList(majorVersionStr);
            List<String> includeVersions = request.getIncludeVersions();
            List<Long> testCycleIds = request.getTestCycleIds();

            logger.info("查询参数 - 项目ID：{}，主版本：{}，包含版本：{}，测试周期：{}", 
                       projectId, majorVersionStr, includeVersions, testCycleIds);

            // 1. 统计计划数（主版本下所有测试用例总数）
            Integer totalPlannedCount = testCaseDao.countPlannedTestCasesByVersions(projectId, majorVersion);
            logger.info("SQL查询计划数 - 使用版本：{}，结果：{}", majorVersion, totalPlannedCount);

            // 2. 查询已执行的测试用例数（去重）
            Integer actualExecutedCount = testCaseDao.countExecutedTestCasesByVersionsAndCycles(
                projectId, majorVersion, includeVersions, testCycleIds
            );
            logger.info("SQL查询执行数 - 使用版本：{}，测试周期：{}，结果：{}", 
                       majorVersion, testCycleIds, actualExecutedCount);

            // 3. 计算执行率
            BigDecimal executionRate = BigDecimal.ZERO;
            if (totalPlannedCount != null && totalPlannedCount > 0 && actualExecutedCount != null) {
                executionRate = BigDecimal.valueOf(actualExecutedCount)
                        .divide(BigDecimal.valueOf(totalPlannedCount), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
            }
            logger.info("执行率计算 - {}÷{}×100% = {}%", actualExecutedCount, totalPlannedCount, executionRate);

            // 4. 获取执行详情
            List<Map<String, Object>> executionDetailMaps = testCaseDao.queryExecutionDetails(
                projectId, majorVersion, includeVersions, testCycleIds);
            logger.info("执行详情查询结果数量：{}", executionDetailMaps != null ? executionDetailMaps.size() : 0);

            if (executionDetailMaps != null && !executionDetailMaps.isEmpty()) {
                logger.info("执行详情示例数据：{}", executionDetailMaps.get(0));
            }

            // 5. 构建执行摘要
            ExecutionSummaryDto executionSummary = buildExecutionSummary(executionDetailMaps);

            // 6. 构建测试周期执行详情
            List<CycleExecutionDetailDto> cycleExecutionDetails = buildCycleExecutionDetails(executionDetailMaps);

            // 7. 构建查询条件记录
            QueryConditionsDto queryConditions = new QueryConditionsDto();
            queryConditions.setProjectId(projectId.toString());
            queryConditions.setMajorVersion(majorVersionStr);
            queryConditions.setIncludeVersions(includeVersions);
            queryConditions.setTestCycleIds(testCycleIds);

            // 8. 构建响应
            FunctionExecutionRateResponseDto responseDto = new FunctionExecutionRateResponseDto();
            responseDto.setVersions(Arrays.asList(majorVersionStr));
            responseDto.setTotalPlannedCount(totalPlannedCount != null ? totalPlannedCount : 0);
            responseDto.setActualExecutedCount(actualExecutedCount != null ? actualExecutedCount : 0);
            responseDto.setExecutionRate(executionRate);
            responseDto.setQueryConditions(queryConditions);
            responseDto.setExecutionSummary(executionSummary);
            responseDto.setCycleExecutionDetails(cycleExecutionDetails);

            logger.info("最终响应数据 - 计划：{}，执行：{}，执行率：{}%，周期详情数量：{}", 
                       responseDto.getTotalPlannedCount(), 
                       responseDto.getActualExecutedCount(),
                       responseDto.getExecutionRate(),
                       cycleExecutionDetails.size());

            return responseDto;

        } catch (Exception e) {
            logger.error("获取功能执行率报表失败", e);
            throw new RuntimeException("获取功能执行率报表失败：" + e.getMessage(), e);
        }
    }

    /**
     * 构建执行摘要
     */
    private ExecutionSummaryDto buildExecutionSummary(List<Map<String, Object>> executionDetailMaps) {
        ExecutionSummaryDto summary = new ExecutionSummaryDto();

        if (executionDetailMaps == null || executionDetailMaps.isEmpty()) {
            summary.setPassCount(0);
            summary.setFailCount(0);
            summary.setBlockedCount(0);
            summary.setSkippedCount(0);
            summary.setNotExecutedCount(0);
            return summary;
        }

        // 按执行状态分组统计
        Map<String, Long> statusCounts = executionDetailMaps.stream()
                .filter(map -> map.get("executionStatus") != null)
                .collect(Collectors.groupingBy(
                    map -> getStatusCategory(map.get("executionStatus")),
                    Collectors.counting()
                ));

        summary.setPassCount(statusCounts.getOrDefault("PASS", 0L).intValue());
        summary.setFailCount(statusCounts.getOrDefault("FAIL", 0L).intValue());
        summary.setBlockedCount(statusCounts.getOrDefault("BLOCKED", 0L).intValue());
        summary.setSkippedCount(statusCounts.getOrDefault("SKIP", 0L).intValue());
        summary.setNotExecutedCount(statusCounts.getOrDefault("NOT_EXECUTED", 0L).intValue());

        logger.info("执行摘要统计 - 通过：{}，失败：{}，阻塞：{}，跳过：{}，未执行：{}", 
                   summary.getPassCount(), summary.getFailCount(), summary.getBlockedCount(),
                   summary.getSkippedCount(), summary.getNotExecutedCount());

        return summary;
    }

    /**
     * 根据执行状态获取状态分类
     */
    private String getStatusCategory(Object statusObj) {
        if (statusObj == null) {
            return "NOT_EXECUTED";
        }

        String status = statusObj.toString();
        switch (status) {
            case "1":
                return "PASS";
            case "2":
                return "FAIL";
            case "3":
                return "SKIP";
            case "4":
            case "4.1":
            case "4.2":
            case "4.3":
            case "4.4":
            case "4.5":
                return "BLOCKED";
            default:
                return "NOT_EXECUTED";
        }
    }

    /**
     * 构建测试周期执行详情
     */
    private List<CycleExecutionDetailDto> buildCycleExecutionDetails(List<Map<String, Object>> executionDetailMaps) {
        List<CycleExecutionDetailDto> cycleDetails = new ArrayList<>();

        if (executionDetailMaps == null || executionDetailMaps.isEmpty()) {
            logger.info("执行详情为空，返回空的周期详情列表");
            return cycleDetails;
        }

        // 按测试周期ID分组
        Map<Long, List<Map<String, Object>>> groupedByTestCycle = executionDetailMaps.stream()
                .filter(map -> map.get("testCycleId") != null)
                .collect(Collectors.groupingBy(map -> {
                    Object testCycleId = map.get("testCycleId");
                    return testCycleId != null ? Long.valueOf(testCycleId.toString()) : 0L;
                }));

        logger.info("按测试周期分组结果，周期数量：{}", groupedByTestCycle.size());

        for (Map.Entry<Long, List<Map<String, Object>>> entry : groupedByTestCycle.entrySet()) {
            List<Map<String, Object>> executions = entry.getValue();

            if (executions.isEmpty()) {
                continue;
            }

            // 从第一个执行记录中获取测试周期信息
            Map<String, Object> firstExecution = executions.get(0);
            Long testCycleId = (Long) firstExecution.get("testCycleId");

            if (testCycleId == null) {
                continue;
            }

            CycleExecutionDetailDto cycleDto = new CycleExecutionDetailDto();
            cycleDto.setTestCycleId(testCycleId);
            cycleDto.setTestCycleTitle((String) firstExecution.get("testCycleTitle"));
            cycleDto.setTestCycleEnv((String) firstExecution.get("testCycleEnv"));

            // 计算该周期的总测试用例数和已执行数
            int totalTestCases = executions.size();
            int executedTestCases = (int) executions.stream()
                    .filter(map -> map.get("executionStatus") != null)
                    .count();

            cycleDto.setTotalTestCases(totalTestCases);
            cycleDto.setExecutedTestCases(executedTestCases);

            // 计算执行率
            BigDecimal executionRate = BigDecimal.ZERO;
            if (totalTestCases > 0) {
                executionRate = BigDecimal.valueOf(executedTestCases)
                        .divide(BigDecimal.valueOf(totalTestCases), 2, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
            }
            cycleDto.setExecutionRate(executionRate);

            cycleDetails.add(cycleDto);

            logger.info("测试周期详情 - ID：{}，标题：{}，总用例：{}，已执行：{}，执行率：{}%", 
                       testCycleId, cycleDto.getTestCycleTitle(), totalTestCases, executedTestCases, executionRate);
        }

        return cycleDetails;
    }
}