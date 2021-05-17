package com.hu.oneclick.server.service;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.TestCase;
import com.hu.oneclick.model.domain.dto.LeftJoinDto;
import com.hu.oneclick.model.domain.dto.TestCaseDto;

import java.util.List;

/**
 * @author qingyang
 */
public interface TestCaseService {

    Resp< List<LeftJoinDto>> queryTitles(String projectId, String title);

    Resp<TestCase> queryById(String id);

    Resp<List<TestCase>> queryList(TestCaseDto testCase);

    Resp<String> insert(TestCase testCase);

    Resp<String> update(TestCase testCase);

    Resp<String> delete(String id);

}
