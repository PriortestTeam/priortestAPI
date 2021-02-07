package com.hu.oneclick.server.service;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.TestCaseStep;

import java.util.List;

/**
 * @author qingyang
 */
public interface TestCaseStepService {


    Resp<TestCaseStep> queryById(String id,String testCaseId);

    Resp<List<TestCaseStep>> queryList(TestCaseStep testCaseStep);

    Resp<String> insert(TestCaseStep testCaseStep);

    Resp<String> update(TestCaseStep testCaseStep);

    Resp<String> delete(String id);


}
