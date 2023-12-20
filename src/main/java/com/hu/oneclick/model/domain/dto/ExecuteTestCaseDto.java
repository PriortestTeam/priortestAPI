package com.hu.oneclick.model.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class ExecuteTestCaseDto {
    private Long testCaseId;
    private Long testCycleId;
    private String testStep;
    private String expectedResult;
    private String actualResult;
    private String teststepCondition;
    private String testData;
    private String remarks;
    private Long testStepId;
    private int statusCode;
    private String teststepExpand;
    private String projectId;
    private Date createTime;
    private int runCount;
}
