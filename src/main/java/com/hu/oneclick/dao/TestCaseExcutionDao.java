package com.hu.oneclick.dao;

import com.hu.oneclick.model.domain.TestCaseExcution;
import com.hu.oneclick.model.domain.TestCycleJoinTestCase;
import tk.mybatis.mapper.common.BaseMapper;

public interface TestCaseExcutionDao extends BaseMapper<TestCaseExcution> {

    public int createTestCaseExcutionDate();

}
