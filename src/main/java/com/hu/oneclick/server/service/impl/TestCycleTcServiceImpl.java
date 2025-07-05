package com.hu.oneclick.server.service.impl;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageInfo;
import com.hu.oneclick.common.enums.StatusCode;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.dao.TestCaseStepDao;
import com.hu.oneclick.dao.TestCycleJoinTestCaseDao;
import com.hu.oneclick.dao.TestCycleTcDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.entity.TestCaseStep;
import com.hu.oneclick.model.entity.TestCasesExecution;
import com.hu.oneclick.model.entity.TestCycleJoinTestCase;
import com.hu.oneclick.model.domain.dto.ExecuteTestCaseDto;
import com.hu.oneclick.model.domain.dto.ExecuteTestCaseRunDto;
import com.hu.oneclick.model.domain.dto.TestCaseRunDto;
import com.hu.oneclick.server.service.TestCycleTcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
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
        List&lt;ExecuteTestCaseDto> execute = testCycleTcDao.queryList(executeTestCaseRunDto);
        int currentCount = !execute.isEmpty() ? execute.get(0).getRunCount() : 0;
        ArrayList&lt;Object> retList = new ArrayList&lt;>();
        if (executeTestCaseRunDto.isRunCountIndicator() {
            // 为true 先查询是否存在execute记录，有则查询runCount当前最大值，没有则直接插入，然后将插入的内容返回
            currentCount++;
            testCaseStep.setTestCaseId(executeTestCaseRunDto.getTestCaseId();
            List&lt;TestCaseStep> testCaseSteps = testCaseStepDao.queryList(testCaseStep);
            //获取用户id
            String userId = jwtUserService.getUserLoginInfo().getSysUser().getId();
            if (testCaseSteps.isEmpty() {
                executeTestCaseDto.setTestCycleId(executeTestCaseRunDto.getTestCycleId();
                executeTestCaseDto.setProjectId(executeTestCaseRunDto.getProjectId();
                executeTestCaseDto.setTestCaseId(executeTestCaseRunDto.getTestCaseId();
                executeTestCaseDto.setStatusCode(StatusCode.NO_RUN.getValue();
                executeTestCaseDto.setRunCount(currentCount);
                testCycleTcDao.addTestCaseExecution(userId, executeTestCaseDto);
            } else {
                for (TestCaseStep caseStep : testCaseSteps) {
                    executeTestCaseDto.setTestCycleId(executeTestCaseRunDto.getTestCycleId();
                    executeTestCaseDto.setTestCaseId(executeTestCaseRunDto.getTestCaseId();
                    executeTestCaseDto.setTestStep(caseStep.getTestStep();
                    executeTestCaseDto.setExpectedResult(caseStep.getExpectedResult();
                    executeTestCaseDto.setTeststepCondition(caseStep.getTeststepCondition();
                    executeTestCaseDto.setTestData(caseStep.getTestData();
                    executeTestCaseDto.setRemarks(caseStep.getRemarks();
                    executeTestCaseDto.setTestStepId(caseStep.getTestStepId();
                    executeTestCaseDto.setStatusCode(caseStep.getStatusCode();
                    executeTestCaseDto.setTeststepExpand(caseStep.getTeststepExpand();
                    executeTestCaseDto.setProjectId(executeTestCaseRunDto.getProjectId();
                    executeTestCaseDto.setRunCount(currentCount);
                    executeTestCaseDto.setTestCaseStepId(caseStep.getId();
                    retList.add(executeTestCaseDto);
                    testCycleTcDao.addTestCaseExecution(userId, executeTestCaseDto);
                }
            }
            // 将 join 表的 count 同步更新并重置 status为 5
            testCycleJoinTestCase.setTestCaseId(executeTestCaseRunDto.getTestCaseId();
            testCycleJoinTestCase.setTestCycleId(executeTestCaseRunDto.getTestCycleId();
            testCycleJoinTestCase.setProjectId(Long.valueOf(executeTestCaseRunDto.getProjectId();
            testCycleJoinTestCase.setRunCount(currentCount);
            testCycleJoinTestCase.setRunStatus((int) StatusCode.NO_RUN.getValue();
            testCycleJoinTestCase.setCaseRunDuration(0);
            testCycleJoinTestCaseDao.updateTestCycleJoinTestCase(testCycleJoinTestCase);
        } else {
            // 为false 则查询要执行的指定用例
            for (ExecuteTestCaseDto executeTestCaseDto : execute) {
                new ExecuteTestCaseDto(executeTestCaseDto.getTestCycleId(), executeTestCaseDto.getTestCaseId(), executeTestCaseDto.getTestStep(), executeTestCaseDto.getExpectedResult(), executeTestCaseDto.getActualResult(), executeTestCaseDto.getTeststepCondition(), executeTestCaseDto.getTestData(), executeTestCaseDto.getRemarks(), executeTestCaseDto.getTestStepId(), executeTestCaseDto.getStatusCode(), executeTestCaseDto.getTeststepExpand(), executeTestCaseDto.getProjectId(), executeTestCaseDto.getCreateTime(), executeTestCaseDto.getRunCount(), executeTestCaseDto.getTestCaseStepId(), executeTestCaseDto.getRerunTime(), executeTestCaseDto.getStepUpdateTime(), executeTestCaseDto.getCaseRunDuration(), executeTestCaseDto.getCaseTotalPeriod();
                retList.add(executeTestCaseDto);
            }
            testCasesExecution.setRerunTime(new Date();
            testCasesExecution.setRunFlag(0);
            LambdaUpdateWrapper<TestCasesExecution> wrapper = new LambdaUpdateWrapper<TestCasesExecution>().eq(TestCasesExecution::getTestCaseId, executeTestCaseRunDto.getTestCaseId().eq(TestCasesExecution::getTestCycleId, executeTestCaseRunDto.getTestCycleId().eq(TestCasesExecution::getProjectId, executeTestCaseRunDto.getProjectId().eq(TestCasesExecution::getRunCount, currentCount).set(TestCasesExecution::getRerunTime, testCasesExecution.getRerunTime().set(TestCasesExecution::getRunFlag, testCasesExecution.getRunFlag();
            //执行更新
            testCycleTcDao.update(null, wrapper);
        }
        startPage();
        return new Resp.Builder<PageInfo<Object>>().setData(PageInfo.of(retList).ok();
    }
    @Override
    public Resp<String> runTestCase(TestCaseRunDto testCaseRunDto) throws ParseException {
        // 获取 - rerunTime 从表 test_execution
        // rerunTime != null , 表时当前操作是再执行
        // 用在方法 ： setCaseRunDuration(),setCaseTotalPeriod()
        ExecuteTestCaseDto latestExe = testCycleTcDao.getLatest(testCaseRunDto);
        // 获取当前运行run count：
        List&lt;ExecuteTestCaseDto> execute = getExecuteTestCaseList(testCaseRunDto);
        int runCount = execute.stream().findFirst().isPresent() ? execute.stream().findFirst().get().getRunCount() : 0;
        System.out.println("max runCount " + runCount);
        boolean isBatchRun = testCaseRunDto.getTestCaseStepId() == null; // true: batchRun
        boolean isReRun = latestExe.getRerunTime() != null;  // true: reRun
        Date currentTime = new Date();
        int upExecute;
        int upJoinRunStatus;
        // 获取最大的 stepUpdateTime
        // 假如 rerunTime!=null, 获取 上一轮执行中多个步骤中最后一次执行的步骤的 update_time
        if (isReRun) {
            if (isBatchRun) {
                System.out.println("当前运行为 再执行 且为 批量运行");
                long currentStepNetDuration = testCaseNetDuration(latestExe, currentTime);
                long currentTestCaseDuration = testCaseDuration(latestExe, currentTime, currentStepNetDuration);
                long naturalDuration = calculateNaturalTime(currentTime, latestExe.getStepUpdateTime();
                testCaseRunDto.setCaseRunDuration(currentTestCaseDuration);
                long totalCasePeriod = testCaseExecutionTotalCasePeriod(naturalDuration, currentTestCaseDuration);
                // 设置 case_total_period - 计算 test_execution 表
                testCaseRunDto.setCaseTotalPeriod(totalCasePeriod);
                testCaseRunDto.setStepUpdateTime(currentTime);
                // 更新 test_execution 状态, run_status，update_user_id， case_run_duration，case_total_period, step_update
                upExecute = testCycleTcDao.upExecuteStatusCode(testCaseRunDto, runCount, testCaseRunDto.getTestCaseStepId();
                System.out.println("test_execution 表 更新 " + runCount);
                // 计算，更新 本次运行状态 属性 - 为 testCycleJoinTestCase 表的 run_status
                testCaseRunDto.setStatusCode(calculateStatusCode((byte) testCaseRunDto.getStatusCode(), getExecuteTestCaseList(testCaseRunDto);
                TestCycleJoinTestCase testCycleJoinTestCaseByCaseId = testCycleJoinTestCaseDao.getCycleJoinTestCaseByCaseId(testCaseRunDto.getTestCaseId(), Long.valueOf(testCaseRunDto.getProjectId(), testCaseRunDto.getTestCycleId();
                testCaseRunDto.setCaseTotalPeriod(testCycleJoinTestCaseByCaseId.getCaseTotalPeriod() + getTesCaseExecutionTotalPeriod(currentStepNetDuration,naturalDuration) );
                // 更新 test_cycle_join_test_case 表
                upJoinRunStatus = testCycleJoinTestCaseDao.updateRunStatus(testCaseRunDto, jwtUserService.getUserLoginInfo().getSysUser().getId();
            } else {
                System.out.println("当前运行为 再执行 且为 步骤运行");
                testCaseRunDto.setRunFlag(1);
                // if flag =1, 当前运行 再执行 且不是第一次运行
                // if flag =0, 当前运行再执行，且是第一次运行
                int flag = testCycleTcDao.getIsFlag(testCaseRunDto, runCount);
                System.out.println("flag rerun info ： " + flag);
                long currentStepNetDuration = testCaseNetDuration(latestExe, currentTime);
                long currentTestCaseDuration = testCaseDuration(latestExe, currentTime, currentStepNetDuration);
                // 临时存储 前端传递 的 test_case_step_id
                // 并且设置 test_case_step_id 为空
                // 获取 所有记录  保存 至 latest
                long testCaseStepId = testCaseRunDto.getTestCaseStepId();
                testCaseRunDto.setTestCaseStepId(null);
                // 上轮中 最新时间
                ExecuteTestCaseDto latest = testCycleTcDao.getLatest(testCaseRunDto);
                long naturalDuration = calculateNaturalTime(currentTime, latest.getStepUpdateTime();
                // 重置设置 test_case_step_id 属性为原有值，保证后续查询不受影响
                testCaseRunDto.setTestCaseStepId(testCaseStepId);
                System.out.println("currentStepNetDuration： " + currentStepNetDuration);
                System.out.println("currentTestCaseDuration：" + currentTestCaseDuration);
                System.out.println("latest.getStepUpdateTime()： // 获取后一条更新记录" + latest.getStepUpdateTime();
                System.out.println("naturalDuration： " + naturalDuration);
                testCaseRunDto.setCaseRunDuration(currentTestCaseDuration);
                long testCaseTotalPeriod = currentStepNetDuration + latestExe.getCaseTotalPeriod();
                System.out.println("latest.getCaseTotalPeriod()： " + latestExe.getCaseTotalPeriod();
                System.out.println("testCaseTotalPeriod -" + testCaseTotalPeriod);
                // 设置 case_total_period - 计算 test_execution 表
                testCaseRunDto.setCaseTotalPeriod(testCaseTotalPeriod);
                testCaseRunDto.setStepUpdateTime(currentTime);
                // 更新 test_execution 状态 ,  run_status，update_user_id， case_run_duration，case_total_period, step_update
                // 再次设置runFlag属性确保只在第一步时更新为 1
                testCaseRunDto.setRunFlag(Objects.equals(flag, 1) ? 0 : 1);
                upExecute = testCycleTcDao.upExecuteStatusCode(testCaseRunDto, runCount, testCaseRunDto.getTestCaseStepId();
                System.out.println(" test_execution 表 更新 " + runCount);
                // 计算，更新 本次运行状态 属性 - 为 testCycleJoinTestCase 表的 run_status
                testCaseRunDto.setStatusCode(calculateStatusCode((byte) testCaseRunDto.getStatusCode(), getExecuteTestCaseList(testCaseRunDto);
                TestCycleJoinTestCase cycleJoinTestCaseByCaseId = testCycleJoinTestCaseDao.getCycleJoinTestCaseByCaseId(testCaseRunDto.getTestCaseId(), Long.valueOf(testCaseRunDto.getProjectId(), testCaseRunDto.getTestCycleId();
                // getLatestCaseTotalPeriod
                long getLatestCaseTotalPeriod = Objects.equals(flag, 1) ? latest.getCaseTotalPeriod() : 0;
                System.out.println("cycleJoinTestCaseByCaseId.getCaseTotalPeriod() - " + cycleJoinTestCaseByCaseId.getCaseTotalPeriod();
                System.out.println("testCaseTotalPeriod -" + testCaseTotalPeriod);
                System.out.println("getLatestCaseTotalPeriod() - "+  getLatestCaseTotalPeriod);
                testCaseRunDto.setCaseRunDuration(cycleJoinTestCaseByCaseId.getCaseRunDuration() + currentStepNetDuration);
                System.out.println("cycleJoinTestCaseByCaseId.getCaseRunDuration() - "+  getLatestCaseTotalPeriod);
                testCaseRunDto.setCaseTotalPeriod(cycleJoinTestCaseByCaseId.getCaseTotalPeriod() + currentStepNetDuration);
                // 更新 test_cycle_join_test_case 表
                upJoinRunStatus = testCycleJoinTestCaseDao.updateRunStatus(testCaseRunDto, jwtUserService.getUserLoginInfo().getSysUser().getId();
            }
        } else {
            System.out.println("当前运行为新执行：");
            if (runCount == 1) {
                System.out.println("当前运行为第一次运行");
                // 当前服务器当前时间 - 为了更新 运行step_update_time
                // 计算，设置 case_duration
                testCaseRunDto.setCaseRunDuration(getDuration(latestExe, currentTime);
                testCaseRunDto.setCaseTotalPeriod(testCaseRunDto.getCaseRunDuration();
                testCaseRunDto.setStepUpdateTime(currentTime);
                // 更新 test_execution 状态 ,  run_status，update_user_id， case_run_duration，case_total_period, step_update
                upExecute = testCycleTcDao.upExecuteStatusCode(testCaseRunDto, runCount, testCaseRunDto.getTestCaseStepId();
                System.out.println(" test_execution 表 更新 " + runCount);
                // 计算，更新 本次运行状态 属性 - 为 testCycleJoinTestCase 表的 run_status
                testCaseRunDto.setStatusCode(calculateStatusCode((byte) testCaseRunDto.getStatusCode(), getExecuteTestCaseList(testCaseRunDto);
                // 更新 test_cycle_join_test_case 表
                upJoinRunStatus = testCycleJoinTestCaseDao.updateRunStatus(testCaseRunDto, jwtUserService.getUserLoginInfo().getSysUser().getId();
            } else {
                System.out.println("当前运行 不属于 新执行 中的第一次， 当前运行 runCount ：" + runCount);
                if (isBatchRun) {
                    System.out.println("当前运行 不属于 新执行 中的第一次， 当前运行 runCount ：" + runCount + "且是批量运行");
                    // 计算，设置 case_duration
                    testCaseRunDto.setCaseRunDuration(getDuration(latestExe, currentTime);
                    testCaseRunDto.setCaseTotalPeriod(testCaseRunDto.getCaseRunDuration();
                    testCaseRunDto.setStepUpdateTime(currentTime);
                    // 更新 test_execution 状态 ,  run_status，update_user_id， case_run_duration，case_total_period, step_update
                    upExecute = testCycleTcDao.upExecuteStatusCode(testCaseRunDto, runCount, testCaseRunDto.getTestCaseStepId();
                    System.out.println(" test_execution 表 更新 " + runCount);
                    // 计算，更新 本次运行状态 属性 - 为 testCycleJoinTestCase 表的 run_status
                    testCaseRunDto.setStatusCode(calculateStatusCode((byte) testCaseRunDto.getStatusCode(), getExecuteTestCaseList(testCaseRunDto);
                    TestCycleJoinTestCase cycleJoinTestCaseByCaseId = testCycleJoinTestCaseDao.getCycleJoinTestCaseByCaseId(testCaseRunDto.getTestCaseId(), Long.valueOf(testCaseRunDto.getProjectId(), testCaseRunDto.getTestCycleId();
                    testCaseRunDto.setCaseTotalPeriod(testCaseRunDto.getCaseTotalPeriod() + cycleJoinTestCaseByCaseId.getCaseTotalPeriod();
                    // 更新 test_cycle_join_test_case 表
                    upJoinRunStatus = testCycleJoinTestCaseDao.updateRunStatus(testCaseRunDto, jwtUserService.getUserLoginInfo().getSysUser().getId();
                } else {
                    System.out.println("当前运行 不属于 新执行 中的第一次， 当前运行 runCount ：" + runCount + "不是是批量运行");
                    // 当前服务器当前时间 - 为了更新 运行step_update_time
                    // 计算，设置 case_duration
                    testCaseRunDto.setCaseRunDuration(getDuration(latestExe, currentTime);
                    testCaseRunDto.setCaseTotalPeriod(testCaseRunDto.getCaseRunDuration();
                    testCaseRunDto.setStepUpdateTime(currentTime);
                    // 更新 test_execution 状态 ,  run_status，update_user_id， case_run_duration，case_total_period, step_update
                    upExecute = testCycleTcDao.upExecuteStatusCode(testCaseRunDto, runCount, testCaseRunDto.getTestCaseStepId();
                    System.out.println(" test_execution 表 更新 " + runCount);
                    // 计算，更新 本次运行状态 属性 - 为 testCycleJoinTestCase 表的 run_status
                    testCaseRunDto.setStatusCode(calculateStatusCode((byte) testCaseRunDto.getStatusCode(), getExecuteTestCaseList(testCaseRunDto);
                    TestCycleJoinTestCase cycleJoinTestCaseByCaseId = testCycleJoinTestCaseDao.getCycleJoinTestCaseByCaseId(testCaseRunDto.getTestCaseId(), Long.valueOf(testCaseRunDto.getProjectId(), testCaseRunDto.getTestCycleId();
                    testCaseRunDto.setCaseTotalPeriod(testCaseRunDto.getCaseTotalPeriod() + cycleJoinTestCaseByCaseId.getCaseTotalPeriod() - cycleJoinTestCaseByCaseId.getCaseRunDuration();
                    // 更新 test_cycle_join_test_case 表
                    upJoinRunStatus = testCycleJoinTestCaseDao.updateRunStatus(testCaseRunDto, jwtUserService.getUserLoginInfo().getSysUser().getId();
                }
            }
        }
        if (upExecute > 0 && upJoinRunStatus > 0) {
            return new Resp.Builder<String>().ok();
        }
        return new Resp.Builder<String>().fail();
    }
    private List&lt;ExecuteTestCaseDto> getExecuteTestCaseList(TestCaseRunDto testCaseRunDto) {
        executeTestCaseRunDto.setTestCycleId(testCaseRunDto.getTestCycleId();
        executeTestCaseRunDto.setTestCaseId(testCaseRunDto.getTestCaseId();
        executeTestCaseRunDto.setProjectId(testCaseRunDto.getProjectId();
        return testCycleTcDao.queryList(executeTestCaseRunDto);
    }
    private byte calculateStatusCode(byte runCode, List&lt;ExecuteTestCaseDto> execute) {
        if (!Objects.equals(runCode, StatusCode.FAIL.getValue() {
            //初始化 成功、无效、跳过 的次数
            byte passNum = 0, invalidNum = 0, skipNum = 0;
            int executeSize = execute.size();
            for (ExecuteTestCaseDto testCaseDto : execute) {
                // 将状态码存储在变量中
                byte statusCode = (byte) (testCaseDto.getStatusCode();
                switch (Objects.requireNonNull(StatusCode.getByValue(statusCode) {
                    case FAIL:
                        return StatusCode.FAIL.getValue();
                    case BLOCKED:
                        return StatusCode.BLOCKED.getValue();
                    case NO_RUN:
                        return StatusCode.NOT_COMPLETED.getValue();
                    case SKIP:
                        skipNum++;
                        // 全部为跳过，则直接返回
                        if (Objects.equals(executeSize, skipNum) {
                            return StatusCode.SKIP.getValue();
                        }
                        break;
                    case PASS:
                        passNum++;
                        // 全部为成功，则直接返回
                        if (Objects.equals(executeSize, passNum) {
                            return StatusCode.PASS.getValue();
                        }
                        break;
                    case INVALID:
                        invalidNum++;
                        // 全部为无效，则直接返回
                        if (Objects.equals(executeSize, invalidNum) {
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
        Date startTime = Objects.nonNull(latestExe.getRerunTime() ? latestExe.getRerunTime() : latestExe.getCreateTime();
        // 计算时间差
        differenceInMillis += Math.subtractExact(time.getTime(), simpleDateFormat.parse(simpleDateFormat.format(startTime).getTime();
        System.out.println("计算 本次运行 或本步骤运行时长： " + "stepUpdate - rerunTime / startTime: " + time.getTime() + " -" + startTime + " = " + differenceInMillis);
        return Objects.nonNull(latestExe.getRerunTime() ? Math.addExact(differenceInMillis, latestExe.getCaseRunDuration() : differenceInMillis;
    }
    /**
     * 获取时间段内 所有日期
     *
     * @param startDate startDate
     * @param endDate   endDate
     * @return java.util.List&lt;java.util.Date>
     * @author Johnson
     */
    private List&lt;Date> getDatesBetween(Date startDate, Date endDate) {
        List&lt;Date> dates = new ArrayList&lt;>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        while (calendar.getTime().before(endDate) {
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
    private int countWeekends(List&lt;Date> dates) {
        int count = 0;
        for (Date date : dates) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            if (Objects.equals(dayOfWeek, Calendar.SATURDAY) || Objects.equals(dayOfWeek, Calendar.SUNDAY) {
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
        long totalTime = Math.subtractExact(endDate.getTime(), startDate.getTime();
        List&lt;Date> dates = getDatesBetween(startDate, endDate);
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
        if ((deductingWeekendMillisecond > eightHoursMilliseconds && deductingWeekendMillisecond <= oneDayMilliseconds) || (Objects.equals(deductingWeekendMillisecond, twoDayMilliseconds) {
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
        if (Objects.nonNull(latestExe.getRerunTime() {
            // 获取自然时间
            long naturalTime = calculateNaturalTime(newStepUpdateTime, latestExe.getStepUpdateTime();
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
        if (Objects.nonNull(latestExe.getRerunTime() {
            // 获取自然时间
            long naturalTime = calculateNaturalTime(newStepUpdateTime, latestExe.getStepUpdateTime();
            System.out.println("⭐⭐️️⭐️ 自然时间:--->>>>>-----(系统当前时间)日期：(" + newStepUpdateTime + ")" + newStepUpdateTime.getTime() + " − (pre_stepUpdateTime)）日期：(" + latestExe.getStepUpdateTime() + ")" + latestExe.getStepUpdateTime().getTime() + " = " + naturalTime);
            // totalPeriod > 为了更新test_execution
            totalPeriod += naturalTime;
            System.out.println("⭐⭐️️⭐️ total:--->>>>>-----(当前duration)" + duration + " + 自然时间：" + naturalTime + " = " + totalPeriod);
        }
        return totalPeriod;
    }
    /**
     * 再次批量执行时的duration
     * @author Johnson
     *
     * @param latestExe latestExe
     * @param time time
     * @return long
     */
    private long testCaseDuration(ExecuteTestCaseDto latestExe, Date time, long currentStepNetDuration) throws ParseException {
        System.out.println("计算 本次运行 或 本步骤运行时长： " + "stepUpdate - rerunTime + pre_duration ");
        System.out.println((formatTimestampToNaturalDate(time.getTime() + " -" + latestExe.getRerunTime() + " = " +  currentStepNetDuration);
        System.out.println(" + " + latestExe.getCaseRunDuration() );
        long currentStepDuration = latestExe.getCaseRunDuration() + currentStepNetDuration;
        System.out.println(currentStepDuration);
        return currentStepDuration;
    }
    public static String formatTimestampToNaturalDate(long timestamp) {
        // Create a Date object using the timestamp
        Date date = new Date(timestamp);
        // Create a SimpleDateFormat object with your desired format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // Format the date using the SimpleDateFormat
        return dateFormat.format(date);
    }
    /**
     * 再次批量执行时的total
     * @author Johnson
     *
     * @param latestExe latestExe
     * @param time time
     * @return long
     */
    private long testCaseExecutionTotalCasePeriod(long naturalDuration, long currentStepDuration){
        System.out.println("计算 本次运行total 或本步骤运行时长： " +  "(已有运行时长 + stepUpdate - rerunTime) + 自然时间 ");
        System.out.println( currentStepDuration + " + " +  naturalDuration );
        long currentTotal = currentStepDuration + naturalDuration;
        System.out.println( " = " + currentTotal );
        return currentTotal;
    }
    private long getTestCycleJoinTestCaseTotalReRun(ExecuteTestCaseDto latestExe, Date time, Date latestStepUpdateTime) {
        long naturalTime = calculateNaturalTime(time, latestStepUpdateTime);
        long differenceInMillis;
        try {
            differenceInMillis = testCaseNetDuration(latestExe, time);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return naturalTime + differenceInMillis;
    }
    private long getTesCaseExecutionTotalPeriod(long netStepDuration, long natureDuration ){
        // nature + net
        System.out.println( "netStepDuration + natureDuration = " + netStepDuration + natureDuration);
        return netStepDuration + natureDuration;
    }
    /**
     * 再次执行时的时间差
     * @author Johnson
     *
     * @param latestExe latestExe
     * @param time time
     * @return long
     */
    private long testCaseNetDuration(ExecuteTestCaseDto latestExe, Date time) throws ParseException {
        // 计算当次运行时长：
        return Math.subtractExact(time.getTime(), simpleDateFormat.parse(simpleDateFormat.format(latestExe.getRerunTime().getTime();
    }
}
}
}
