package com.hu.oneclick.model.domain.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author 王富贵
 * @version 1.0.0 2021/9/17
 * @since JDK 1.8.0
 */
public class SignOffDto {
    /**
     *项目
     */
    private String projectId;
    /**
     *测试环境
     */
    private String env;
    /**
     * 发布版本
     */
    private String version;
    /**
     * 测试周期
     */
    private String testCycleVersion;
    /**
     * 缺陷
     */
    private String issue;
    /**
     * 签名
     */
    private String fileUrl;

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTestCycleVersion() {
        return testCycleVersion;
    }

    public void setTestCycleVersion(String testCycleVersion) {
        this.testCycleVersion = testCycleVersion;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}
