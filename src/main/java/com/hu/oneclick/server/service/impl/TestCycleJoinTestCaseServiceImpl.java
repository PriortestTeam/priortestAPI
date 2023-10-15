package com.hu.oneclick.server.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hu.oneclick.dao.TestCycleJoinTestCaseDao;
import com.hu.oneclick.model.domain.TestCycleJoinTestCase;
import com.hu.oneclick.model.domain.dto.TestCycleJoinTestCaseSaveDto;
import com.hu.oneclick.server.service.TestCycleJoinTestCaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: jhh
 * @Date: 2023/7/8
 */
@Service
@Slf4j
public class TestCycleJoinTestCaseServiceImpl extends ServiceImpl<TestCycleJoinTestCaseDao, TestCycleJoinTestCase> implements TestCycleJoinTestCaseService {

    @Resource
    private TestCycleJoinTestCaseDao testCycleJoinTestCaseDao;

    @Override
    public Boolean saveInstance(TestCycleJoinTestCaseSaveDto dto) {
        TestCycleJoinTestCase joinTestCase = null;
        for (Long testCaseId : dto.getTestCaseIds()) {
            List<TestCycleJoinTestCase> entityList = this.getByProjectIdAndCycleIdAndCaseId(dto.getProjectId(), dto.getTestCycleId(), testCaseId);
            if (CollUtil.isNotEmpty(entityList)) {
//                throw new BaseException(StrUtil.format("该测试用例已关联"));
                continue;
            }
            joinTestCase = new TestCycleJoinTestCase();
            joinTestCase.setProjectId(dto.getProjectId());
            joinTestCase.setTestCycleId(dto.getTestCycleId());
            joinTestCase.setTestCaseId(testCaseId);
            this.testCycleJoinTestCaseDao.insert(joinTestCase);
        }
        return true;
    }

    private List<TestCycleJoinTestCase> getByProjectIdAndCycleIdAndCaseId(Long projectId, Long testCycleId, Long testCaseId) {
        LambdaQueryWrapper<TestCycleJoinTestCase> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TestCycleJoinTestCase::getProjectId, projectId)
                .eq(TestCycleJoinTestCase::getTestCycleId, testCycleId)
                .eq(TestCycleJoinTestCase::getTestCaseId, testCaseId);
        return this.list(queryWrapper);
    }

    @Override
    public void deleteInstance(TestCycleJoinTestCaseSaveDto dto) {
        for (Long testCaseId : dto.getTestCaseIds()) {
            this.testCycleJoinTestCaseDao.deleteByParam(dto.getProjectId(), dto.getTestCycleId(), testCaseId);
        }
    }

    @Override
    public List<Long> getCaseIdListByCycleId(Long testCycleId) {

        return this.testCycleJoinTestCaseDao.getCaseIdListByCycleId(testCycleId);
    }
}
