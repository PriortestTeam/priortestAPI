package com.hu.oneclick.model.domain.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ExecuteTestCaseDto {
    private String testCaseId;
    private String testCycleId;
    private String testStep;
    private String expectedResult;
    private String actualResult;
    private String teststepCondition;
    private String testData;
    private String remarks;
    private Integer testStepId;
    private String statusCode;
    private String teststepExpand;
    private String projectId;
    private Date createTime;
}
