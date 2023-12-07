package com.hu.oneclick.dao;

import com.hu.oneclick.model.domain.dto.ExecuteTestCaseDto;

public interface TestCycleTcDao {
    int addTestCaseExecution(String userId, ExecuteTestCaseDto executeTestCaseDto);
}
