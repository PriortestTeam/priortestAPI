package com.hu.oneclick.quartz.controller;

import com.hu.oneclick.common.util.PageUtil;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.quartz.QuartzManager;
import com.hu.oneclick.quartz.domain.JobDetails;
import com.hu.oneclick.quartz.domain.JobOperateDto;
import com.hu.oneclick.quartz.domain.JobSaveDto;
import com.hu.oneclick.quartz.domain.JobUpdateDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/job")
@Tag(name = "定时任务调度管理", description = "定时任务调度管理相关接口")
@Slf4j
public class JobController {

    @Resource
    private QuartzManager qtzManager;


    @SuppressWarnings("unchecked")
    private static Class<? extends QuartzJobBean> getClass(String classname) throws Exception {
        Class<?> class1 = Class.forName(classname);
        return (Class<? extends QuartzJobBean>) class1;
    }

    @Operation(summary = "添加任务")
    @PostMapping(value = "/addJob")
    public Resp<?> addJob(@RequestBody @Validated JobSaveDto dto) {
        try {
            qtzManager.addJob(getClass(dto.getJobClassName()), dto.getJobName(), dto.getJobGroupName(), dto.getCronExpression(), dto.getJobDataMap());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new Resp.Builder<>().fail();
        }
        return new Resp.Builder<>().ok();
    }

    @Operation(summary = "更新任务")
    @PutMapping(value = "/updateJob")
    public Resp<?> updateJob(@RequestBody @Validated JobUpdateDto dto) {
        try {
            qtzManager.updateJob(dto.getJobName(), dto.getJobGroupName(), dto.getCronExpression(), dto.getJobDataMap());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new Resp.Builder<>().fail();
        }
        return new Resp.Builder<>().ok();
    }

    @Operation(summary = "任务详情")
    @GetMapping(value = "/jobInfo")
    public Resp<?> jobInfo(@RequestParam(value = "jobName") String jobName,
                           @RequestParam(value = "jobGroupName", defaultValue = "DEFAULT", required = false) String jobGroupName) {
        try {
            JobDetails jobDetails = qtzManager.jobInfo(jobName, jobGroupName);
            return new Resp.Builder<>().setData(jobDetails).ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new Resp.Builder<>().fail();
        }
    }

    @Operation(summary = "暂停任务")
    @PutMapping("/pauseJob")
    public Resp<?> pauseJob(@RequestBody @Validated JobOperateDto dto) {
        try {
            qtzManager.pauseJob(dto.getJobName(), dto.getJobGroupName());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new Resp.Builder<>().fail();
        }
        return new Resp.Builder<>().ok();
    }

    @Operation(summary = "恢复任务")
    @PutMapping("/resumeJob")
    public Resp<?> resumeJob(@RequestBody @Validated JobOperateDto dto) {
        try {
            qtzManager.resumeJob(dto.getJobName(), dto.getJobGroupName());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new Resp.Builder<>().fail();
        }
        return new Resp.Builder<>().ok();
    }

    @Operation(summary = "删除任务")
    @DeleteMapping("/deleteJob")
    public Resp<?> deleteJob(@RequestBody @Validated JobOperateDto dto) {
        try {
            qtzManager.deleteJob(dto.getJobName(), dto.getJobGroupName());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new Resp.Builder<>().fail();
        }
        return new Resp.Builder<>().ok();
    }

    @Operation(summary = "查询任务列表")
    @GetMapping(value = "/queryJob")
    public Resp<?> queryJob() {
        try {
            List<JobDetails> jobAndTrigger = qtzManager.queryAllJobBean();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("number", jobAndTrigger.size());
            map.put("JobAndTrigger", PageUtil.manualPaging(jobAndTrigger));
            return new Resp.Builder<>().setData(map).ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new Resp.Builder<>().fail();
        }
    }

}
