package com.hu.oneclick.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageInfo;
import com.hu.oneclick.common.page.BaseController;
import com.hu.oneclick.common.util.PageUtil;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.entity.TestCase;
import com.hu.oneclick.model.entity.TestCycle;
import com.hu.oneclick.model.entity.TestCyclePlan;
import com.hu.oneclick.model.domain.dto.TestCyclePlanSaveDto;
import com.hu.oneclick.quartz.QuartzManager;
import com.hu.oneclick.quartz.domain.JobDetails;
import com.hu.oneclick.server.service.TestCyclePlanService;
import com.hu.oneclick.server.service.TestCycleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.quartz.TriggerUtils;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
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
@Tag(name = "测试周期 - 运行计划", description = "测试周期 - 运行计划相关接口")
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


    @Operation(summary = "列表")
    @PostMapping("/list/{testCycleId}")
    public Resp<PageInfo<JobDetails>> list(@Parameter(description = "测试周期ID") @PathVariable Long testCycleId) {
        try {
            List<JobDetails> jobDetails = qtzManager.queryAllJobBeanByGroup(StrUtil.format("{}_{}", GROUP_PREFIX, testCycleId));
            return new Resp.Builder<PageInfo<JobDetails>>().setData(PageUtil.manualPaging(jobDetails)).ok();
        } catch (Exception e) {
            log.error("查询失败，原因：" + e.getMessage(), e);
            return new Resp.Builder<PageInfo<JobDetails>>().fail();
        }
    }

    @Operation(summary = "新增")
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

    @Operation(summary = "详情")
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

    @Operation(summary = "删除")
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

    @Operation(summary = "根据计划ID查询测试周期计划")
    @GetMapping("/{planId}")
    public Resp<TestCyclePlanSaveDto> getTestCyclePlan(@PathVariable("planId") Long planId) {
        try {
            TestCyclePlan testCyclePlan = testCyclePlanService.getById(planId);
            if (testCyclePlan == null) {
                return new Resp.Builder<TestCyclePlanSaveDto>().buildResult("未找到对应的测试周期计划");
            }
            TestCyclePlanSaveDto dto = new TestCyclePlanSaveDto();
            // convert TestCyclePlan to TestCyclePlanSaveDto
            dto.setTestCycleId(testCyclePlan.getTestCycleId());
            dto.setJenkinsJobName(testCyclePlan.getJobName());
            JobDetails jobDetails = qtzManager.jobInfo(testCyclePlan.getJobName(), testCyclePlan.getJobGroup());
            if (jobDetails != null) {
                dto.setCronExpression(jobDetails.getCronExpression());
            }
            return new Resp.Builder<TestCyclePlanSaveDto>().setData(dto).ok();
        } catch (Exception e) {
            log.error("查询失败，原因：" + e.getMessage(), e);
            return new Resp.Builder<TestCyclePlanSaveDto>().fail();
        }
    }

    @Operation(summary = "创建测试周期计划")
    @PostMapping
    public Resp<TestCyclePlan> createTestCyclePlan(@RequestBody @Validated TestCyclePlanSaveDto dto) {
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

    @Operation(summary = "更新测试周期计划")
    @PutMapping("/{planId}")
    public Resp<TestCyclePlan> updateTestCyclePlan(@PathVariable("planId") Long planId, @RequestBody @Validated TestCyclePlanSaveDto dto) {
        try {
            TestCyclePlan testCyclePlan = testCyclePlanService.getById(planId);
            if (testCyclePlan == null) {
                return new Resp.Builder<TestCyclePlan>().buildResult("未找到对应的测试周期计划");
            }
            // 更新计划任务
            JobDetails jobDetails = qtzManager.jobInfo(testCyclePlan.getJobName(), testCyclePlan.getJobGroup());
            if (jobDetails == null) {
                return new Resp.Builder<TestCyclePlan>().buildResult("未找到对应的计划任务");
            }
            if (StrUtil.isNotBlank(dto.getJenkinsJobName())) {
                Map<String, Object> jobDataMap = new HashMap<>();
                jobDataMap.put("jenkinsJobName", dto.getJenkinsJobName());
                qtzManager.updateJob(testCyclePlan.getJobName(), testCyclePlan.getJobGroup(), dto.getCronExpression(), jobDataMap);
            }
            return new Resp.Builder<TestCyclePlan>().buildResult("更新成功");
        } catch (Exception e) {
            log.error("更新失败，原因：" + e.getMessage(), e);
            return new Resp.Builder<TestCyclePlan>().fail();
        }
    }

    @Operation(summary = "删除测试周期计划")
    @DeleteMapping("/{planId}")
    public Resp<?> deleteTestCyclePlan(@PathVariable("planId") Long planId) {
        try {
            TestCyclePlan testCyclePlan = testCyclePlanService.getById(planId);
            if (testCyclePlan == null) {
                return new Resp.Builder<TestCase>().ok();
            }
            // 删除计划任务
            qtzManager.deleteJob(testCyclePlan.getJobName(), testCyclePlan.getJobGroup());
            // 删除计划记录
            testCyclePlanService.removeById(planId);
        } catch (Exception e) {
            log.error("删除失败，原因：" + e.getMessage(), e);
            return new Resp.Builder<>().fail();
        }
        return new Resp.Builder<>().ok();
    }

}