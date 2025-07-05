package com.hu.oneclick.quartz;
import com.hu.oneclick.quartz.domain.JobDetails;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import jakarta.annotation.Resource;
import java.util.*;
@Component
public class QuartzManager {
    @Resource
    private Scheduler scheduler;
    /**
     * 创建or更新任务，存在则更新不存在创建
     *
     * @param jobClass     任务类
     * @param jobName      任务名称
     * @param jobGroupName 任务组名称
     * @param jobCron      cron表达式
     */
    public void addOrUpdateJob(Class<? extends QuartzJobBean> jobClass, String jobName, String jobGroupName, String jobCron, Map<String, Object> jobDataMap) {
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroupName);
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            if (trigger == null) {
                addJob(jobClass, jobName, jobGroupName, jobCron, jobDataMap);
            } else {
                if (trigger.getCronExpression().equals(jobCron) {
                    return;
                }
                updateJob(jobName, jobGroupName, jobCron, jobDataMap);
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
    /**
     * 增加一个job
     *
     * @param jobClass     任务实现类
     * @param jobName      任务名称
     * @param jobGroupName 任务组名
     * @param jobCron      cron表达式(如：0/5 * * * * ? )
     */
    public void addJob(Class<? extends QuartzJobBean> jobClass, String jobName, String jobGroupName, String jobCron, Map<String, Object> jobDataMap) throws SchedulerException {
        JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobName, jobGroupName).setJobData(new JobDataMap(jobDataMap).build();
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity(jobName, jobGroupName).usingJobData(new JobDataMap(jobDataMap)
                .startAt(DateBuilder.futureDate(1, DateBuilder.IntervalUnit.SECOND)
                .withSchedule(CronScheduleBuilder.cronSchedule(jobCron).startNow().build();
        scheduler.scheduleJob(jobDetail, trigger);
        if (!scheduler.isShutdown() {
            scheduler.start();
        }
    }
    /**
     * @param jobClass
     * @param jobName
     * @param jobGroupName
     * @param jobTime
     */
    public void addJob(Class<? extends Job> jobClass, String jobName, String jobGroupName, int jobTime) {
        addJob(jobClass, jobName, jobGroupName, jobTime, -1);
    }
    public void addJob(Class<? extends Job> jobClass, String jobName, String jobGroupName, int jobTime, int jobTimes) {
        try {
            JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobName, jobGroupName)// 任务名称和组构成任务key
                    .build();
            // 使用simpleTrigger规则
            Trigger trigger;
            if (jobTimes < 0) {
                trigger = TriggerBuilder.newTrigger().withIdentity(jobName, jobGroupName)
                        .withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(1).withIntervalInSeconds(jobTime)
                        .startNow().build();
            } else {
                trigger = TriggerBuilder
                        .newTrigger().withIdentity(jobName, jobGroupName).withSchedule(SimpleScheduleBuilder
                                .repeatSecondlyForever(1).withIntervalInSeconds(jobTime).withRepeatCount(jobTimes)
                        .startNow().build();
            }
            scheduler.scheduleJob(jobDetail, trigger);
            if (!scheduler.isShutdown() {
                scheduler.start();
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
    public void updateJob(String jobName, String jobGroupName, String jobTime, Map<String, Object> jobDataMap) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroupName);
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).usingJobData(new JobDataMap(jobDataMap)
                .withSchedule(CronScheduleBuilder.cronSchedule(jobTime).build();
        // 重启触发器
        scheduler.rescheduleJob(triggerKey, trigger);
    }
    /**
     * 删除任务一个job
     *
     * @param jobName      任务名称
     * @param jobGroupName 任务组名
     */
    public void deleteJob(String jobName, String jobGroupName) throws SchedulerException {
        scheduler.pauseTrigger(TriggerKey.triggerKey(jobName, jobGroupName);
        scheduler.unscheduleJob(TriggerKey.triggerKey(jobName, jobGroupName);
        scheduler.deleteJob(new JobKey(jobName, jobGroupName);
    }
    /**
     * 暂停一个job
     *
     * @param jobName
     * @param jobGroupName
     */
    public void pauseJob(String jobName, String jobGroupName) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(jobName, jobGroupName);
        scheduler.pauseJob(jobKey);
    }
    /**
     * 恢复一个job
     *
     * @param jobName
     * @param jobGroupName
     */
    public void resumeJob(String jobName, String jobGroupName) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(jobName, jobGroupName);
        scheduler.resumeJob(jobKey);
    }
    /**
     * 立即执行一个job
     *
     * @param jobName
     * @param jobGroupName
     */
    public void runAJobNow(String jobName, String jobGroupName) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(jobName, jobGroupName);
        scheduler.triggerJob(jobKey);
    }
    public List<JobDetails> queryAllJobBean() throws SchedulerException {
        GroupMatcher<JobKey> matcher = GroupMatcher.anyJobGroup();
        Set<JobKey> jobKeys = scheduler.getJobKeys(matcher);
        List<JobDetails> jobList = new ArrayList<>();
        for (JobKey jobKey : jobKeys) {
            List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
            for (Trigger trigger : triggers) {
                JobDetails jobDetails = new JobDetails();
                if (trigger instanceof CronTrigger) {
                    CronTrigger cronTrigger = (CronTrigger) trigger;
                    jobDetails.setCronExpression(cronTrigger.getCronExpression();
                    jobDetails.setTimeZone(cronTrigger.getTimeZone().getDisplayName();
                }
                jobDetails.setTriggerGroupName(trigger.getKey().getName();
                jobDetails.setTriggerName(trigger.getKey().getGroup();
                jobDetails.setJobGroupName(jobKey.getGroup();
                jobDetails.setJobName(jobKey.getName();
                jobDetails.setStartTime(trigger.getStartTime();
                jobDetails.setJobClassName(scheduler.getJobDetail(jobKey).getJobClass().getName();
                jobDetails.setNextFireTime(trigger.getNextFireTime();
                jobDetails.setPreviousFireTime(trigger.getPreviousFireTime();
                jobDetails.setStatus(scheduler.getTriggerState(trigger.getKey().name();
                jobDetails.setJobDataMap(trigger.getJobDataMap();
                jobList.add(jobDetails);
            }
        }
        return jobList;
    }
    public List<JobDetails> queryAllJobBeanByGroup(String groupName) throws SchedulerException {
        GroupMatcher<JobKey> matcher = GroupMatcher.jobGroupEquals(groupName);
        Set<JobKey> jobKeys = scheduler.getJobKeys(matcher);
        List<JobDetails> jobList = new ArrayList<>();
        for (JobKey jobKey : jobKeys) {
            List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
            for (Trigger trigger : triggers) {
                JobDetails jobDetails = new JobDetails();
                if (trigger instanceof CronTrigger) {
                    CronTrigger cronTrigger = (CronTrigger) trigger;
                    jobDetails.setCronExpression(cronTrigger.getCronExpression();
                    jobDetails.setTimeZone(cronTrigger.getTimeZone().getDisplayName();
                }
                jobDetails.setTriggerGroupName(trigger.getKey().getName();
                jobDetails.setTriggerName(trigger.getKey().getGroup();
                jobDetails.setJobGroupName(jobKey.getGroup();
                jobDetails.setJobName(jobKey.getName();
                jobDetails.setStartTime(trigger.getStartTime();
                jobDetails.setJobClassName(scheduler.getJobDetail(jobKey).getJobClass().getName();
                jobDetails.setNextFireTime(trigger.getNextFireTime();
                jobDetails.setPreviousFireTime(trigger.getPreviousFireTime();
                jobDetails.setStatus(scheduler.getTriggerState(trigger.getKey().name();
                jobDetails.setJobDataMap(trigger.getJobDataMap();
                jobList.add(jobDetails);
            }
        }
        return jobList;
    }
    public JobDetails jobInfo(String jobName, String jobGroupName) {
        JobDetails jobDetails = new JobDetails();
        try {
            JobKey jobKey = JobKey.jobKey(jobName, jobGroupName);
            List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
            for (Trigger trigger : triggers) {
                if (trigger instanceof CronTrigger) {
                    CronTrigger cronTrigger = (CronTrigger) trigger;
                    jobDetails.setCronExpression(cronTrigger.getCronExpression();
                    jobDetails.setTimeZone(cronTrigger.getTimeZone().getDisplayName();
                }
                jobDetails.setTriggerGroupName(trigger.getKey().getName();
                jobDetails.setTriggerName(trigger.getKey().getGroup();
                jobDetails.setJobGroupName(jobKey.getGroup();
                jobDetails.setJobName(jobKey.getName();
                jobDetails.setStartTime(trigger.getStartTime();
                jobDetails.setJobClassName(scheduler.getJobDetail(jobKey).getJobClass().getName();
                jobDetails.setNextFireTime(trigger.getNextFireTime();
                jobDetails.setPreviousFireTime(trigger.getPreviousFireTime();
                jobDetails.setStatus(scheduler.getTriggerState(trigger.getKey().name();
                jobDetails.setJobDataMap(trigger.getJobDataMap();
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return jobDetails;
    }
    /**
     * 获取所有计划中的任务列表
     *
     * @return
     */
    public List<Map<String, Object>> queryAllJob() {
        List<Map<String, Object>> jobList = null;
        try {
            GroupMatcher<JobKey> matcher = GroupMatcher.anyJobGroup();
            Set<JobKey> jobKeys = scheduler.getJobKeys(matcher);
            jobList = new ArrayList<>();
            for (JobKey jobKey : jobKeys) {
                List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
                for (Trigger trigger : triggers) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("jobName", jobKey.getName();
                    map.put("jobGroupName", jobKey.getGroup();
                    map.put("description", "trigger:" + trigger.getKey();
                    Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey();
                    map.put("jobStatus", triggerState.name();
                    if (trigger instanceof CronTrigger) {
                        CronTrigger cronTrigger = (CronTrigger) trigger;
                        String cronExpression = cronTrigger.getCronExpression();
                        map.put("jobTime", cronExpression);
                    }
                    jobList.add(map);
                }
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return jobList;
    }
    /**
     * 获取所有正在运行的job
     *
     * @return
     */
    public List<Map<String, Object>> queryRunJon() {
        List<Map<String, Object>> jobList = null;
        try {
            List<JobExecutionContext> executingJobs = scheduler.getCurrentlyExecutingJobs();
            jobList = new ArrayList<>(executingJobs.size();
            for (JobExecutionContext executingJob : executingJobs) {
                Map<String, Object> map = new HashMap<>();
                JobDetail jobDetail = executingJob.getJobDetail();
                JobKey jobKey = jobDetail.getKey();
                Trigger trigger = executingJob.getTrigger();
                map.put("jobName", jobKey.getName();
                map.put("jobGroupName", jobKey.getGroup();
                map.put("description", "trigger:" + trigger.getKey();
                Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey();
                map.put("jobStatus", triggerState.name();
                if (trigger instanceof CronTrigger) {
                    CronTrigger cronTrigger = (CronTrigger) trigger;
                    String cronExpression = cronTrigger.getCronExpression();
                    map.put("jobTime", cronExpression);
                }
                jobList.add(map);
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return jobList;
    }
}
}
}
