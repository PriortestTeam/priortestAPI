package com.hu.oneclick.dao;

import com.hu.oneclick.model.domain.dto.ExecuteTestCaseDto;
import com.hu.oneclick.model.domain.dto.ExecuteTestCaseRunDto;

import java.util.List;

public interface TestCycleTcDao {
    int addTestCaseExecution(String userId, ExecuteTestCaseDto executeTestCaseDto);

    List<ExecuteTestCaseDto> queryList(ExecuteTestCaseRunDto executeTestCaseRunDto);
}
