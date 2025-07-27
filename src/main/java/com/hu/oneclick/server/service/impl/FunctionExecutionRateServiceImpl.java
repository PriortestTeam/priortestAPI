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
        // 按测试周期ID分组
        Map<Long, List<Map<String, Object>>> groupedByCycle = executionDetailMaps.stream()
            .filter(map -> map.get("testCycleId") != null)
            .collect(Collectors.groupingBy(map -> Long.valueOf(map.get("testCycleId").toString())));

        List<CycleExecutionDetailDto> cycleDetails = new ArrayList<>();

        for (Map.Entry<Long, List<Map<String, Object>>> entry : groupedByCycle.entrySet()) {
            Long testCycleId = entry.getKey();
            List<Map<String, Object>> executions = entry.getValue();

            if (executions.isEmpty()) continue;

            Map<String, Object> firstExecution = executions.get(0);
            
            CycleExecutionDetailDto cycleDetail = new CycleExecutionDetailDto();
            cycleDetail.setTestCycleId(testCycleId);
            cycleDetail.setTestCycleTitle(String.valueOf(firstExecution.get("testCycleTitle")));
            cycleDetail.setTestCycleEnv(String.valueOf(firstExecution.get("testCycleEnv")));
            
            // 统计该周期下的执行情况
            int totalInCycle = executions.size();
            long executedInCycle = executions.stream()
                .filter(exec -> exec.get("executionStatus") != null)
                .count();
            
            cycleDetail.setTotalTestCases(totalInCycle);
            cycleDetail.setExecutedTestCases((int) executedInCycle);
            
            if (totalInCycle > 0) {
                BigDecimal cycleRate = BigDecimal.valueOf(executedInCycle)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalInCycle), 2, RoundingMode.HALF_UP);
                cycleDetail.setExecutionRate(cycleRate);
            } else {
                cycleDetail.setExecutionRate(BigDecimal.ZERO);
            }
            
            cycleDetails.add(cycleDetail);
        }

        return cycleDetails;

            CycleExecutionDetailDto cycleDto = new CycleExecutionDetailDto();
            cycleDto.setTestCycleId(testCycleId);
            cycleDto.setTestCycleTitle((String) firstExecution.get("testCycleTitle"));
            cycleDto.setTestCycleEnv((String) firstExecution.get("testCycleEnv"));

            Object currentRelease = firstExecution.get("currentRelease");
            cycleDto.setCurrentRelease(currentRelease != null ? Integer.valueOf(currentRelease.toString()) : 0);

            Object released = firstExecution.get("released");
            cycleDto.setReleased(released != null ? Integer.valueOf(released.toString()) : 0);

            // 设置测试周期版本
            cycleDto.setVersion((String) firstExecution.get("testCycleVersion"));

            cycleDto.setExecutedCaseCount(executions.size());

            // 构建执行用例详情列表
            List<ExecutedCaseDto> executedCases = executions.stream()
                .map(exec -> {
                    ExecutedCaseDto caseDto = new ExecutedCaseDto();

                    Object testCaseId = exec.get("testCaseId");
                    caseDto.setTestCaseId(testCaseId != null ? Long.valueOf(testCaseId.toString()) : null);

                    Object runCaseId = exec.get("runCaseId");
                    if (runCaseId != null && !runCaseId.toString().isEmpty()) {
                        caseDto.setRunCaseId(Long.valueOf(runCaseId.toString()));
                    } else {
                        caseDto.setRunCaseId(null);
                    }

                    Object runStatus = exec.get("executionStatus");
                    caseDto.setRunStatus(runStatus != null ? Integer.valueOf(runStatus.toString()) : null);

                    caseDto.setVersion((String) exec.get("version"));
                    caseDto.setTestCaseTitle((String) exec.get("testCaseTitle"));

                    Object time = exec.get("lastExecutionTime");
                    caseDto.setExecutionTime(time != null ? (LocalDateTime) time : null);

                    return caseDto;
                })
                .collect(Collectors.toList());

            cycleDto.setExecutedCases(executedCases);
            cycleDetails.add(cycleDto);
        }

        return cycleDetails;
    }

    /**
     * 构建执行摘要
     */
    private ExecutionSummaryDto buildExecutionSummary(List<Map<String, Object>> executionDetailMaps) {
        ExecutionSummaryDto summary = new ExecutionSummaryDto();

        int passCount = 0, failCount = 0, blockedCount = 0, skippedCount = 0, notExecutedCount = 0;

        for (Map<String, Object> exec : executionDetailMaps) {
            Object status = exec.get("executionStatus");
            if (status == null) {
                notExecutedCount++;
                continue;
            }

            double statusValue = Double.parseDouble(status.toString());
            if (statusValue == 1) {
                passCount++;
            } else if (statusValue == 2) {
                failCount++;
            } else if (statusValue == 3) {
                skippedCount++;
            } else if (statusValue >= 4 && statusValue <= 4.5) {
                blockedCount++;
            } else {
                notExecutedCount++;
            }
        }

        summary.setPassCount(passCount);
        summary.setFailCount(failCount);
        summary.setBlockedCount(blockedCount);
        summary.setSkippedCount(skippedCount);
        summary.setNotExecutedCount(notExecutedCount);

        return summary;
    }

    /**
     * 根据状态码获取状态文本
     */
    private String getStatusText(double statusCode) {
        if (statusCode == 1) return "PASS";
        if (statusCode == 2) return "FAIL";
        if (statusCode == 3) return "SKIP";
        if (statusCode == 4) return "BLOCKED";
        if (statusCode == 4.1) return "ENV_BLOCKED";
        if (statusCode == 4.2) return "DATA_BLOCKED";
        if (statusCode == 4.3) return "DEPENDENCY_BLOCKED";
        if (statusCode == 4.4) return "DEFECT_BLOCKED";
        if (statusCode == 4.5) return "THIRD_PARTY_BLOCKED";
        if (statusCode == 0) return "INVALID";
        if (statusCode == 0.1) return "CASE_NEED_MODIFY";
        if (statusCode == 0.2) return "ENV_NOT_SUPPORT";
        if (statusCode == 0.3) return "CONDITION_NOT_READY";
        if (statusCode == 5) return "NO_RUN";
        if (statusCode == 6) return "NOT_COMPLETED";
        return "UNKNOWN";
    }
}