package com.hu.oneclick.model.domain.dto;

import lombok.Data;
import java.math.BigDecimal;

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
     * 该周期总测试用例数
     */
    private Integer totalTestCases;

    /**
     * 该周期已执行测试用例数
     */
    private Integer executedTestCases;

    /**
     * 该周期执行率
     */
    private BigDecimal executionRate;
}