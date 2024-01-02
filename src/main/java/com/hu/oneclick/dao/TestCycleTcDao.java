package com.hu.oneclick.dao;

import com.hu.oneclick.model.domain.dto.ExecuteTestCaseDto;
import com.hu.oneclick.model.domain.dto.ExecuteTestCaseRunDto;
import com.hu.oneclick.model.domain.dto.TestCaseRunDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TestCycleTcDao {
    int addTestCaseExecution(String userId, ExecuteTestCaseDto executeTestCaseDto);

    List<ExecuteTestCaseDto> queryList(ExecuteTestCaseRunDto executeTestCaseRunDto);

    int upExecuteStatusCode(TestCaseRunDto testCaseRunDto, int runCount, @Param("testCaseStepId") Long testCaseStepId);
    int updateRerunTime(ExecuteTestCaseRunDto executeTestCaseRunDto);
}
