package com.hu.oneclick.quartz.task;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.hu.oneclick.quartz.JenkinsManager;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.JobWithDetails;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import jakarta.annotation.Resource;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

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
    private JenkinsServer jenkinsServer = null;
    private String jobName = "";


    @Override
    protected void executeInternal(JobExecutionContext context) {
        try {
            // 获取参数列表
            JobDataMap jobDataMap = context.getMergedJobDataMap();
            String jenkinsJobName = jobDataMap.getString("jenkinsJobName");
//            Map&lt;String, Object> jenkinsParams = (Map&lt;String, Object>) jobDataMap.get("jenkinsParams");
            // 初始化 Jenkins 服务
            initJenkinsServer(jenkinsJobName);
            // 当前时间
            DateTime curDate = DateUtil.date();
            JobWithDetails job = jenkinsServer.getJob(jobName);
            log.info("Job详情:");
            log.info(JSONUtil.toJsonStr(job);
            // 构建任务 (priortestapi)
            job.build(true);
            log.info(StrUtil.format("【{}】Job执行成功, 时间:{}", jenkinsJobName, curDate);
        } catch (IOException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void initJenkinsServer(String jenkinsJobName) throws URISyntaxException {
        log.info("开始初始化 Jenkins 服务");
        // 获取URL
        log.info("获取到的URL:{}", jenkinsJobName);
        String substring = jenkinsJobName.substring(jenkinsJobName.indexOf("//") + 2);
        String user_token = substring.substring(0, substring.indexOf("@");
        String ip = substring.substring(substring.indexOf("@") + 1);
        String ip_port = ip.substring(0, ip.indexOf("/");
        String job = ip.substring(ip.indexOf("job/") + 4, ip.indexOf("/build");
        String user = user_token.substring(0, user_token.indexOf(":");
        String token = user_token.substring(user_token.indexOf(":") + 1);
        jobName = job;
        jenkinsServer = new JenkinsServer(new URI(StrUtil.format("http://{}", ip_port), user, token);
    }

}
}
