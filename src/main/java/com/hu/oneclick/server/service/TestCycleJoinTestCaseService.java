package com.hu.oneclick.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hu.oneclick.model.domain.TestCycleJoinTestCase;
import com.hu.oneclick.model.domain.dto.TestCycleJoinTestCaseSaveDto;

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

    /**
     * 删除测试周期绑定的测试用例
     *
     * @param dto
     */
    void deleteInstance(TestCycleJoinTestCaseSaveDto dto);

    List<Long> getCaseIdListByCycleId(Long testCycleId);
}
