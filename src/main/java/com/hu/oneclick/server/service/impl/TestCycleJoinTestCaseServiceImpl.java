package com.hu.oneclick.server.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hu.oneclick.dao.TestCycleJoinTestCaseDao;
import com.hu.oneclick.dao.TestCycleTcDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.TestCasesExecution;
import com.hu.oneclick.model.domain.TestCycleJoinTestCase;
import com.hu.oneclick.model.domain.dto.TestCaseRunDto;
import com.hu.oneclick.model.domain.dto.TestCycleJoinTestCaseDto;
import com.hu.oneclick.model.domain.dto.TestCycleJoinTestCaseSaveDto;
import com.hu.oneclick.server.service.TestCycleJoinTestCaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

/**
 * @Author: jhh
 * @Date: 2023/7/8
 */
@Service
@Slf4j
public class TestCycleJoinTestCaseServiceImpl extends ServiceImpl<TestCycleJoinTestCaseDao, TestCycleJoinTestCase>
    implements TestCycleJoinTestCaseService {

    @Resource
    private TestCycleJoinTestCaseDao testCycleJoinTestCaseDao;

    @Override
    public Boolean saveInstance(TestCycleJoinTestCaseSaveDto dto) {
        TestCycleJoinTestCase joinTestCase = null;
        for (Long testCaseId : dto.getTestCaseIds()) {
            List<TestCycleJoinTestCase> entityList = this.getByProjectIdAndCycleIdAndCaseId(dto.getProjectId(),
                dto.getTestCycleId(), testCaseId);
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

    private List<TestCycleJoinTestCase> getByProjectIdAndCycleIdAndCaseId(Long projectId, Long testCycleId,
        Long testCaseId) {
        LambdaQueryWrapper<TestCycleJoinTestCase> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TestCycleJoinTestCase::getProjectId, projectId)
            .eq(TestCycleJoinTestCase::getTestCycleId, testCycleId)
            .eq(TestCycleJoinTestCase::getTestCaseId, testCaseId);
        return this.list(queryWrapper);
    }

    @Resource
    TestCycleTcDao testCycleTcDao;
    @Override
    public void deleteInstance(TestCycleJoinTestCaseSaveDto dto) {
        ArrayList<Long> testCasesIds = new ArrayList<>();
        for (Long testCaseId : dto.getTestCaseIds()) {
            this.testCycleJoinTestCaseDao.deleteByParam(dto.getProjectId(), dto.getTestCycleId(), testCaseId);
            testCasesIds.add(testCaseId);
        }

        testCycleTcDao.delete(new LambdaQueryWrapper<TestCasesExecution>().in(TestCasesExecution::getTestCaseId, testCasesIds).eq(TestCasesExecution::getTestCycleId, dto.getTestCycleId()).eq(TestCasesExecution::getProjectId, dto.getProjectId()));
    }

    @Override
    public List<Long> getCaseIdListByCycleId(Long testCycleId) {

        return this.testCycleJoinTestCaseDao.getCaseIdListByCycleId(testCycleId);
    }

    @Override
    public int countCycleIdByCaseId(Long testCaseId, Long projectid, Long cycleId) {
        return this.testCycleJoinTestCaseDao.countByTestCaseIdInt(testCaseId, projectid, cycleId);
    }

    @Override
    public TestCycleJoinTestCase getCycleJoinTestCaseByCaseId(Long caseId, Long projectId, Long cycleId) {
        return this.testCycleJoinTestCaseDao.getCycleJoinTestCaseByCaseId(caseId, projectId, cycleId);
    }

    /**
     * 更改runCaseStatus
     *
     * @param projectId
     * @param testCycleJoinTestCaseDto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp runCaseStatusUpdate(Long projectId, TestCycleJoinTestCaseDto testCycleJoinTestCaseDto) {

        //参数校验
        if (testCycleJoinTestCaseDto == null || testCycleJoinTestCaseDto.getTestCycleId() == null
                || testCycleJoinTestCaseDto.getAddedOn() == null || testCycleJoinTestCaseDto.getTestCaseId() == null){
            return new Resp.Builder<>().buildResult("非法参数");
        }

        TestCycleJoinTestCase testCycleJoinTestCase = this.getOne(new LambdaQueryWrapper<TestCycleJoinTestCase>()
                .eq(TestCycleJoinTestCase::getTestCycleId, testCycleJoinTestCaseDto.getTestCycleId())
                .eq(TestCycleJoinTestCase::getProjectId, projectId)
                .eq(TestCycleJoinTestCase::getTestCaseId, testCycleJoinTestCaseDto.getTestCaseId()));

        if (testCycleJoinTestCase == null){
            return new Resp.Builder<>().buildResult("数据不存在");
        }

        testCycleJoinTestCase.setRunStatus(testCycleJoinTestCaseDto.getRunStatus());
        if (testCycleJoinTestCaseDto.getAddedOn()){
            testCycleJoinTestCase.setCaseRunDuration((int)(testCycleJoinTestCase.getCaseRunDuration() + testCycleJoinTestCaseDto.getCaseRunDuration()));
            testCycleJoinTestCase.setCaseTotalPeriod(testCycleJoinTestCase.getCaseTotalPeriod() + testCycleJoinTestCaseDto.getCaseTotalPeriod());
            testCycleJoinTestCase.setRunCount(testCycleJoinTestCase.getRunCount() + testCycleJoinTestCaseDto.getRunCount());
        }else {
            testCycleJoinTestCase.setCaseRunDuration(testCycleJoinTestCaseDto.getCaseRunDuration().intValue());
            testCycleJoinTestCase.setCaseTotalPeriod(testCycleJoinTestCaseDto.getCaseTotalPeriod());
            testCycleJoinTestCase.setRunCount(testCycleJoinTestCaseDto.getRunCount());
        }

        //更新
        boolean b = this.updateById(testCycleJoinTestCase);
        if (!b){
            return new Resp.Builder<>().buildResult("更新失败");
        }
        return new Resp.Builder<>().ok();
    }
}
