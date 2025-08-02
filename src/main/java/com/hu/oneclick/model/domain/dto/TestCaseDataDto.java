package com.hu.oneclick.model.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 测试用例数据DTO，包含测试用例和运行信息
 */
@Data
public class TestCaseDataDto {

    /**
     * test_cycle_join_test_case表的ID
     */
    private Long id;

    /**
     * 测试用例ID
     */
    private Long testCaseId;

    /**
     * 运行次数
     */
    private Integer runCount;

    /**
     * 运行状态
     */
    private Integer runStatus;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建用户ID
     */
    private String createUserId;

    /**
     * 更新用户ID
     */
    private String updateUserId;

    /**
     * 用例运行时长
     */
    private String caseRunDuration;

    /**
     * 用例总时长
     */
    private String caseTotalPeriod;
}