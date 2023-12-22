package com.hu.oneclick.server.service;


import com.github.pagehelper.PageInfo;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.dto.ExecuteTestCaseDto;
import com.hu.oneclick.model.domain.dto.ExecuteTestCaseRunDto;
import com.hu.oneclick.model.domain.dto.TestCaseRunDto;


public interface TestCycleTcService {
    Resp<String> runTestCycleTc(ExecuteTestCaseDto executeTestCaseDto);

    Resp<PageInfo<Object>> runExecuteTestCase(ExecuteTestCaseRunDto executeTestCaseRunDto);

    Resp<String> runTestCase(TestCaseRunDto testCaseRunDto);
}
