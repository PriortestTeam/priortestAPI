package com.hu.oneclick.server.service.impl;

import com.github.pagehelper.PageInfo;
import com.hu.oneclick.common.enums.StatusCode;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.hu.oneclick.common.util.PageUtil.startPage;
import static jodd.util.Util.containsElement;

@Service
public class TestCycleTcServiceImpl implements TestCycleTcService {

    private TestCycleTcDao testCycleTcDao;

    @Resource
    JwtUserServiceImpl jwtUserService;

    @Autowired
    public void setTestCycleTcDao(TestCycleTcDao testCycleTcDao) {
        this.testCycleTcDao = testCycleTcDao;
    }

    @Override
    public Resp<String> runTestCycleTc(ExecuteTestCaseDto executeTestCaseDto) {

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
            int currentCount = !execute.isEmpty() ? execute.get(0).getRunCount() : 0;
            currentCount++;
            testCaseStep.setTestCaseId(executeTestCaseRunDto.getTestCaseId());
            List<TestCaseStep> testCaseSteps = testCaseStepDao.queryList(testCaseStep);
            //获取用户id
            String userId = jwtUserService.getUserLoginInfo().getSysUser().getId();
            if (testCaseSteps.isEmpty()) {
                executeTestCaseDto.setTestCycleId(executeTestCaseRunDto.getTestCycleId());
                executeTestCaseDto.setProjectId(executeTestCaseRunDto.getProjectId());
                executeTestCaseDto.setTestCaseId(executeTestCaseRunDto.getTestCaseId());
                executeTestCaseDto.setStatusCode(5);
                executeTestCaseDto.setRunCount(currentCount);
                testCycleTcDao.addTestCaseExecution(userId, executeTestCaseDto);
            } else {
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
                    executeTestCaseDto.setRunCount(currentCount);
                    executeTestCaseDto.setTestCaseStepId(caseStep.getId());
                    retList.add(executeTestCaseDto);
                    testCycleTcDao.addTestCaseExecution(userId, executeTestCaseDto);
                }
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
        // 查询最新一轮的execute记录
        List<ExecuteTestCaseDto> execute = getExecuteTestCaseList(testCaseRunDto);
        int runCount = execute.stream().findFirst().isPresent() ? execute.stream().findFirst().get().getRunCount() : 0;
        // 更新 execute 状态
        int upExecute = testCycleTcDao.upExecuteStatusCode(testCaseRunDto, runCount, testCaseRunDto.getTestCaseStepId());
        // 更新 testCycleJoinTestCase 表的 状态
        byte runCode = (byte) testCaseRunDto.getStatusCode();
        int upJoinRunStatus = testCycleJoinTestCaseDao.updateRunStatus(testCaseRunDto.getTestCaseId(), testCaseRunDto.getTestCycleId(), calculateStatusCode(runCode, execute), jwtUserService.getUserLoginInfo().getSysUser().getId(), testCaseRunDto.getProjectId());
        if (upExecute > 0 && upJoinRunStatus > 0) {
            return new Resp.Builder<String>().ok();
        }
        return new Resp.Builder<String>().fail();
    }

    private List<ExecuteTestCaseDto> getExecuteTestCaseList(TestCaseRunDto testCaseRunDto) {
        executeTestCaseRunDto.setTestCycleId(testCaseRunDto.getTestCycleId());
        executeTestCaseRunDto.setTestCaseId(testCaseRunDto.getTestCaseId());
        executeTestCaseRunDto.setProjectId(testCaseRunDto.getProjectId());
        return testCycleTcDao.queryList(executeTestCaseRunDto);
    }

    private byte calculateStatusCode(byte runCode, List<ExecuteTestCaseDto> execute) {
        if (!Objects.equals(runCode, StatusCode.FAIL.getValue())) {
            byte passNum = 0, invalidNum = 0, skipNUm = 0; //初始化 成功、无效、跳过 的次数
            byte lastCode = runCode; // 最近更新的状态
            for (ExecuteTestCaseDto testCaseDto : execute) {
                byte statusCode = (byte) (testCaseDto.getStatusCode()); // 将状态码存储在变量中
                switch (Objects.requireNonNull(StatusCode.getByValue(statusCode))) {
                    case FAIL:
                        runCode = StatusCode.FAIL.getValue();
                        break;
                    case BLOCKED:
                        runCode = StatusCode.BLOCKED.getValue();
                        break;
                    case NO_RUN:
                        runCode = StatusCode.NOT_COMPLETED.getValue();
                        break;
                    case SKIP:
                        skipNUm++;
                        break;
                    case PASS:
                        passNum++;
                        break;
                    case INVALID:
                        invalidNum++;
                        break;
                }
            }
            byte[] arr = {StatusCode.FAIL.getValue(), StatusCode.BLOCKED.getValue(), StatusCode.NOT_COMPLETED.getValue()};
            if (!containsElement(arr, runCode)) {
                int executeSize = execute.size();
                if (Objects.equals(executeSize, skipNUm)) {
                    runCode = StatusCode.SKIP.getValue(); // 全部为跳过，则直接返回
                } else if (Objects.equals(executeSize, passNum)) {
                    runCode = StatusCode.PASS.getValue(); // 全部为成功，则直接返回
                } else if (Objects.equals(executeSize, invalidNum)) {
                    runCode = StatusCode.INVALID.getValue(); // 全部为无效，则直接返回
                } else {
                    runCode = lastCode; // 默认使用最后一次的状态码
                }
            }
        }
        return runCode;
    }
}
