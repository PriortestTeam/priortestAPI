package com.hu.oneclick.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hu.oneclick.model.domain.TestCycleJoinTestCase;
import com.hu.oneclick.model.domain.TestCycleJoinTestStep;
import java.util.List;

public interface TestCycleJoinTestStepDao extends BaseMapper<TestCycleJoinTestCase> {
    List<TestCycleJoinTestStep> queryList(TestCycleJoinTestStep testCycleJoinTestStep);

    int updateByIdsSelective(TestCycleJoinTestStep testCycleJoinTestStep);

    int updateRunCount(TestCycleJoinTestStep testCycleJoinTestStep);
}
