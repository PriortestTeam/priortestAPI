package com.hu.oneclick.dao;

import com.hu.oneclick.model.annotation.Page;
import com.hu.oneclick.model.domain.Issue;
import com.hu.oneclick.model.domain.TestCaseExcution;
import tk.mybatis.mapper.common.BaseMapper;

import java.util.List;

public interface TestCaseExcutionDao extends BaseMapper<TestCaseExcution> {

    public int createTestCaseExcutionDate();
    public int update(TestCaseExcution testCaseExcution);

    public int insertIssue(Issue issue);

    public int mergeIssue(Issue issue);

    List<Issue> queryIssueList(Issue issue);

    @Page
    List<TestCaseExcution> queryHistoryByTestCaseId(TestCaseExcution testCaseExcution);

}
