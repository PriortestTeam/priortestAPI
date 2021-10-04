package com.hu.oneclick.model.domain.dto;

/**
 * @author qingyang
 */
public class ExecuteTestCaseDto {

    private String testCaseId;
    private String testCycleId;
    private String testCaseStepId;


    private Integer stepStatus;
    /**
     * 实际结果
     */
    private String actualResult;

    public String getTestCaseId() {
        return testCaseId;
    }

    public void setTestCaseId(String testCaseId) {
        this.testCaseId = testCaseId;
    }

    public String getTestCycleId() {
        return testCycleId;
    }

    public void setTestCycleId(String testCycleId) {
        this.testCycleId = testCycleId;
    }

    public String getTestCaseStepId() {
        return testCaseStepId;
    }

    public void setTestCaseStepId(String testCaseStepId) {
        this.testCaseStepId = testCaseStepId;
    }

    public Integer getStepStatus() {
        return stepStatus;
    }

    public void setStepStatus(Integer stepStatus) {
        this.stepStatus = stepStatus;
    }

    public String getActualResult() {
        return actualResult;
    }

    public void setActualResult(String actualResult) {
        this.actualResult = actualResult;
    }
}
