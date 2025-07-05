package com.hu.oneclick.model.domain.dto;


import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

@Data
public class TestCaseDataDto {
    /**
     * id
     */
    private Long Id;
    /**
     * 关联项目id
     */
    private Long projectId;

    /**
     * 名称
     */
    private String title;

    /**
     * 优先级
     */
    private String priority;

    /**
     * 故事id
     */
    private String feature;

    /**
     * 描述
     */
    private String description;

    /**
     * 执行时间
     */
    private Date executeTime;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * 平台
     */
    private String platform;

    /**
     * 版本
     */
    private String version;

    /**
     * 用例类别
     */
    private String caseCategory;

    /**
     * 用例类型
     */
    private String testType;

    /**
     * 前提条件
     */
    private String testCondition;

    /**
     * 环境
     */
    private String env;

    /**
     * 外部id
     */
    private String externalLinkId;

    /**
     * 最后运行状态
     */
    private Integer lastRunStatus;

    /**
     * 模块
     */
    private String module;

    /**
     * 测试设备
     */
    private String testDevice;

    /**
     * 测试数据
     */
    private String testData;

    /**
     * 测试方法
     */
    private String testMethod;

    /**
     * test_status
     */
    private String testStatus;

    /**
     * 测试执行状态,默认5 未执行
     */
    private int runStatus = 5;

    /**
     * reportTo
     */
    private String reportTo;

    /**
     * testcase_expand
     */
    private String testcaseExpand;

    /**
     * remarks
     */
    private String remarks;

    private int runCount;

    private Date updateTime;

    private BigInteger createUserId;

    private BigInteger updateUserId;

    private BigInteger caseRunDuration;

    private BigInteger caseTotalPeriod;
}
