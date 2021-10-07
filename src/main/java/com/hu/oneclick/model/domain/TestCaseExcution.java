package com.hu.oneclick.model.domain;

import com.hu.oneclick.model.base.BaseEntity;

import java.io.Serializable;
import java.util.Date;

public class TestCaseExcution extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 114802398790239711L;

    private String testCycleId;
    private String testCaseId;
    /**
     * 步骤
     */
    private String step;

    private Integer stepStatus;

    private Date updateTime;

    private Integer runCount;

    private String userId;

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

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public Integer getStepStatus() {
        return stepStatus;
    }

    public void setStepStatus(Integer stepStatus) {
        this.stepStatus = stepStatus;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
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
}
