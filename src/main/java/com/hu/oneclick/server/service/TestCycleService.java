package com.hu.oneclick.server.service;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.TestCase;
import com.hu.oneclick.model.domain.TestCycle;
import com.hu.oneclick.model.domain.TestCycleJoinTestCase;
import com.hu.oneclick.model.domain.dto.ExecuteTestCaseDto;
import com.hu.oneclick.model.domain.dto.LeftJoinDto;
import com.hu.oneclick.model.domain.dto.SignOffDto;
import com.hu.oneclick.model.domain.dto.TestCycleDto;

import java.util.List;
import java.util.Map;

public interface TestCycleService {

    Resp< List<LeftJoinDto>> queryTitles(String projectId, String title);

    Resp<TestCycle> queryById(String id);

    Resp<List<TestCycle>> queryList(TestCycleDto testCycle);

    Resp<String> insert(TestCycle testCycle);

    Resp<String> update(TestCycle testCycle);

    Resp<String> delete(String id);



    Resp<List<TestCase>> queryBindCaseList(String testCycleId);

    Resp<String> bindCaseInsert(TestCycleJoinTestCase testCycleJoinTestCase);

    Resp<String> bindCaseDelete(String testCaseId);

    Resp<String> executeTestCase(ExecuteTestCaseDto executeTestCaseDto);

    Resp<List<String>> getTestCycleVersion(String projectId, String env, String version);

    List<Map<String, String>> getAllTestCycle(SignOffDto signOffDto);
}
