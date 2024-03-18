package com.hu.oneclick.server.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.TestCase;
import com.hu.oneclick.model.domain.TestCycleJoinTestCase;
import com.hu.oneclick.model.domain.dto.TestCaseRunDto;
import com.hu.oneclick.model.domain.dto.TestCycleJoinTestCaseDto;
import com.hu.oneclick.model.domain.dto.TestCycleJoinTestCaseSaveDto;

/**
 * @Author: jhh
 * @Date: 2023/7/8
 */
public interface TestCycleJoinTestCaseService extends IService<TestCycleJoinTestCase> {

    /**
     * 绑定测试用例到测试周期
     *
     * @param dto
     * @return
     */
    Boolean saveInstance(TestCycleJoinTestCaseSaveDto dto);

    /**
     * 删除测试周期绑定的测试用例
     *
     * @param dto
     */
    void deleteInstance(TestCycleJoinTestCaseSaveDto dto);

    List<Long> getCaseIdListByCycleId(Long testCycleId);

    int countCycleIdByCaseId(Long caseId, Long projectId, Long cycleId);

    TestCycleJoinTestCase getCycleJoinTestCaseByCaseId(Long caseId, Long projectId, Long cycleId);

    /**
     * 更改runCaseStatus
     * @param projectId
     * @param testCycleJoinTestCaseDto
     */
    Resp runCaseStatusUpdate(Long projectId, TestCycleJoinTestCaseDto testCycleJoinTestCaseDto);
}
