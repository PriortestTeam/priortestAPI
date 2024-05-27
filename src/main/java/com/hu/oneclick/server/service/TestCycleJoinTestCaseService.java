package com.hu.oneclick.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.TestCycleJoinTestCase;
import com.hu.oneclick.model.domain.dto.TestCycleJoinTestCaseDto;
import com.hu.oneclick.model.domain.dto.TestCycleJoinTestCaseSaveDto;
import com.hu.oneclick.model.domain.vo.TestCycleJoinTestCaseVo;

import java.util.List;

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

    List<Long> saveDataWithIdReturn(TestCycleJoinTestCaseSaveDto dto);

    /**
     * 严格的保存测试用例，需要验证测试周期 ID 和测试用例 ID 都存在，如果有不存在就失败
     *
     * @param dto save dto 对象
     * @return 一个 Map，成功则返回一个 key 为 id 的 testCycleId 以及一个 key 为 testCaseId 的 Map 列表
     */
    Resp<Object> strictlySaveInstance(TestCycleJoinTestCaseSaveDto dto);

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

    TestCycleJoinTestCaseVo removeTCsFromTestCycle(Long projectId, TestCycleJoinTestCaseSaveDto dto);
}
