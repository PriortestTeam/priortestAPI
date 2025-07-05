package com.hu.oneclick.quartz;

import cn.hutool.extra.spring.SpringUtil;
import com.hu.oneclick.quartz.config.JenkinsConfig;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.client.JenkinsHttpClient;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.Job;
import com.offbytwo.jenkins.model.JobWithDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

@Component
@Slf4j


public class JenkinsManager {

    private static final JenkinsConfig jenkinsConfig = SpringUtil.getBean(JenkinsConfig.class);

    private JenkinsHttpClient jenkinsHttpClient = null;
    private JenkinsServer jenkinsServer = null;

    @PostConstruct
    public void init() {
        try {
            jenkinsServer = new JenkinsServer(new URI(jenkinsConfig.getUrl(), jenkinsConfig.getUsername(), jenkinsConfig.getPassword();
            jenkinsHttpClient = new JenkinsHttpClient(new URI(jenkinsConfig.getUrl(), jenkinsConfig.getUsername(), jenkinsConfig.getPassword();
        } catch (URISyntaxException e) {
            log.error("jenkins init error", e);
        }
    }

    /**
     * 创建Job
     *
     * @param jobName Job名称
     * @param xml     xml
     * @throws IOException ioexception
     */
    public void createJob(String jobName, String xml) throws IOException {
        jenkinsServer.createJob(jobName, xml, true);
    }

    /**
     * 更新Job
     *
     * @param jobName Job名称
     * @param xml     xml
     * @throws IOException ioexception
     */
    public void updateJob(String jobName, String xml) throws IOException {
        jenkinsServer.updateJob(jobName, xml, true);
    }

    /**
     * 获取 Job 基本信息
     *
     * @param jobName Job名称
     * @throws IOException ioexception
     */
    public JobWithDetails getJob(String jobName) throws IOException {
        return jenkinsServer.getJob(jobName);
    }

    /**
     * 获取 Job 列表
     */
    public Map&lt;String, Job> getJobList() throws IOException {
        return jenkinsServer.getJobs();
    }

    /**
     * 执行无参数 Job
     *
     * @param jobName Job名称
     */
    public void buildJob(String jobName) throws IOException {
        jenkinsServer.getJob(jobName).build(true);
    }

    /**
     * 执行带参数 Job
     *
     * @param jobName Job名称
     */
    public void buildParamJob(String jobName, Map&lt;String, String> param) throws IOException {
        jenkinsServer.getJob(jobName).build(param, true);
    }

    /**
     * 停止 Job
     *
     * @param jobName Job名称
     */
    public void stopJob(String jobName) throws IOException {
        // 获取最后的 build 信息
        Build build = jenkinsServer.getJob(jobName).getLastBuild();
        // 停止最后的 build
        build.Stop(true);
    }

    /**
     * 删除 Job
     *
     * @param jobName Job名称
     */
    public void deleteJob(String jobName) throws IOException {
        jenkinsServer.deleteJob(jobName, true);
    }

}
}
}
