package com.hu.oneclick.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageInfo;
import com.hu.oneclick.common.page.BaseController;
import com.hu.oneclick.common.util.PageUtil;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.TestCase;
import com.hu.oneclick.model.domain.TestCycle;
import com.hu.oneclick.model.domain.TestCyclePlan;
import com.hu.oneclick.model.domain.dto.TestCyclePlanSaveDto;
import com.hu.oneclick.quartz.QuartzManager;
import com.hu.oneclick.quartz.domain.JobDetails;
import com.hu.oneclick.server.service.TestCyclePlanService;
import com.hu.oneclick.server.service.TestCycleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.quartz.TriggerUtils;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 测试周期 - 运行计划
 *
 * @author xiaohai
 * @date 2023/08/25
 */
@RestController
@RequestMapping("/testCycle/plan")
@Api(tags = "测试周期 - 运行计划")
@Slf4j
public class TestCyclePlanController extends BaseController {

    // 测试周期计划组名前缀
    private final static String GROUP_PREFIX = "TEST_CYCLE";
    private final static String JOB_PREFIX = "TEST_CYCLE_JOB";
    @SuppressWarnings("unchecked")
    private static Class<? extends QuartzJobBean> getClass(String classname) throws Exception {
        Class<?> class1 = Class.forName(classname);
        return (Class<? extends QuartzJobBean>) class1;
    }

    @Resource
    private TestCyclePlanService testCyclePlanService;
    @Resource
    private TestCycleService testCycleService;
    @Resource
    private QuartzManager qtzManager;


    @ApiOperation("列表")
    @PostMapping("/list/{testCycleId}")
    public Resp<PageInfo<JobDetails>> list(@ApiParam("测试周期ID") @PathVariable Long testCycleId) {
        try {
            List<JobDetails> jobDetails = qtzManager.queryAllJobBeanByGroup(StrUtil.format("{}_{}", GROUP_PREFIX, testCycleId));
            return new Resp.Builder<PageInfo<JobDetails>>().setData(PageUtil.manualPaging(jobDetails)).ok();
        } catch (Exception e) {
            log.error("查询失败，原因：" + e.getMessage(), e);
            return new Resp.Builder<PageInfo<JobDetails>>().fail();
        }
    }

    @ApiOperation("新增")
    @PostMapping("/save")
    public Resp<TestCyclePlan> save(@RequestBody @Validated TestCyclePlanSaveDto dto) {
        try {
            // 添加执行任务
            String jobName = StrUtil.format("{}_{}", JOB_PREFIX, IdUtil.getSnowflakeNextIdStr());
            String jobGroupName = StrUtil.format("{}_{}", GROUP_PREFIX, dto.getTestCycleId());
            // 任务参数
            Map<String, Object> jobDataMap = new HashMap<>();
            jobDataMap.put("jenkinsJobName", dto.getJenkinsJobName());
            qtzManager.addJob(getClass("com.hu.oneclick.quartz.task.BuildJobTask"),
                    jobName,
                    jobGroupName,
                    dto.getCronExpression(),
                    jobDataMap);
            // 保存任务记录
            TestCycle testCycle = testCycleService.getById(dto.getTestCycleId());
            TestCyclePlan testCyclePlan = new TestCyclePlan();
            testCyclePlan.setTestCycleId(testCycle.getId());
            testCyclePlan.setTestCycleTitle(testCycle.getTitle());
            testCyclePlan.setJobName(jobName);
            testCyclePlan.setJobGroup(jobGroupName);
            testCyclePlanService.save(testCyclePlan);
            return new Resp.Builder<TestCyclePlan>().setData(testCyclePlan).ok();
        } catch (Exception e) {
            log.error("新增失败，原因：" + e.getMessage(), e);
            return new Resp.Builder<TestCyclePlan>().fail();
        }
    }

    @ApiOperation("详情")
    @GetMapping("/info/{id}")
    public Resp<TestCyclePlan> info(@PathVariable Long id) {
        try {
            TestCyclePlan testCyclePlan = testCyclePlanService.getById(id);
            JobDetails jobDetails = qtzManager.jobInfo(testCyclePlan.getJobName(), testCyclePlan.getJobGroup());
            // 计算计划任务最近十次运行时间
            List<String> runTimeList = new ArrayList<>();
            CronTriggerImpl cronTriggerImpl = new CronTriggerImpl();
            cronTriggerImpl.setCronExpression(jobDetails.getCronExpression());
            List<Date> dates = TriggerUtils.computeFireTimes(cronTriggerImpl, null, 10);
            testCyclePlan.setJobDetails(jobDetails);
            testCyclePlan.setRunTimeList(dates.stream().map(DateUtil::formatDateTime).collect(Collectors.toList()));
            return new Resp.Builder<TestCyclePlan>().setData(testCyclePlan).ok();
        } catch (Exception e) {
            log.error("查询失败，原因：" + e.getMessage(), e);
            return new Resp.Builder<TestCyclePlan>().fail();
        }
    }

    @ApiOperation("删除")
    @DeleteMapping("/delete/{ids}")
    public Resp<?> delete(@PathVariable Long[] ids) {
        try {
            List<TestCyclePlan> testCyclePlanList = testCyclePlanService.listByIds(Arrays.asList(ids));
            if (CollUtil.isEmpty(testCyclePlanList)) {
                return new Resp.Builder<TestCase>().ok();
            }
            // 删除计划任务
            for (TestCyclePlan testCyclePlan : testCyclePlanList) {
                qtzManager.deleteJob(testCyclePlan.getJobName(), testCyclePlan.getJobGroup());
            }
            // 删除计划记录
            testCyclePlanService.removeBatchByIds(Arrays.asList(ids));
        } catch (Exception e) {
            log.error("删除失败，原因：" + e.getMessage(), e);
            return new Resp.Builder<>().fail();
        }
        return new Resp.Builder<>().ok();
    }

}
