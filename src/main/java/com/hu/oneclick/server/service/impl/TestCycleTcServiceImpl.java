package com.hu.oneclick.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageInfo;
import com.hu.oneclick.common.enums.StatusCode;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.dao.TestCaseStepDao;
import com.hu.oneclick.dao.TestCycleJoinTestCaseDao;
import com.hu.oneclick.dao.TestCycleTcDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.TestCaseStep;
import com.hu.oneclick.model.domain.TestCasesExecution;
import com.hu.oneclick.model.domain.TestCycleJoinTestCase;
import com.hu.oneclick.model.domain.dto.ExecuteTestCaseDto;
import com.hu.oneclick.model.domain.dto.ExecuteTestCaseRunDto;
import com.hu.oneclick.model.domain.dto.TestCaseRunDto;
import com.hu.oneclick.server.service.TestCycleTcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.hu.oneclick.common.util.PageUtil.startPage;

/**
 * @author Johnson
 */
@Service
public class TestCycleTcServiceImpl implements TestCycleTcService {

    @Resource
    JwtUserServiceImpl jwtUserService;
    @Resource
    ExecuteTestCaseDto executeTestCaseDto;
    @Resource
    TestCaseStepDao testCaseStepDao;
    @Resource
    TestCaseStep testCaseStep;
    @Resource
    TestCycleJoinTestCase testCycleJoinTestCase;
    @Resource
    TestCasesExecution testCasesExecution;
    @Resource
    ExecuteTestCaseRunDto executeTestCaseRunDto;
    @Resource
    TestCycleJoinTestCaseDao testCycleJoinTestCaseDao;
    @Resource
    SimpleDateFormat simpleDateFormat;
    private TestCycleTcDao testCycleTcDao;

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

    @Override
    public Resp<PageInfo<Object>> runExecuteTestCase(ExecuteTestCaseRunDto executeTestCaseRunDto) {

        List<ExecuteTestCaseDto> execute = testCycleTcDao.queryList(executeTestCaseRunDto);
        int currentCount = !execute.isEmpty() ? execute.get(0).getRunCount() : 0;
        ArrayList<Object> retList = new ArrayList<>();
        if (executeTestCaseRunDto.isRunCountIndicator()) {
            // 为true 先查询是否存在execute记录，有则查询runCount当前最大值，没有则直接插入，然后将插入的内容返回
            currentCount++;
            testCaseStep.setTestCaseId(executeTestCaseRunDto.getTestCaseId());
            List<TestCaseStep> testCaseSteps = testCaseStepDao.queryList(testCaseStep);
            //获取用户id
            String userId = jwtUserService.getUserLoginInfo().getSysUser().getId();
            if (testCaseSteps.isEmpty()) {
                executeTestCaseDto.setTestCycleId(executeTestCaseRunDto.getTestCycleId());
                executeTestCaseDto.setProjectId(executeTestCaseRunDto.getProjectId());
                executeTestCaseDto.setTestCaseId(executeTestCaseRunDto.getTestCaseId());
                executeTestCaseDto.setStatusCode(StatusCode.NO_RUN.getValue());
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
            // 将 join 表的 count 同步更新并重置 status为 5
            testCycleJoinTestCase.setTestCaseId(executeTestCaseRunDto.getTestCaseId());
            testCycleJoinTestCase.setTestCycleId(executeTestCaseRunDto.getTestCycleId());
            testCycleJoinTestCase.setProjectId(Long.valueOf(executeTestCaseRunDto.getProjectId()));
            testCycleJoinTestCase.setRunCount(currentCount);
            testCycleJoinTestCase.setRunStatus((int) StatusCode.NO_RUN.getValue());
            testCycleJoinTestCase.setCaseRunDuration(0);
            testCycleJoinTestCaseDao.updateTestCycleJoinTestCase(testCycleJoinTestCase);
        } else {
            // 为false 则查询要执行的指定用例
            for (ExecuteTestCaseDto executeTestCaseDto : execute) {
                new ExecuteTestCaseDto(executeTestCaseDto.getTestCycleId(), executeTestCaseDto.getTestCaseId(), executeTestCaseDto.getTestStep(), executeTestCaseDto.getExpectedResult(), executeTestCaseDto.getActualResult(), executeTestCaseDto.getTeststepCondition(), executeTestCaseDto.getTestData(), executeTestCaseDto.getRemarks(), executeTestCaseDto.getTestStepId(), executeTestCaseDto.getStatusCode(), executeTestCaseDto.getTeststepExpand(), executeTestCaseDto.getProjectId(), executeTestCaseDto.getCreateTime(), executeTestCaseDto.getRunCount(), executeTestCaseDto.getTestCaseStepId(), executeTestCaseDto.getRerunTime(), executeTestCaseDto.getStepUpdateTime(), executeTestCaseDto.getCaseRunDuration(), executeTestCaseDto.getCaseTotalPeriod());
                retList.add(executeTestCaseDto);
            }
            testCasesExecution.setRerunTime(new Date());
            LambdaUpdateWrapper<TestCasesExecution> wrapper = new LambdaUpdateWrapper<TestCasesExecution>().eq(TestCasesExecution::getTestCaseId, executeTestCaseRunDto.getTestCaseId()).eq(TestCasesExecution::getTestCycleId, executeTestCaseRunDto.getTestCycleId()).eq(TestCasesExecution::getProjectId, executeTestCaseRunDto.getProjectId()).eq(TestCasesExecution::getRunCount, currentCount).set(TestCasesExecution::getRerunTime, testCasesExecution.getRerunTime());
            //执行更新
            testCycleTcDao.update(null, wrapper);
        }

        startPage();
        return new Resp.Builder<PageInfo<Object>>().setData(PageInfo.of(retList)).ok();
    }

    @Override
    public Resp<String> runTestCase(TestCaseRunDto testCaseRunDto) throws ParseException {
        // 获取 - rerunTime 从表 test_execution
        // rerunTime != null , 表时当前操作是再执行
        // 用在方法 ： setCaseRunDuration(),setCaseTotalPeriod()
        ExecuteTestCaseDto latestExe = testCycleTcDao.getLatest(testCaseRunDto);

        // 获取当前运行run count：
        List<ExecuteTestCaseDto> execute = getExecuteTestCaseList(testCaseRunDto);
        int runCount = execute.stream().findFirst().isPresent() ? execute.stream().findFirst().get().getRunCount() : 0;
        System.out.println("max runCount " + runCount);

        boolean isBatchRun = testCaseRunDto.getTestCaseStepId() == null; // true: batchRun
        boolean isReRun = latestExe.getRerunTime() != null;  // true: reRun

        int upExecute = 0;
        int upJoinRunStatus = 0;
        // 获取最大的 stepUpdateTime
        // 假如 rerunTime!=null, 获取 上一轮执行中多个步骤中最后一次执行的步骤的 update_time
        if (isReRun) {
            if (isBatchRun) {
                System.out.println("当前运行为 再执行 且为 批量运行");
                Date stepUpdateTime = testCycleTcDao.getLatest(testCaseRunDto).getStepUpdateTime();
                latestExe.setStepUpdateTime(stepUpdateTime);

                Date time = new Date();
                System.out.println("--------:"+ getDuration(latestExe, time));
                testCaseRunDto.setCaseRunDuration(getDuration(latestExe, time));
                long total = getTotalPeriod(latestExe, time, testCaseRunDto.getCaseRunDuration());
                // 设置 case_total_period - 计算 test_execution 表
                testCaseRunDto.setCaseTotalPeriod(total);
                testCaseRunDto.setStepUpdateTime(time);

                // 更新 test_execution 状态 ,  run_status，update_user_id， case_run_duration，case_total_period, step_update
                upExecute = testCycleTcDao.upExecuteStatusCode(testCaseRunDto, runCount, testCaseRunDto.getTestCaseStepId());
                System.out.println(" test_execution 表 更新 " + runCount);

                // 计算，更新 本次运行状态 属性 - 为 testCycleJoinTestCase 表的 run_status
                testCaseRunDto.setStatusCode(calculateStatusCode((byte) testCaseRunDto.getStatusCode(), getExecuteTestCaseList(testCaseRunDto)));

                TestCycleJoinTestCase cycleJoinTestCaseByCaseId = testCycleJoinTestCaseDao.getCycleJoinTestCaseByCaseId(testCaseRunDto.getTestCaseId(), Long.valueOf(testCaseRunDto.getProjectId()), testCaseRunDto.getTestCycleId());
                testCaseRunDto.setCaseTotalPeriod(testCaseRunDto.getCaseTotalPeriod() + cycleJoinTestCaseByCaseId.getCaseTotalPeriod());

                // 更新 test_cycle_join_test_case 表
            } else {
                System.out.println("当前运行为 再执行 且为 步骤运行");

                Date stepUpdateTime = testCycleTcDao.getLatest(testCaseRunDto).getStepUpdateTime();
                latestExe.setStepUpdateTime(stepUpdateTime);

                Date time = new Date();
                testCaseRunDto.setCaseRunDuration(getDuration(latestExe, time));
                long total = getTotalPeriodNoneBatch(latestExe, time, testCaseRunDto.getCaseRunDuration());
                // 设置 case_total_period - 计算 test_execution 表
                testCaseRunDto.setCaseTotalPeriod(total);
                testCaseRunDto.setStepUpdateTime(time);

                // 更新 test_execution 状态 ,  run_status，update_user_id， case_run_duration，case_total_period, step_update
                upExecute = testCycleTcDao.upExecuteStatusCode(testCaseRunDto, runCount, testCaseRunDto.getTestCaseStepId());
                System.out.println(" test_execution 表 更新 " + runCount);

                // 计算，更新 本次运行状态 属性 - 为 testCycleJoinTestCase 表的 run_status
                testCaseRunDto.setStatusCode(calculateStatusCode((byte) testCaseRunDto.getStatusCode(), getExecuteTestCaseList(testCaseRunDto)));

                TestCycleJoinTestCase cycleJoinTestCaseByCaseId = testCycleJoinTestCaseDao.getCycleJoinTestCaseByCaseId(testCaseRunDto.getTestCaseId(), Long.valueOf(testCaseRunDto.getProjectId()), testCaseRunDto.getTestCycleId());
                testCaseRunDto.setCaseTotalPeriod(testCaseRunDto.getCaseTotalPeriod() + cycleJoinTestCaseByCaseId.getCaseTotalPeriod());

                // 更新 test_cycle_join_test_case 表
            }
            upJoinRunStatus = testCycleJoinTestCaseDao.updateRunStatus(testCaseRunDto, jwtUserService.getUserLoginInfo().getSysUser().getId());

        } else {
            System.out.println("当前运行为新执行：");

            if (runCount == 1) {
                System.out.println("当前运行为第一次运行");
                // 当前服务器当前时间 - 为了更新 运行step_update_time
                Date time = new Date();
                // 计算，设置 case_duration
                testCaseRunDto.setCaseRunDuration(getDuration(latestExe, time));
                testCaseRunDto.setCaseTotalPeriod(testCaseRunDto.getCaseRunDuration());
                testCaseRunDto.setStepUpdateTime(time);

                // 更新 test_execution 状态 ,  run_status，update_user_id， case_run_duration，case_total_period, step_update
                upExecute = testCycleTcDao.upExecuteStatusCode(testCaseRunDto, runCount, testCaseRunDto.getTestCaseStepId());
                System.out.println(" test_execution 表 更新 " + runCount);

                // 计算，更新 本次运行状态 属性 - 为 testCycleJoinTestCase 表的 run_status
                testCaseRunDto.setStatusCode(calculateStatusCode((byte) testCaseRunDto.getStatusCode(), getExecuteTestCaseList(testCaseRunDto)));
                // 更新 test_cycle_join_test_case 表
                upJoinRunStatus = testCycleJoinTestCaseDao.updateRunStatus(testCaseRunDto, jwtUserService.getUserLoginInfo().getSysUser().getId());

            } else {
                System.out.println("当前运行 不属于 新执行 中的第一次， 当前运行 runCount ：" + runCount);
                if (isBatchRun) {
                    System.out.println("当前运行 不属于 新执行 中的第一次， 当前运行 runCount ：" + runCount + "且是批量运行");
                    // 当前服务器当前时间 - 为了更新 运行step_update_time
                    Date time = new Date();
                    // 计算，设置 case_duration
                    testCaseRunDto.setCaseRunDuration(getDuration(latestExe, time));
                    testCaseRunDto.setCaseTotalPeriod(testCaseRunDto.getCaseRunDuration());
                    testCaseRunDto.setStepUpdateTime(time);

                    // 更新 test_execution 状态 ,  run_status，update_user_id， case_run_duration，case_total_period, step_update
                    upExecute = testCycleTcDao.upExecuteStatusCode(testCaseRunDto, runCount, testCaseRunDto.getTestCaseStepId());
                    System.out.println(" test_execution 表 更新 " + runCount);

                    // 计算，更新 本次运行状态 属性 - 为 testCycleJoinTestCase 表的 run_status
                    testCaseRunDto.setStatusCode(calculateStatusCode((byte) testCaseRunDto.getStatusCode(), getExecuteTestCaseList(testCaseRunDto)));

                    TestCycleJoinTestCase cycleJoinTestCaseByCaseId = testCycleJoinTestCaseDao.getCycleJoinTestCaseByCaseId(testCaseRunDto.getTestCaseId(), Long.valueOf(testCaseRunDto.getProjectId()), testCaseRunDto.getTestCycleId());
                    testCaseRunDto.setCaseTotalPeriod(testCaseRunDto.getCaseTotalPeriod() + cycleJoinTestCaseByCaseId.getCaseTotalPeriod());

                    // 更新 test_cycle_join_test_case 表
                    upJoinRunStatus = testCycleJoinTestCaseDao.updateRunStatus(testCaseRunDto, jwtUserService.getUserLoginInfo().getSysUser().getId());
                } else {
                    System.out.println("当前运行 不属于 新执行 中的第一次， 当前运行 runCount ：" + runCount + "不是是批量运行");
                    // 当前服务器当前时间 - 为了更新 运行step_update_time
                    Date time = new Date();
                    // 计算，设置 case_duration
                    testCaseRunDto.setCaseRunDuration(getDuration(latestExe, time));
                    testCaseRunDto.setCaseTotalPeriod(testCaseRunDto.getCaseRunDuration());
                    testCaseRunDto.setStepUpdateTime(time);

                    // 更新 test_execution 状态 ,  run_status，update_user_id， case_run_duration，case_total_period, step_update
                    upExecute = testCycleTcDao.upExecuteStatusCode(testCaseRunDto, runCount, testCaseRunDto.getTestCaseStepId());
                    System.out.println(" test_execution 表 更新 " + runCount);

                    // 计算，更新 本次运行状态 属性 - 为 testCycleJoinTestCase 表的 run_status
                    testCaseRunDto.setStatusCode(calculateStatusCode((byte) testCaseRunDto.getStatusCode(), getExecuteTestCaseList(testCaseRunDto)));

                    TestCycleJoinTestCase cycleJoinTestCaseByCaseId = testCycleJoinTestCaseDao.getCycleJoinTestCaseByCaseId(testCaseRunDto.getTestCaseId(), Long.valueOf(testCaseRunDto.getProjectId()), testCaseRunDto.getTestCycleId());
                    testCaseRunDto.setCaseTotalPeriod(testCaseRunDto.getCaseTotalPeriod() + cycleJoinTestCaseByCaseId.getCaseTotalPeriod() - cycleJoinTestCaseByCaseId.getCaseRunDuration());

                    // 更新 test_cycle_join_test_case 表
                    upJoinRunStatus = testCycleJoinTestCaseDao.updateRunStatus(testCaseRunDto, jwtUserService.getUserLoginInfo().getSysUser().getId());
                }
            }


        }

       /* if (Objects.nonNull(testCaseRunDto.getTestCaseStepId())) {
            testCaseRunDto.setTestCaseStepId(null);
            Date stepUpdateTime = testCycleTcDao.getLatest(testCaseRunDto).getStepUpdateTime();
            latestExe.setStepUpdateTime(stepUpdateTime);
        }

        // 获取 计算 case_total_period， joinTotal
        // 新执行： test_execution 表：  case_total_period == case_duration
        long total = 0;
        if (latestExe.getRerunTime() ==null){
            System.out.println("当前运行为 新执行：");
            testCaseRunDto.setCaseTotalPeriod( testCaseRunDto.getCaseRunDuration());
        }
        // 再执行： test_execution 表： 计算 本次运行时长 + 自然时长
        else{
            System.out.println("当前运行为 再执行：");
             total = getTotalPeriod(latestExe, time, testCaseRunDto.getCaseRunDuration());
            // 设置 case_total_period - 计算 test_execution 表
            testCaseRunDto.setCaseTotalPeriod(total);
        }

        // 设置更新时间 -step_update
        testCaseRunDto.setStepUpdateTime(time);
        System.out.println("setup update time：" +time);

        // 查询最新一轮的execute记录 (以 max runCount为准 )- test_execution
        List<ExecuteTestCaseDto> execute = getExecuteTestCaseList(testCaseRunDto);
        int runCount = execute.stream().findFirst().isPresent() ? execute.stream().findFirst().get().getRunCount() : 0;
        System.out.println("max runCount " + runCount);



        // 以下代码与 testcase_join_test cycle 更新有关
        // 计算，更新 本次运行状态 属性 - 为 testCycleJoinTestCase 表的 run_status
        testCaseRunDto.setStatusCode(calculateStatusCode((byte) testCaseRunDto.getStatusCode(), getExecuteTestCaseList(testCaseRunDto)));

        // 获取当前运行用例 在表里的 case_total_period
        // 如果当前  getRunCount > 1, case_total_period 累加
        // 如果 当前  getRunCount == 1, cycleJoinTestCaseByCaseId.getCaseTotalPeriod()==0
        TestCycleJoinTestCase cycleJoinTestCaseByCaseId = testCycleJoinTestCaseDao.getCycleJoinTestCaseByCaseId(testCaseRunDto.getTestCaseId(), Long.valueOf(testCaseRunDto.getProjectId()), testCaseRunDto.getTestCycleId());
        if (latestExe.getRunCount() > 1 ) {
            testCaseRunDto.setCaseTotalPeriod(testCaseRunDto.getCaseTotalPeriod() + cycleJoinTestCaseByCaseId.getCaseTotalPeriod());
            System.out.println(" testCycleJoinTestCase totalPeriod " + testCaseRunDto.getCaseTotalPeriod() + "    " +cycleJoinTestCaseByCaseId.getCaseTotalPeriod());
        }
        if (latestExe.getRunCount()==1 && cycleJoinTestCaseByCaseId.getCaseTotalPeriod()==0){
            testCaseRunDto.setCaseTotalPeriod(testCaseRunDto.getCaseTotalPeriod() + cycleJoinTestCaseByCaseId.getCaseTotalPeriod());
            System.out.println(" testCycleJoinTestCase totalPeriod " + testCaseRunDto.getCaseTotalPeriod() + "    " +cycleJoinTestCaseByCaseId.getCaseTotalPeriod());
        }
        // 更新 testCycleJoinTestCase  表的 run_status，update_user_id， case_run_duration，case_total_period
       */
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
            //初始化 成功、无效、跳过 的次数
            byte passNum = 0, invalidNum = 0, skipNum = 0;
            int executeSize = execute.size();
            for (ExecuteTestCaseDto testCaseDto : execute) {
                // 将状态码存储在变量中
                byte statusCode = (byte) (testCaseDto.getStatusCode());
                switch (Objects.requireNonNull(StatusCode.getByValue(statusCode))) {
                    case FAIL:
                        return StatusCode.FAIL.getValue();
                    case BLOCKED:
                        return StatusCode.BLOCKED.getValue();
                    case NO_RUN:
                        return StatusCode.NOT_COMPLETED.getValue();
                    case SKIP:
                        skipNum++;
                        // 全部为跳过，则直接返回
                        if (Objects.equals(executeSize, skipNum)) {
                            return StatusCode.SKIP.getValue();
                        }
                        break;
                    case PASS:
                        passNum++;
                        // 全部为成功，则直接返回
                        if (Objects.equals(executeSize, passNum)) {
                            return StatusCode.PASS.getValue();
                        }
                        break;
                    case INVALID:
                        invalidNum++;
                        // 全部为无效，则直接返回
                        if (Objects.equals(executeSize, invalidNum)) {
                            return StatusCode.INVALID.getValue();
                        }
                        break;
                    default:
                        return runCode;
                }
            }
        }
        return runCode;
    }

    @Bean
    public SimpleDateFormat simpleDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 计算当前步骤运行时间
     *
     * @param latestExe latestExe
     * @param time      当前时间
     * @return long
     * @author Johnson
     */
    private long getDuration(ExecuteTestCaseDto latestExe, Date time) throws ParseException {
        long differenceInMillis = 0;
        // 如果再次执行时间不为空，则开始时间为再次执行时间
        Date startTime = Objects.nonNull(latestExe.getRerunTime()) ? latestExe.getRerunTime() : latestExe.getCreateTime();

        // 计算时间差
        differenceInMillis += Math.subtractExact(time.getTime(), simpleDateFormat.parse(simpleDateFormat.format(startTime)).getTime());

        System.out.println("计算 本次运行 或本步骤运行时长： " + "stepUpdate - rerunTime / startTime " + time.getTime() + " -" + startTime + " = " + differenceInMillis);
        return Objects.nonNull(latestExe.getRerunTime()) ? Math.addExact(differenceInMillis, latestExe.getCaseRunDuration()) : differenceInMillis;
    }

    /**
     * 获取时间段内 所有日期
     *
     * @param startDate startDate
     * @param endDate   endDate
     * @return java.util.List<java.util.Date>
     * @author Johnson
     */
    private List<Date> getDatesBetween(Date startDate, Date endDate) {
        List<Date> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        while (calendar.getTime().before(endDate)) {
            Date date = calendar.getTime();
            dates.add(date);
            calendar.add(Calendar.DATE, 1);
        }
        return dates;
    }

    /**
     * 计算给定日期的周末天数
     *
     * @param dates dates
     * @return int
     * @author Johnson
     */
    private int countWeekends(List<Date> dates) {
        int count = 0;
        for (Date date : dates) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            if (Objects.equals(dayOfWeek, Calendar.SATURDAY) || Objects.equals(dayOfWeek, Calendar.SUNDAY)) {
                count++;
            }
        }
        return count;
    }

    /**
     * 扣除周末后的剩余毫秒数
     *
     * @param startDate startDate
     * @param endDate   endDate
     * @return long
     * @author Johnson
     */
    private long filterWeekends(Date startDate, Date endDate) {
        // 该时间段的总毫秒数
        long totalTime = Math.subtractExact(endDate.getTime(), startDate.getTime());
        List<Date> dates = getDatesBetween(startDate, endDate);
        // 统计周末出现的天数
        int weekends = countWeekends(dates);
        // 获取一天的毫秒数
        long dayMilliseconds = ChronoUnit.DAYS.getDuration().toMillis();
        // 周末总毫秒数
        long weekendsMillis = Math.multiplyExact(dayMilliseconds, weekends);
        // 当前用例剩余毫秒数 = 总毫秒数 - 周末毫秒数
        long surplusMillis = Math.subtractExact(totalTime, weekendsMillis);
        // 当扣除周末后结果<0,则加 24小时
        return surplusMillis < 0 ? Math.addExact(surplusMillis, dayMilliseconds) : surplusMillis;
    }

    /**
     * 获取自然时间
     *
     * @param newStepUpdateTime newStepUpdateTime 当前时间
     * @param preStepUpdateTime preStepUpdateTime 上次执行时间
     * @return long
     * @author Johnson
     */
    private long calculateNaturalTime(Date newStepUpdateTime, Date preStepUpdateTime) {
        // 扣除周末后的毫秒数
        long deductingWeekendMillisecond = filterWeekends(preStepUpdateTime, newStepUpdateTime);
        // 获取八小时的毫秒数
        long eightHoursMilliseconds = Math.multiplyExact(ChronoUnit.HOURS.getDuration().toMillis(), 8);
        // 获取一天的毫秒数
        long oneDayMilliseconds = ChronoUnit.DAYS.getDuration().toMillis();
        // 获取两天的毫秒数
        long twoDayMilliseconds = Math.multiplyExact(oneDayMilliseconds, 2);
        // 扣除一天的毫秒数
        long deductionOneDayMilliseconds = Math.subtractExact(deductingWeekendMillisecond, oneDayMilliseconds);
        // 扣除两天天的毫秒数
        long deductionTwoDayMilliseconds = Math.subtractExact(deductingWeekendMillisecond, twoDayMilliseconds);

        // 如果自然时间大于 48小时
        if (deductingWeekendMillisecond > twoDayMilliseconds) {
            // 扣除 48小时，结果大于 8小时 则等于 8小时，否则还为该计算值
            return Math.min(deductionTwoDayMilliseconds, eightHoursMilliseconds);
        }
        // 如果 自然时间 大于 24小时，且小于48小时，则扣除一天的毫秒数
        if (deductingWeekendMillisecond > oneDayMilliseconds && deductingWeekendMillisecond < twoDayMilliseconds) {
            // 扣除 24小时， 结果大于 8小时 则等于 8小时，否则还为该计算值
            return Math.min(deductionOneDayMilliseconds, eightHoursMilliseconds);
        }
        // 如果 自然时间 大于 8小时，且小于等于24小时，或者自然时间等于 48小时，则等于 8 小时
        if ((deductingWeekendMillisecond > eightHoursMilliseconds && deductingWeekendMillisecond <= oneDayMilliseconds) || (Objects.equals(deductingWeekendMillisecond, twoDayMilliseconds))) {
            return eightHoursMilliseconds;
        }
        // 默认为
        return deductingWeekendMillisecond;
    }

    /**
     * 获取当前用例的总完成时间
     *
     * @param latestExe         latestExe
     * @param newStepUpdateTime newStepUpdateTime
     * @param duration          duration
     * @return long
     * @author Johnson
     */
    private long getTotalPeriodNoneBatch(ExecuteTestCaseDto latestExe, Date newStepUpdateTime, long duration) {
        long totalPeriod = duration;

        if (Objects.nonNull(latestExe.getRerunTime())) {
            // 获取自然时间
            long naturalTime = calculateNaturalTime(newStepUpdateTime, latestExe.getStepUpdateTime());
            System.out.println("⭐⭐️️⭐️ 自然时间:--->>>>>-----(系统当前时间)日期：(" + newStepUpdateTime + ")" + newStepUpdateTime.getTime() + " − (pre_stepUpdateTime)）日期：(" + latestExe.getStepUpdateTime() + ")" + latestExe.getStepUpdateTime().getTime() + " = " + naturalTime);
            // 计算caseTotalPeriod
            totalPeriod += naturalTime;
            System.out.println("⭐⭐️️⭐️ total:--->>>>>-----(当前duration)" + duration + " + 自然时间：" + naturalTime + " = " + totalPeriod);
        }
        if (latestExe.getRunCount() > 1) {
            totalPeriod += latestExe.getCaseTotalPeriod();
        }
        return totalPeriod;
    }

    private long getTotalPeriod(ExecuteTestCaseDto latestExe, Date newStepUpdateTime, long duration) {
        long totalPeriod = duration;
        System.out.println("⭐⭐️️⭐️ total:--->>>>>-----(当前duration)" + duration);

        if (Objects.nonNull(latestExe.getRerunTime())) {
            // 获取自然时间
            long naturalTime = calculateNaturalTime(newStepUpdateTime, latestExe.getStepUpdateTime());
            System.out.println("⭐⭐️️⭐️ 自然时间:--->>>>>-----(系统当前时间)日期：(" + newStepUpdateTime + ")" + newStepUpdateTime.getTime() + " − (pre_stepUpdateTime)）日期：(" + latestExe.getStepUpdateTime() + ")" + latestExe.getStepUpdateTime().getTime() + " = " + naturalTime);
            // totalPeriod > 为了更新test_execution
            totalPeriod += naturalTime;
            System.out.println("⭐⭐️️⭐️ total:--->>>>>-----(当前duration)" + duration + " + 自然时间：" + naturalTime + " = " + totalPeriod);
        }
        return totalPeriod;
    }

}
