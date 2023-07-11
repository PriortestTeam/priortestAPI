package com.hu.oneclick.quartz.task;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.hu.oneclick.quartz.JenkinsManager;
import com.offbytwo.jenkins.model.JobWithDetails;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import javax.annotation.Resource;
import java.io.IOException;

/**
 * 构建 Jenkins Job
 *
 * @author xiaohai
 * @date 2023/07/11
 */
@Slf4j
public class BuildJobTask extends QuartzJobBean {

    @Resource
    private JenkinsManager jenkinsManager;


    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            // 当前时间
            DateTime curDate = DateUtil.date();
            JobWithDetails job = jenkinsManager.getJob("priortestapi");
            log.info("Job详情:");
            log.info(JSONUtil.toJsonStr(job));
            jenkinsManager.buildJob("priortestapi");
            log.info("【priortestapi】Job执行成功, 时间:" + curDate);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

}
