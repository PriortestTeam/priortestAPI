package com.hu.oneclick.dao;

import com.hu.oneclick.model.domain.TestCase;
import com.hu.oneclick.model.domain.TestCycleJoinTestCase;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.BaseMapper;

import java.util.List;
import java.util.Map;

public interface TestCycleJoinTestCaseDao extends BaseMapper<TestCycleJoinTestCase> {


    int bindCaseDelete(@Param("testCaseId") String testCaseId);

    List<TestCase> queryBindCaseList(@Param("testCycleId") String testCycleId);

    /**
     * 查询test case 运行状态
     * @param testCycleId
     * @return
     */
    List<Map<String, String>> queryBindCaseRunStatus(@Param("testCycleId") String testCycleId);
    /**
     * 查询test cycle 执行状态
     * @param testCycleId
     * @return
     */
    List<String> queryTestCycleStatus(String testCycleId);

    List<TestCycleJoinTestCase> queryList(TestCycleJoinTestCase testCycleJoinTestCase);

}
