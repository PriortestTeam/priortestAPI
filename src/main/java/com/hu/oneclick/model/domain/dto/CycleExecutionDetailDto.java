package com.hu.oneclick.model.domain.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 测试周期执行详情DTO
 */
@Data
public class CycleExecutionDetailDto {

    /**
     * 测试周期ID
     */
    private Long testCycleId;

    /**
     * 测试周期标题
     */
    private String testCycleTitle;

    /**
     * 测试周期环境
     */
    private String testCycleEnv;

    /**
     * 测试周期版本
     */
    private String testCycleVersion;

    /**
     * 该周期总测试用例数
     */
    private int totalTestCases;

    /**
     * 该周期已执行测试用例数
     */
    private int executedTestCases;

    /**
     * 该周期执行率
     */
    private BigDecimal executionRate;

    /**
     * 测试用例执行详情列表
     */
    private List<TestCaseExecutionDetailDto> testCaseDetails;
}