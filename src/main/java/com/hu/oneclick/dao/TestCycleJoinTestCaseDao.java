package com.hu.oneclick.dao;

import com.hu.oneclick.model.domain.TestCase;
import com.hu.oneclick.model.domain.TestCycleJoinTestCase;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.BaseMapper;

import java.util.List;

public interface TestCycleJoinTestCaseDao extends BaseMapper<TestCycleJoinTestCase> {


    int bindCaseDelete(@Param("testCaseId") String testCaseId);

    List<TestCase> queryBindCaseList(String testCycleId);

}
