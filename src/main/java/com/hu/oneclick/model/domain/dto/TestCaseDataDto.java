package com.hu.oneclick.model.domain.dto;

import lombok.Data;

import java.util.Date;

@Data
public class TestCaseDataDto {
    private Long id;
    private Long testCaseId;
    private Integer runCount;
    private Integer runStatus;
    private Date updateTime;
    private String createUserId;
    private String updateUserId;
    private String caseRunDuration;
    private String caseTotalPeriod;

    // Test case fields
    private Long projectId;
    private String title;
    private String priority;
    private String feature;
    private String description;
    private Date executeTime;
    private String browser;
    private String platform;
    private String version;
    private String caseCategory;
    private String testType;
    private String testCondition;
    private String env;
    private String externalLinkId;
    private Integer lastRunStatus;
    private String module;
    private String testDevice;
    private String testData;
    private String testMethod;
    private String testStatus;
    private String reportTo;
    private String testcaseExpand;
    private String remarks;
}