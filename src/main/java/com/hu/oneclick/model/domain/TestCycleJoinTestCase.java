package com.hu.oneclick.model.domain;

import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;

public class TestCycleJoinTestCase implements Serializable {


    private static final long serialVersionUID = 3210170197758676763L;


    private String testCycleId;
    private String testCaseId;
    private Integer runStatus;
    private Integer stepStatus;
    private Integer runDuration;
    private Integer runCount;
    private String userId;
    private Date updateTime;

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getRunStatus() {
        return runStatus;
    }

    public void setRunStatus(Integer runStatus) {
        this.runStatus = runStatus;
    }

    public Integer getStepStatus() {
        return stepStatus;
    }

    public void setStepStatus(Integer stepStatus) {
        this.stepStatus = stepStatus;
    }

    public Integer getRunDuration() {
        return runDuration;
    }

    public void setRunDuration(Integer runDuration) {
        this.runDuration = runDuration;
    }

    public Integer getRunCount() {
        return runCount;
    }

    public void setRunCount(Integer runCount) {
        this.runCount = runCount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Transient
    private String executeStatus;


    public String getTestCycleId() {
        return testCycleId;
    }

    public void setTestCycleId(String testCycleId) {
        this.testCycleId = testCycleId;
    }

    public String getTestCaseId() {
        return testCaseId;
    }

    public void setTestCaseId(String testCaseId) {
        this.testCaseId = testCaseId;
    }

    public String getExecuteStatus() {
        return executeStatus;
    }

    public void setExecuteStatus(String executeStatus) {
        this.executeStatus = executeStatus;
    }
}
