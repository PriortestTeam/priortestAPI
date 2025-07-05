package com.hu.oneclick.model.domain.dto;


import lombok.Data;
import org.springframework.stereotype.Component;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

// Adding missing setter methods to TestCaseDataCaseDto and adding swagger schema annotations.
@Data
@Schema(description = "测试用例数据DTO")
public class TestCaseDataCaseDto {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "项目ID")
    private Long projectId;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "优先级")
    private String priority;

    @Schema(description = "特性")
    private String feature;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "执行时间")
    private java.util.Date executeTime;

    @Schema(description = "浏览器")
    private String browser;

    @Schema(description = "平台")
    private String platform;

    @Schema(description = "版本")
    private String version;

    @Schema(description = "用例分类")
    private String caseCategory;

    @Schema(description = "测试类型")
    private String testType;

    @Schema(description = "测试条件")
    private String testCondition;

    @Schema(description = "环境")
    private String env;

    @Schema(description = "外部链接ID")
    private String externalLinkId;

    @Schema(description = "最后运行状态")
    private Integer lastRunStatus;

    @Schema(description = "模块")
    private String module;

    @Schema(description = "测试设备")
    private String testDevice;

    @Schema(description = "测试数据")
    private String testData;

    @Schema(description = "测试方法")
    private String testMethod;

    @Schema(description = "测试状态")
    private String testStatus;

    @Schema(description = "报告给")
    private String reportTo;

    // 手动添加所有缺失的setter方法
    public void setId(Long id) {
        this.id = id;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setExecuteTime(java.util.Date executeTime) {
        this.executeTime = executeTime;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setCaseCategory(String caseCategory) {
        this.caseCategory = caseCategory;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }

    public void setTestCondition(String testCondition) {
        this.testCondition = testCondition;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public void setExternalLinkId(String externalLinkId) {
        this.externalLinkId = externalLinkId;
    }

    public void setLastRunStatus(Integer lastRunStatus) {
        this.lastRunStatus = lastRunStatus;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public void setTestDevice(String testDevice) {
        this.testDevice = testDevice;
    }

    public void setTestData(String testData) {
        this.testData = testData;
    }

    public void setTestMethod(String testMethod) {
        this.testMethod = testMethod;
    }

    public void setTestStatus(String testStatus) {
        this.testStatus = testStatus;
    }

    public void setReportTo(String reportTo) {
        this.reportTo = reportTo;
    }
}