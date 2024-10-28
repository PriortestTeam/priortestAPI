package com.hu.oneclick.model.entity;

import com.hu.oneclick.model.base.BaseEntity;

import java.io.Serializable;
import java.util.Date;

public class TestCycleJoinTestStep extends BaseEntity implements Serializable {
    /**
     * 关联testCase id
     */
    private String testCaseId;
    /**
     * 关联testCycleId
     */
    private String testCycleId;
    /**
     * 步骤
     */
    private String step;
    /**
     * 测试日期
     */
    private Date createDate;
    /**
     * 预期结果
     */
    private Integer stepStatus;
    /**
     * 0 未执行， 1 执行失败 2 执行 成功
     */
    private Integer status;

    private Integer runCount;
    private Date updateTime;

    private String runner;
    private String issueId;

    public String getIssueId() {
        return issueId;
    }

    public void setIssueId(String issueId) {
        this.issueId = issueId;
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

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Integer getStepStatus() {
        return stepStatus;
    }

    public void setStepStatus(Integer stepStatus) {
        this.stepStatus = stepStatus;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getRunCount() {
        return runCount;
    }

    public void setRunCount(Integer runCount) {
        this.runCount = runCount;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getRunner() {
        return runner;
    }

    public void setRunner(String runner) {
        this.runner = runner;
    }

}
