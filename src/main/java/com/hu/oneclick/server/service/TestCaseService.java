package com.hu.oneclick.server.service;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.TestCase;

import java.util.List;
import java.util.Map;

/**
 * @author qingyang
 */
public interface TestCaseService {

    Resp< List<Map<String,String>>> queryTitles(String projectId, String title);

    Resp<TestCase> queryById(String id);

    Resp<List<TestCase>> queryList(TestCase testCase);

    Resp<String> insert(TestCase testCase);

    Resp<String> update(TestCase testCase);

    Resp<String> delete(String id);

}
