package com.hu.oneclick.server.service;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.TestCycle;

import java.util.List;
import java.util.Map;

public interface TestCycleService {

    Resp< List<Map<String,String>>> queryTitles(String projectId, String title);

    Resp<TestCycle> queryById(String id);

    Resp<List<TestCycle>> queryList(TestCycle testCycle);

    Resp<String> insert(TestCycle testCycle);

    Resp<String> update(TestCycle testCycle);

    Resp<String> delete(String id);


}
