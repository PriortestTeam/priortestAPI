package com.hu.oneclick.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.entity.TestCycle;
import com.hu.oneclick.model.domain.dto.LeftJoinDto;
import com.hu.oneclick.model.domain.dto.SignOffDto;
import com.hu.oneclick.model.domain.dto.TestCycleSaveDto;
import com.hu.oneclick.model.param.TestCycleParam;

import java.util.List;
import java.util.Map;

public interface TestCycleService extends IService<TestCycle> {

    Resp<List<LeftJoinDto>> queryTitles(String projectId, String title);
//
    Resp<TestCycle> queryById(String id);
//
//    Resp<List<TestCycle>> queryList(TestCycleDto testCycle);
//
//    Resp<String> insert(TestCycle testCycle);
//
//    Resp<String> update(TestCycle testCycle);
//
//    Resp<String> delete(String id);
//
//
//    Resp<List<TestCase>> queryBindCaseList(String testCycleId);
//
//    Resp<String> bindCaseInsert(TestCycleJoinTestCase testCycleJoinTestCase);
//
//    Resp<String> bindCaseDelete(String testCaseId);
//
//    Resp<String> executeTestCase(ExecuteTestCaseDto executeTestCaseDto);
//
//    Resp<Map<String, Object>> runTestCycleTc(ExecuteTestCaseDto executeTestCaseDto);
//
//    Resp<Map<String, Object>> excute(ExecuteTestCaseDto executeTestCaseDto);
//
//    Resp<Map<String, Object>> queryIssueByIdOrName(Issue issue);
//
//    Resp<String> mergeIssue(Issue issue);
//
    Resp<List<Map<String, String>>> getTestCycleVersion(String projectId, String env, String version);
//
    List<Map<String, Object>> getAllTestCycle(SignOffDto signOffDto);
//
//    /**
//     * 添加计划
//     *
//     * @Param: [model]
//     * @return: com.hu.oneclick.model.base.Resp<java.lang.String>
//     * @Author: MaSiyi
//     * @Date: 2021/12/9
//     */
//    Resp<String> addSchedule(TestCycleScheduleModel model);
//
    List<String> getTestCycleByProjectIdAndEvn(String projectId, String env, String testCycle);

    List<TestCycle> list(TestCycleParam param);

    TestCycle save(TestCycleSaveDto dto);

    TestCycle update(TestCycleSaveDto dto);

    TestCycle info(Long id);

    void clone(List<Long> ids);
}
