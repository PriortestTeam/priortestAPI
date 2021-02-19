package com.hu.oneclick.model.domain;

import java.io.Serializable;

public class TestCycleJoinTestCase implements Serializable {


    private static final long serialVersionUID = 3210170197758676763L;


    private String testCycleId;
    private String testCaseId;


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
}
