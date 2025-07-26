
package com.hu.oneclick.server.service.impl;

import com.hu.oneclick.dao.TestCaseDao;
import com.hu.oneclick.model.domain.dto.*;
import com.hu.oneclick.server.service.FunctionExecutionRateService;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 功能执行率报表服务实现
 */
@Service
public class FunctionExecutionRateServiceImpl implements FunctionExecutionRateService {

    @Resource
    private TestCaseDao testCaseDao;

    @Override
    public FunctionExecutionRateResponseDto getFunctionExecutionRate(FunctionExecutionRateRequestDto requestDto) {
        FunctionExecutionRateResponseDto responseDto = new FunctionExecutionRateResponseDto();
        
        // 1. 查询计划测试用例总数
        Integer totalPlannedCount = testCaseDao.countPlannedTestCasesByVersions(
            requestDto.getProjectId(), 
            requestDto.getVersions()
        );
        
        // 2. 查询实际执行数量
        Integer actualExecutedCount = testCaseDao.countExecutedTestCasesByVersionsAndCycles(
            requestDto.getProjectId(),
            requestDto.getVersions(), 
            requestDto.getTestCycleIds()
        );
        
        // 3. 计算执行率
        BigDecimal executionRate = BigDecimal.ZERO;
        if (totalPlannedCount != null && totalPlannedCount > 0) {
            executionRate = BigDecimal.valueOf(actualExecutedCount == null ? 0 : actualExecutedCount)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalPlannedCount), 2, RoundingMode.HALF_UP);
        }
        
        // 4. 查询详细执行信息
        List<Map<String, Object>> executionDetailMaps = testCaseDao.queryExecutionDetails(
            requestDto.getProjectId(),
            requestDto.getVersions(),
            requestDto.getTestCycleIds()
        );
        
        // 5. 组装详细执行信息
        List<ExecutionDetailDto> executionDetails = buildExecutionDetails(executionDetailMaps);
        
        // 6. 设置响应数据
        responseDto.setVersions(requestDto.getVersions());
        responseDto.setTotalPlannedCount(totalPlannedCount == null ? 0 : totalPlannedCount);
        responseDto.setActualExecutedCount(actualExecutedCount == null ? 0 : actualExecutedCount);
        responseDto.setExecutionRate(executionRate);
        responseDto.setExecutionDetails(executionDetails);
        
        return responseDto;
    }

    /**
     * 构建执行详情列表
     */
    private List<ExecutionDetailDto> buildExecutionDetails(List<Map<String, Object>> executionDetailMaps) {
        // 按测试用例ID分组
        Map<Long, List<Map<String, Object>>> groupedByTestCase = executionDetailMaps.stream()
            .filter(map -> map.get("testCaseId") != null)
            .collect(Collectors.groupingBy(map -> Long.valueOf(map.get("testCaseId").toString())));
        
        List<ExecutionDetailDto> executionDetails = new ArrayList<>();
        
        for (Map.Entry<Long, List<Map<String, Object>>> entry : groupedByTestCase.entrySet()) {
            Long testCaseId = entry.getKey();
            List<Map<String, Object>> executions = entry.getValue();
            
            if (executions.isEmpty()) continue;
            
            Map<String, Object> firstExecution = executions.get(0);
            
            ExecutionDetailDto detailDto = new ExecutionDetailDto();
            detailDto.setTestCaseId(testCaseId);
            detailDto.setTestCaseTitle((String) firstExecution.get("testCaseTitle"));
            detailDto.setVersion((String) firstExecution.get("version"));
            
            // 判断是否已执行（只要有一次执行状态在有效范围内就算已执行）
            boolean executed = executions.stream()
                .anyMatch(exec -> {
                    Object status = exec.get("executionStatus");
                    if (status == null) return false;
                    double statusValue = Double.parseDouble(status.toString());
                    return statusValue == 1 || statusValue == 2 || statusValue == 3 || 
                           statusValue == 4 || (statusValue >= 4.1 && statusValue <= 4.5);
                });
            
            detailDto.setExecuted(executed);
            detailDto.setExecutionCount(executions.size());
            
            // 获取最后执行时间
            LocalDateTime lastExecutionTime = executions.stream()
                .map(exec -> {
                    Object time = exec.get("lastExecutionTime");
                    return time != null ? (LocalDateTime) time : null;
                })
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);
            detailDto.setLastExecutionTime(lastExecutionTime);
            
            // 构建测试周期执行历史
            List<TestCycleExecutionDto> testCycleExecutions = executions.stream()
                .filter(exec -> exec.get("testCycleId") != null)
                .map(exec -> {
                    TestCycleExecutionDto cycleExecDto = new TestCycleExecutionDto();
                    cycleExecDto.setTestCycleId(Long.valueOf(exec.get("testCycleId").toString()));
                    cycleExecDto.setTestCycleTitle((String) exec.get("testCycleTitle"));
                    cycleExecDto.setTestCycleEnv((String) exec.get("testCycleEnv"));
                    cycleExecDto.setExecutionTime((LocalDateTime) exec.get("executionTime"));
                    
                    Object status = exec.get("executionStatus");
                    if (status != null) {
                        cycleExecDto.setExecutionStatus(Integer.valueOf(status.toString().split("\\.")[0]));
                        cycleExecDto.setExecutionStatusText(getStatusText(Double.parseDouble(status.toString())));
                    }
                    
                    return cycleExecDto;
                })
                .collect(Collectors.toList());
            
            detailDto.setTestCycleExecutions(testCycleExecutions);
            executionDetails.add(detailDto);
        }
        
        return executionDetails;
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
