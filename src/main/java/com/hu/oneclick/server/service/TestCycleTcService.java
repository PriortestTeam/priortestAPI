package com.hu.oneclick.server.service;


import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.dto.ExecuteTestCaseDto;


public interface TestCycleTcService {
    Resp<String> runTestCycleTc(ExecuteTestCaseDto executeTestCaseDto);
}
