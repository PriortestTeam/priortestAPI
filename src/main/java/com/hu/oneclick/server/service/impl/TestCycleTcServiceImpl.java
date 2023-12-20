package com.hu.oneclick.server.service.impl;

import com.github.pagehelper.PageInfo;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.dao.TestCaseStepDao;
import com.hu.oneclick.dao.TestCycleTcDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.TestCaseStep;
import com.hu.oneclick.model.domain.dto.ExecuteTestCaseDto;
import com.hu.oneclick.model.domain.dto.ExecuteTestCaseRunDto;
import com.hu.oneclick.server.service.TestCycleTcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.hu.oneclick.common.util.PageUtil.startPage;

@Service
public class TestCycleTcServiceImpl implements TestCycleTcService {

    private TestCycleTcDao testCycleTcDao;

    private final Timestamp currentTime;

    @Resource
    JwtUserServiceImpl jwtUserService;

    /**
     * 构造函数初始化userId 和 currentTime
     *
     * @author Johson
     * @date 2023/12/20 18:47
     */
    TestCycleTcServiceImpl() {
        Date currentDate = new Date();
        currentTime = new Timestamp(currentDate.getTime());
    }

    @Autowired
    public void setTestCycleTcDao(TestCycleTcDao testCycleTcDao) {
        this.testCycleTcDao = testCycleTcDao;
    }

    @Override
    public Resp<String> runTestCycleTc(ExecuteTestCaseDto executeTestCaseDto) {

        executeTestCaseDto.setCreateTime(currentTime);
        int i = testCycleTcDao.addTestCaseExecution(jwtUserService.getUserLoginInfo().getSysUser().getId(), executeTestCaseDto);

        if (i > 0) {
            return new Resp.Builder<String>().ok();
        }
        return new Resp.Builder<String>().fail();
    }

    @Resource
    ExecuteTestCaseDto executeTestCaseDto;
    @Resource
    TestCaseStepDao testCaseStepDao;
    @Resource
    TestCaseStep testCaseStep;

    @Override
    public Resp runExecuteTestCase(ExecuteTestCaseRunDto executeTestCaseRunDto) {

        List<ExecuteTestCaseDto> execute = testCycleTcDao.queryList(executeTestCaseRunDto);
        ArrayList<Object> retList = new ArrayList<>();
        if (executeTestCaseRunDto.isRunCountIndicator()) {
            // 为true 先查询是否存在execute记录，有则查询runCount当前最大值，没有则直接插入，然后将插入的内容返回
            int currentCount = !execute.isEmpty() ? execute.get(0).getRunCount() : executeTestCaseDto.getRunCount();
            currentCount++;
            testCaseStep.setTestCaseId(executeTestCaseRunDto.getTestCaseId());
            List<TestCaseStep> testCaseSteps = testCaseStepDao.queryList(testCaseStep);
            for (TestCaseStep caseStep : testCaseSteps) {
                executeTestCaseDto.setTestCycleId(executeTestCaseRunDto.getTestCycleId());
                executeTestCaseDto.setTestCaseId(executeTestCaseRunDto.getTestCaseId());
                executeTestCaseDto.setTestStep(caseStep.getTestStep());
                executeTestCaseDto.setExpectedResult(caseStep.getExpectedResult());
                executeTestCaseDto.setTeststepCondition(caseStep.getTeststepCondition());
                executeTestCaseDto.setTestData(caseStep.getTestData());
                executeTestCaseDto.setRemarks(caseStep.getRemarks());
                executeTestCaseDto.setTestStepId(caseStep.getTestStepId());
                executeTestCaseDto.setStatusCode(caseStep.getStatusCode());
                executeTestCaseDto.setTeststepExpand(caseStep.getTeststepExpand());
                executeTestCaseDto.setProjectId(executeTestCaseRunDto.getProjectId());
                executeTestCaseDto.setCreateTime(currentTime);
                executeTestCaseDto.setRunCount(currentCount);
                retList.add(executeTestCaseDto);
                testCycleTcDao.addTestCaseExecution(jwtUserService.getUserLoginInfo().getSysUser().getId(), executeTestCaseDto);
            }
        } else {
            // 为false 则查询要执行的指定用例
            for (ExecuteTestCaseDto executeTestCaseDto : execute) {
                new ExecuteTestCaseDto(executeTestCaseDto.getTestCycleId(), executeTestCaseDto.getTestCaseId(), executeTestCaseDto.getTestStep(), executeTestCaseDto.getExpectedResult(), executeTestCaseDto.getActualResult(), executeTestCaseDto.getTeststepCondition(), executeTestCaseDto.getTestData(), executeTestCaseDto.getRemarks(), executeTestCaseDto.getTestStepId(), executeTestCaseDto.getStatusCode(), executeTestCaseDto.getTeststepExpand(), executeTestCaseDto.getProjectId(), executeTestCaseDto.getCreateTime(), executeTestCaseDto.getRunCount());
                retList.add(executeTestCaseDto);
            }
        }

        startPage();
        return new Resp.Builder<PageInfo<Object>>().setData(PageInfo.of(retList)).ok();
    }
}
