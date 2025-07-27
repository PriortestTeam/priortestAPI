
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

            logger.info("获取功能执行率报表，项目ID：{}，主版本：{}，包含版本：{}，测试周期：{}", 
                       projectId, majorVersionStr, includeVersions, testCycleIds);

            // 1. 统计计划数（主版本下所有测试用例总数）
            Integer totalPlannedCount = testCaseDao.countPlannedTestCasesByVersions(projectId, majorVersion);
            logger.info("计划测试用例总数：{}", totalPlannedCount);

            // 2. 统计实际执行数
            Integer actualExecutedCount = testCaseDao.countExecutedTestCasesByVersionsAndCycles(
                    projectId, majorVersion, includeVersions, testCycleIds);
            logger.info("实际执行测试用例数：{}", actualExecutedCount);

            // 3. 计算执行率
            BigDecimal executionRate = BigDecimal.ZERO;
            if (totalPlannedCount != null && totalPlannedCount > 0) {
                executionRate = BigDecimal.valueOf(actualExecutedCount != null ? actualExecutedCount : 0)
                        .divide(BigDecimal.valueOf(totalPlannedCount), 2, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
            }

            // 4. 获取执行摘要
            ExecutionSummaryDto executionSummary = getExecutionSummary(projectId, majorVersion, includeVersions, testCycleIds);

            // 5. 获取测试周期执行详情
            List<CycleExecutionDetailDto> cycleExecutionDetails = getCycleExecutionDetails(
                    projectId, majorVersion, includeVersions, testCycleIds);

            // 6. 构建查询条件
            QueryConditionsDto queryConditions = new QueryConditionsDto();
            queryConditions.setProjectId(projectId);
            queryConditions.setMajorVersion(majorVersionStr);
            queryConditions.setIncludeVersions(includeVersions);
            queryConditions.setTestCycleIds(testCycleIds);

            // 7. 构建响应
            FunctionExecutionRateResponseDto responseDto = new FunctionExecutionRateResponseDto();
            responseDto.setVersions(Arrays.asList(majorVersionStr));
            responseDto.setTotalPlannedCount(totalPlannedCount != null ? totalPlannedCount : 0);
            responseDto.setActualExecutedCount(actualExecutedCount != null ? actualExecutedCount : 0);
            responseDto.setExecutionRate(executionRate);
            responseDto.setQueryConditions(queryConditions);
            responseDto.setExecutionSummary(executionSummary);
            responseDto.setCycleExecutionDetails(cycleExecutionDetails);

            return responseDto;

        } catch (Exception e) {
            logger.error("获取功能执行率报表失败", e);
            throw new RuntimeException("获取功能执行率报表失败：" + e.getMessage(), e);
        }
    }

    /**
     * 获取执行摘要
     */
    private ExecutionSummaryDto getExecutionSummary(Long projectId, List<String> majorVersion, 
                                                   List<String> includeVersions, List<Long> testCycleIds) {
        try {
            // 获取执行详情数据
            List<Map<String, Object>> executionDetails = testCaseDao.getExecutionDetailsByVersionsAndCycles(
                    projectId, majorVersion, includeVersions, testCycleIds);

            int passCount = 0;
            int failCount = 0;
            int blockedCount = 0;
            int skippedCount = 0;
            int notExecutedCount = 0;

            for (Map<String, Object> detail : executionDetails) {
                String status = (String) detail.get("executionStatus");
                if (status == null) {
                    notExecutedCount++;
                } else {
                    switch (status.toLowerCase()) {
                        case "pass":
                        case "passed":
                            passCount++;
                            break;
                        case "fail":
                        case "failed":
                            failCount++;
                            break;
                        case "blocked":
                            blockedCount++;
                            break;
                        case "skip":
                        case "skipped":
                            skippedCount++;
                            break;
                        default:
                            notExecutedCount++;
                            break;
                    }
                }
            }

            ExecutionSummaryDto summary = new ExecutionSummaryDto();
            summary.setPassCount(passCount);
            summary.setFailCount(failCount);
            summary.setBlockedCount(blockedCount);
            summary.setSkippedCount(skippedCount);
            summary.setNotExecutedCount(notExecutedCount);

            return summary;
        } catch (Exception e) {
            logger.error("获取执行摘要失败", e);
            return new ExecutionSummaryDto();
        }
    }

    /**
     * 获取测试周期执行详情
     */
    private List<CycleExecutionDetailDto> getCycleExecutionDetails(Long projectId, List<String> majorVersion, 
                                                                  List<String> includeVersions, List<Long> testCycleIds) {
        try {
            List<Map<String, Object>> executionDetailMaps = testCaseDao.getExecutionDetailsByVersionsAndCycles(
                    projectId, majorVersion, includeVersions, testCycleIds);

            if (executionDetailMaps == null || executionDetailMaps.isEmpty()) {
                return new ArrayList<>();
            }

            // 按测试周期ID分组
            Map<Long, List<Map<String, Object>>> groupedByTestCycle = executionDetailMaps.stream()
                    .collect(Collectors.groupingBy(map -> {
                        Object testCycleId = map.get("testCycleId");
                        return testCycleId != null ? Long.valueOf(testCycleId.toString()) : 0L;
                    }));

            List<CycleExecutionDetailDto> cycleDetails = new ArrayList<>();

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
        } catch (Exception e) {
            logger.error("获取测试周期执行详情失败", e);
            return new ArrayList<>();
        }
    }
}
