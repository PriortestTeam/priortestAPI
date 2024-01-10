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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.hu.oneclick.common.util.PageUtil.startPage;

/**
 * @author Johnson
 */
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
    @Resource
    TestCycleJoinTestCase testCycleJoinTestCase;
    @Resource
    TestCasesExecution testCasesExecution;

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

    @Resource
    ExecuteTestCaseRunDto executeTestCaseRunDto;

    @Resource
    TestCycleJoinTestCaseDao testCycleJoinTestCaseDao;

    @Override
    public Resp<String> runTestCase(TestCaseRunDto testCaseRunDto) throws ParseException {
        // 获取最新的时间信息
        ExecuteTestCaseDto latestExe = testCycleTcDao.getLatest(testCaseRunDto);
        // 当前系统时间
        Date time = new Date();
        // 设置 duration
        testCaseRunDto.setCaseRunDuration(getDuration(latestExe, time));
        // 设置 totalPeriod
        testCaseRunDto.setCaseTotalPeriod(getTotalPeriod(latestExe, time, testCaseRunDto.getCaseRunDuration()));
        // 设置更新时间
        testCaseRunDto.setStepUpdateTime(time);
        // 查询最新一轮的execute记录
        List<ExecuteTestCaseDto> execute = getExecuteTestCaseList(testCaseRunDto);
        int runCount = execute.stream().findFirst().isPresent() ? execute.stream().findFirst().get().getRunCount() : 0;
        // 更新 execute 状态
        int upExecute = testCycleTcDao.upExecuteStatusCode(testCaseRunDto, runCount, testCaseRunDto.getTestCaseStepId());
        // 更新 testCycleJoinTestCase 表的 状态
        testCaseRunDto.setStatusCode(calculateStatusCode((byte) testCaseRunDto.getStatusCode(), getExecuteTestCaseList(testCaseRunDto)));

        int upJoinRunStatus = testCycleJoinTestCaseDao.updateRunStatus(testCaseRunDto, jwtUserService.getUserLoginInfo().getSysUser().getId());
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

    @Resource
    SimpleDateFormat simpleDateFormat;

    @Bean
    public SimpleDateFormat simpleDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 计算当前步骤运行时间
     * @author Johnson
     *
     * @param latestExe latestExe
     * @param time 当前时间
     * @return long
     */
    private long getDuration(ExecuteTestCaseDto latestExe, Date time) throws ParseException {
        long differenceInMillis = 0;
        // 如果再次执行时间不为空，则开始时间为再次执行时间
        Date startTime = Objects.nonNull(latestExe.getRerunTime()) ? latestExe.getRerunTime() : latestExe.getCreateTime();

        // 计算时间差
        differenceInMillis += Math.subtractExact(time.getTime(), simpleDateFormat.parse(simpleDateFormat.format(startTime)).getTime());

        return Objects.nonNull(latestExe.getRerunTime()) ? Math.addExact(differenceInMillis, latestExe.getCaseRunDuration()) : differenceInMillis;
    }

    /**
     * 获取时间段内 所有日期
     * @author Johnson
     *
     * @param startDate startDate
     * @param endDate endDate
     * @return java.util.List<java.util.Date>
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
     * @author Johnson
     *
     * @param dates dates
     * @return int
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
     * @author Johnson
     *
     * @param startDate startDate
     * @param endDate endDate
     * @return long
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
     * @author Johnson
     *
     * @param newStepUpdateTime newStepUpdateTime 当前时间
     * @param preStepUpdateTime preStepUpdateTime 上次执行时间
     * @return long
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
     * @author Johnson
     *
     * @param latestExe latestExe
     * @param newStepUpdateTime newStepUpdateTime
     * @param duration duration
     * @return long
     */
    private long getTotalPeriod(ExecuteTestCaseDto latestExe, Date newStepUpdateTime, long duration) {
        long totalPeriod = duration;
        if (Objects.nonNull(latestExe.getRerunTime())) {
            // 获取自然时间
            long naturalTime = calculateNaturalTime(newStepUpdateTime, latestExe.getStepUpdateTime());
            System.out.println("自然时间:--->>>>>-----(系统当前时间)" + newStepUpdateTime.getTime() + "-(pre_stepUpdateTime)）" + latestExe.getStepUpdateTime().getTime() + "=" + naturalTime);
            // 计算时间差
            long upMinusRerunTime = Math.subtractExact(newStepUpdateTime.getTime(), latestExe.getRerunTime().getTime());
            // 计算caseTotalPeriod
            totalPeriod = Math.addExact(upMinusRerunTime, naturalTime);
        }

        return latestExe.getRunCount() > 1 ? Math.addExact(totalPeriod, latestExe.getCaseTotalPeriod()) : totalPeriod;
    }
}
