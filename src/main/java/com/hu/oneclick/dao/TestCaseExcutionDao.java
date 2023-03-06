package com.hu.oneclick.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hu.oneclick.model.annotation.Page;
import com.hu.oneclick.model.domain.Issue;
import com.hu.oneclick.model.domain.TestCaseExcution;
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
