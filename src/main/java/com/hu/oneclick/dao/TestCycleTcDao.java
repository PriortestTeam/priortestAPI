package com.hu.oneclick.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hu.oneclick.model.entity.TestCasesExecution;
import com.hu.oneclick.model.domain.dto.ExecuteTestCaseDto;
import com.hu.oneclick.model.domain.dto.ExecuteTestCaseRunDto;
import com.hu.oneclick.model.domain.dto.TestCaseRunDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TestCycleTcDao extends BaseMapper<TestCasesExecution> {
    int addTestCaseExecution(String userId, ExecuteTestCaseDto executeTestCaseDto);

    List<ExecuteTestCaseDto> queryList(ExecuteTestCaseRunDto executeTestCaseRunDto);

    int upExecuteStatusCode(TestCaseRunDto testCaseRunDto, int runCount, @Param("testCaseStepId") Long testCaseStepId);

    ExecuteTestCaseDto getLatest(TestCaseRunDto testCaseRunDto);

    int getIsFlag(TestCaseRunDto testCaseRunDto, int runCount);
}
