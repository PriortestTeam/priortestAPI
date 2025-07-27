package com.hu.oneclick.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hu.oneclick.model.entity.TestCasesExecution;
import com.hu.oneclick.model.domain.dto.ExecuteTestCaseDto;
import com.hu.oneclick.model.domain.dto.ExecuteTestCaseRunDto;
import com.hu.oneclick.model.domain.dto.TestCaseRunDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TestCycleTcDao extends BaseMapper<TestCasesExecution> {
    int addTestCaseExecution(String userId, ExecuteTestCaseDto executeTestCaseDto);

    List<ExecuteTestCaseDto> queryList(ExecuteTestCaseRunDto executeTestCaseRunDto);

    int upExecuteStatusCode(TestCaseRunDto testCaseRunDto, int runCount, @Param("testCaseStepId") Long testCaseStepId);

    ExecuteTestCaseDto getLatest(TestCaseRunDto testCaseRunDto);

    int getIsFlag(TestCaseRunDto testCaseRunDto, int runCount);

    /**
     * 根据版本和测试周期查询执行详情
     */
    List<Map<String, Object>> getExecutionDetailsByVersionsAndCycles(@Param("projectId") Long projectId,
                                                                     @Param("majorVersion") List<String> majorVersion,
                                                                     @Param("includeVersions") List<String> includeVersions,
                                                                     @Param("testCycleIds") List<Long> testCycleIds);
}