package com.hu.oneclick.server.service;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.TestCase;

import java.util.List;

/**
 * @author qingyang
 */
public interface TestCaseService {

    Resp<TestCase> queryById(String id);

    Resp<List<TestCase>> queryList(TestCase testCase);

    Resp<String> insert(TestCase testCase);

    Resp<String> update(TestCase testCase);

    Resp<String> delete(String id);

}
