package com.hu.oneclick.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hu.oneclick.model.entity.TestCase;
import com.hu.oneclick.model.entity.TestCycleJoinTestCase;
import com.hu.oneclick.model.domain.dto.TestCaseRunDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TestCycleJoinTestCaseDao extends BaseMapper<TestCycleJoinTestCase> {

    int bindCaseDelete(@Param("testCaseId") String testCaseId);

    List<TestCase> queryBindCaseList(@Param("testCycleId") String testCycleId);

    /**
     * 查询test case 运行状态
     *
     * @param testCycleId
     * @return
     */
    List<Map<String, String>> queryBindCaseRunStatus(@Param("testCycleId") String testCycleId);

    /**
     * 查询test cycle 执行状态
     *
     * @param testCycleId
     * @return
     */
    List<String> queryTestCycleStatus(String testCycleId);

    List<TestCycleJoinTestCase> queryList(TestCycleJoinTestCase testCycleJoinTestCase);

    List<TestCycleJoinTestCase> queryAllDate(TestCycleJoinTestCase testCycleJoinTestCase);

    int updateTestCycleJoinTestCase(TestCycleJoinTestCase testCycleJoinTestCase);

    void deleteByParam(@Param("projectId") Long projectId, @Param("testCycleId") Long testCycleId,
        @Param("testCaseId") Long testCaseId);

    List<Long> getCaseIdListByCycleId(@Param("testCycleId") Long testCycleId);

    int countByTestCaseIdInt(@Param("caseId") Long caseId, @Param("projectId") Long projectId,
        @Param("cycleId") Long cycleId);

    TestCycleJoinTestCase getCycleJoinTestCaseByCaseId(@Param("caseId") Long caseId, @Param("projectId") Long projectId,
        @Param("cycleId") Long cycleId);

    int updateRunStatus(TestCaseRunDto testCaseRunDto, String userId);


}
