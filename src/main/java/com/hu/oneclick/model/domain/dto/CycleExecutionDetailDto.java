package com.hu.oneclick.model.domain.dto;

import lombok.Data;
import java.time.LocalDateTime;
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
     * 测试环境
     */
    private String testCycleEnv;

    /**
     * 当前发布状态
     */
    private Integer currentRelease;

    /**
     * 是否已发布
     */
    private Integer released;

    /**
     * 执行用例数量
     */
    private Integer executedCaseCount;

    /**
     * 已执行的测试用例列表
     */
    private List<ExecutedCaseDto> executedCases;

    /**
     * 测试周期版本
     */
    private String version;
}