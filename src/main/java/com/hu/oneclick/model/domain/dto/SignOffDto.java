package com.hu.oneclick.model.domain.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author 王富贵
 * @version 1.0.0 2021/9/17
 * @since JDK 1.8.0
 */
public class SignOffDto {
    private String projectId;
    private String env;
    private String version;
    private String testCycleVersion;
    private List<String> issue;
    private MultipartFile file;

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

    public List<String> getIssue() {
        return issue;
    }

    public void setIssue(List<String> issue) {
        this.issue = issue;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
