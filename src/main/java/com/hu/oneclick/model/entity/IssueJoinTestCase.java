package com.hu.oneclick.model.entity;

import java.io.Serializable;

/**
 * 缺陷关联测试用例(IssueJoinTestCase)实体类
 *
 * @author makejava
 * @since 2021-02-18 14:15:05
 */
public class IssueJoinTestCase implements Serializable {
    private static final long serialVersionUID = 948868735634832580L;

    @TableId(type = IdType.INPUT)
    private String issueId;

    private String testCaseId;


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

}
