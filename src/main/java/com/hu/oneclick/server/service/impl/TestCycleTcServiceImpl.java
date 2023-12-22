package com.hu.oneclick.server.service.impl;

import com.github.pagehelper.PageInfo;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.dao.TestCaseStepDao;
import com.hu.oneclick.dao.TestCycleJoinTestCaseDao;
import com.hu.oneclick.dao.TestCycleTcDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.TestCaseStep;
import com.hu.oneclick.model.domain.dto.ExecuteTestCaseDto;
import com.hu.oneclick.model.domain.dto.ExecuteTestCaseRunDto;
import com.hu.oneclick.model.domain.dto.TestCaseRunDto;
import com.hu.oneclick.server.service.TestCycleTcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.hu.oneclick.common.util.PageUtil.startPage;
import static jodd.util.Util.containsElement;

@Service
public class TestCycleTcServiceImpl implements TestCycleTcService {

    private TestCycleTcDao testCycleTcDao;

    private final Timestamp currentTime;

    @Resource
    JwtUserServiceImpl jwtUserService;

    /**
     * 构造函数初始化 currentTime
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
        executeTestCaseDto.setRunCount(1);
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
    public Resp<PageInfo<Object>> runExecuteTestCase(ExecuteTestCaseRunDto executeTestCaseRunDto) {

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
                executeTestCaseDto.setTestCaseStepId(caseStep.getId());
                retList.add(executeTestCaseDto);
                testCycleTcDao.addTestCaseExecution(jwtUserService.getUserLoginInfo().getSysUser().getId(), executeTestCaseDto);
            }
        } else {
            // 为false 则查询要执行的指定用例
            for (ExecuteTestCaseDto executeTestCaseDto : execute) {
                new ExecuteTestCaseDto(executeTestCaseDto.getTestCycleId(), executeTestCaseDto.getTestCaseId(), executeTestCaseDto.getTestStep(), executeTestCaseDto.getExpectedResult(), executeTestCaseDto.getActualResult(), executeTestCaseDto.getTeststepCondition(), executeTestCaseDto.getTestData(), executeTestCaseDto.getRemarks(), executeTestCaseDto.getTestStepId(), executeTestCaseDto.getStatusCode(), executeTestCaseDto.getTeststepExpand(), executeTestCaseDto.getProjectId(), executeTestCaseDto.getCreateTime(), executeTestCaseDto.getRunCount(), executeTestCaseDto.getTestCaseStepId());
                retList.add(executeTestCaseDto);
            }
        }

        startPage();
        return new Resp.Builder<PageInfo<Object>>().setData(PageInfo.of(retList)).ok();
    }

    @Resource
    ExecuteTestCaseRunDto executeTestCaseRunDto;

    @Resource
    TestCycleJoinTestCaseDao testCycleJoinTestCaseDao;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Resp<String> runTestCase(TestCaseRunDto testCaseRunDto) {
        // 更新 execute 状态
        int upExecute = testCycleTcDao.upExecuteStatusCode(testCaseRunDto);
        // 更新 testCycleJoinTestCase 表的 状态
        byte runCode = calculateStatusCode(testCaseRunDto);
        System.out.println("-------------->>>->最终所得状态---->>>>-->" + runCode);
        String userId = jwtUserService.getUserLoginInfo().getSysUser().getId();
        int upJoinRunStatus = testCycleJoinTestCaseDao.updateRunStatus(testCaseRunDto.getTestCaseId(), testCaseRunDto.getTestCycleId(), runCode, userId, testCaseRunDto.getProjectId());
        if (upExecute > 0 && upJoinRunStatus > 0) {
            return new Resp.Builder<String>().ok();
        }
        return new Resp.Builder<String>().fail();
    }

    private byte calculateStatusCode(TestCaseRunDto testCaseRunDto) {
        byte runCode = (byte) testCaseRunDto.getStatusCode();
        byte FAIL = 2; // 失败 状态
        if (runCode == FAIL) {
            // 出现失败，则直接更新为失败
            System.out.println("-------------->>>->当前传参为 Fail，直接 更新为失败");
            return runCode;
        } else {
            // 查询最新一轮的execute记录
            executeTestCaseRunDto.setTestCycleId(testCaseRunDto.getTestCycleId());
            executeTestCaseRunDto.setTestCaseId(testCaseRunDto.getTestCaseId());
            executeTestCaseRunDto.setProjectId(testCaseRunDto.getProjectId());
            List<ExecuteTestCaseDto> execute = testCycleTcDao.queryList(executeTestCaseRunDto);
            byte passNum = 0;  // 成功次数初始化
            byte invalidNum = 0; // 无效次数初始化
            byte skipNUm = 0; // 跳过次数初始化
            byte lastCode = runCode; // 最近更新的状态
            byte INVALID = 0; //无效 状态
            byte PASS = 1; //成功 状态
            byte SKIP = 3; //跳过 状态
            byte BLOCKED = 4; //停滞 状态
            byte INCOMLETE = 6; //未完成 状态
            for (ExecuteTestCaseDto testCaseDto : execute) {
                int statusCode = testCaseDto.getStatusCode(); // 将状态码存储在变量中
                System.out.println("-------------->>>->遍历状态->>>>>>:" + statusCode);
                // 有一个是失败就是失败
                if (statusCode == FAIL) {
                    runCode = FAIL;
                    break;
                }
                // 没有失败 有一个是停滞就是停滞
                if (statusCode == BLOCKED) {
                    runCode = BLOCKED;
                    break;
                }
                byte UNEXECUTED = 5; //未执行 状态
                // 不存在失败、停滞、有一个未执行 就是未完成
                if (statusCode == UNEXECUTED) {
                    runCode = INCOMLETE;
                    break;
                }
                // 统计 跳过、成功、无效 出现的次数
                if (statusCode == SKIP) {
                    skipNUm++;
                } else if (statusCode == PASS) {
                    passNum++;
                } else if (statusCode == INVALID) {
                    invalidNum++;
                }
            }
            byte[] arr = {FAIL, BLOCKED, INCOMLETE};
            if (!containsElement(arr, runCode)) {
                int executeSize = execute.size();
                System.out.println("-------------->>>->总数-->>>>-->" + executeSize);
                if (executeSize == skipNUm) {
                    runCode = SKIP; // 全部为跳过，则直接返回
                } else if (executeSize == passNum) {
                    runCode = PASS; // 全部为成功，则直接返回
                } else if (executeSize == invalidNum) {
                    runCode = INVALID; // 全部为无效，则直接返回
                } else {
                    System.out.println("-------------->>>->出现混合状态，取最新状态");
                    runCode = lastCode; // 默认使用最后一次的状态码
                }
            }
            return runCode;
        }
    }
}
