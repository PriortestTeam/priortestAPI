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
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Arrays;

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
            List<String> majorVersion = Arrays.asList(majorVersionStr); // 转换为列表以适配DAO方法
            List<String> includeVersions = request.getIncludeVersions();
            List<Long> testCycleIds = request.getTestCycleIds();

            logger.info("获取功能执行率报表，项目ID：{}，主版本：{}，包含版本：{}，测试周期：{}",
                       projectId, majorVersionStr, includeVersions, testCycleIds);

            // 1. 统计计划数（主版本下所有测试用例总数）
            logger.info("开始统计计划测试用例总数...");
            Integer totalPlannedCount = testCaseDao.countPlannedTestCasesByVersions(projectId, majorVersion);
            logger.info("计划测试用例总数：{}", totalPlannedCount);

        logger.info("开始统计实际执行测试用例数...");
            // 2. 统计实际执行数（测试用例在包含版本测试周期中的执行数，去重）
            Integer actualExecutedCount = testCaseDao.countExecutedTestCasesByVersionsAndCycles(projectId, majorVersion, includeVersions, testCycleIds);
            logger.info("实际执行测试用例数：{}", actualExecutedCount);

        // 3. 计算执行率
        BigDecimal executionRate = BigDecimal.ZERO;
        if (totalPlannedCount != null && totalPlannedCount > 0) {
            executionRate = BigDecimal.valueOf(actualExecutedCount == null ? 0 : actualExecutedCount)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalPlannedCount), 2, RoundingMode.HALF_UP);
        }

        // 4. 查询详细执行信息
        List<Map<String, Object>> executionDetailMaps = testCaseDao.queryExecutionDetails(projectId, majorVersion, includeVersions, testCycleIds);

        // 5. 组装执行摘要
        ExecutionSummaryDto executionSummary = buildExecutionSummary(executionDetailMaps);

        // 6. 组装测试周期执行详情
        List<CycleExecutionDetailDto> cycleExecutionDetails = buildCycleExecutionDetails(executionDetailMaps);

        // 7. 设置查询条件记录
        QueryConditionsDto queryConditions = new QueryConditionsDto();
        queryConditions.setProjectId(projectId);
        queryConditions.setMajorVersion(majorVersionStr);
        queryConditions.setIncludeVersions(includeVersions);
        queryConditions.setTestCycleIds(testCycleIds);

        // 8. 设置响应数据
        FunctionExecutionRateResponseDto responseDto = new FunctionExecutionRateResponseDto();
        responseDto.setVersions(Arrays.asList(majorVersionStr));
        responseDto.setTotalPlannedCount(totalPlannedCount == null ? 0 : totalPlannedCount);
        responseDto.setActualExecutedCount(actualExecutedCount == null ? 0 : actualExecutedCount);
        responseDto.setExecutionRate(executionRate);
        responseDto.setQueryConditions(queryConditions);
        // 设置执行摘要
        responseDto.setExecutionSummary(executionSummary);
        // 设置测试周期执行详情
        responseDto.setCycleExecutionDetails(cycleExecutionDetails);

        return responseDto;
        } catch (Exception e) {
            logger.error("获取功能执行率报表失败", e);
            throw new RuntimeException("获取功能执行率报表失败：" + e.getMessage(), e);
        }
    }

    /**
     * 构建测试周期执行详情列表
     */
    private List<CycleExecutionDetailDto> buildCycleExecutionDetails(List<Map<String, Object>> executionDetailMaps) {
        List<CycleExecutionDetailDto> cycleDetails = new ArrayList<>();

        if (executionDetailMaps == null || executionDetailMaps.isEmpty()) {
            return cycleDetails;
        }

        // 按测试周期ID分组
        Map<Long, List<Map<String, Object>>> groupedByTestCycle = executionDetailMaps.stream()
                .collect(Collectors.groupingBy(map -> {
                    Object testCycleId = map.get("testCycleId");
                    return testCycleId != null ? Long.valueOf(testCycleId.toString()) : 0L;
                }));

        for (Map.Entry<Long, List<Map<String, Object>>> entry : groupedByTestCycle.entrySet()) {
            Long testCycleId = entry.getKey();
            List<Map<String, Object>> executions = entry.getValue();

            if (executions.isEmpty()) {
                continue;
            }

            Map<String, Object> firstExecution = executions.get(0);

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
        }

        return cycleDetails;
    }

    /**
     * 构建执行摘要
     */
    private ExecutionSummaryDto buildExecutionSummary(List<Map<String, Object>> executionDetailMaps) {
        ExecutionSummaryDto summary = new ExecutionSummaryDto();

        if (executionDetailMaps == null || executionDetailMaps.isEmpty()) {
            return summary;
        }

        // 统计各种状态的数量
        Map<String, Long> statusCounts = executionDetailMaps.stream()
                .filter(map -> map.get("executionStatus") != null)
                .collect(Collectors.groupingBy(
                        map -> map.get("executionStatus").toString(),
                        Collectors.counting()
                ));

        summary.setPassCount(statusCounts.getOrDefault("1", 0L).intValue());
        summary.setFailCount(statusCounts.getOrDefault("2", 0L).intValue());
        summary.setBlockedCount(statusCounts.getOrDefault("3", 0L).intValue());
        summary.setSkippedCount(statusCounts.getOrDefault("4", 0L).intValue());

        // 计算未执行数量
        long totalExecuted = statusCounts.values().stream().mapToLong(Long::longValue).sum();
        long totalCases = executionDetailMaps.size();
        summary.setNotExecutedCount((int)(totalCases - totalExecuted));

        return summary;
    }
}