package com.hu.oneclick.model.domain.dto;

/**
 * @author qingyang
 */
public class ExecuteTestCaseDto {

    private String testCaseId;
    private String testCycleId;
    private String testCaseStepId;


    private Integer stepStatus;
    private Integer step;

    private Integer RunDuration;

    public Integer getRunDuration() {
        return RunDuration;
    }

    public void setRunDuration(Integer runDuration) {
        RunDuration = runDuration;
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }



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

}
