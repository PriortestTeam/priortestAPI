package com.hu.oneclick.model.domain.dto;

import lombok.Data;

/**
 * @author qingyang
 */
@Data
public class ExecuteTestCaseDto {

    private String testCaseId;
    private String testCycleId;
    private String testCaseStepId;


    private Integer stepStatus;
    private Integer step;

    private Integer RunDuration;

    private String actualResult;


}
