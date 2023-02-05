package com.hu.oneclick.dao;

import com.hu.oneclick.model.domain.TestCycleJoinTestCase;
import com.hu.oneclick.model.domain.TestCycleJoinTestStep;
import tk.mybatis.mapper.common.BaseMapper;

import java.util.List;

public interface TestCycleJoinTestStepDao extends BaseMapper<TestCycleJoinTestCase> {
    List<TestCycleJoinTestStep> queryList(TestCycleJoinTestStep testCycleJoinTestStep);

    int update(TestCycleJoinTestStep testCycleJoinTestStep);

    int updateRunCount(TestCycleJoinTestStep testCycleJoinTestStep);
}
