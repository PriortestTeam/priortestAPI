package com.hu.oneclick.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hu.oneclick.model.base.AssignBaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 测试周期
 *
 * @author xiaohai
 * @date 2023/05/12
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "测试周期")
@TableName("test_cycle")
public class TestCycle extends AssignBaseEntity implements Serializable {

    private static final long serialVersionUID = -5508923063848235392L;

    /**
     * 项目id
     */
    @Schema(description = "项目id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long projectId;
    /**
     * 名称
     */
    @Schema(description = "名称")
    private String title;
    /**
     * 执行进度 0: 未开始; 1:未完成; 2: 完成
     */
    @Schema(description = "执行进度 0: 未开始; 1:未完成; 2: 完成")
    private Integer exeucteProgress;
    /**
     * 运行状态
     */
    @Schema(description = "运行状态")
    private Integer runStatus;
    /**
     * 最后一次运行时间
     */
    @Schema(description = "最后一次运行时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastRunDate;
    /**
     * 版本
     */
    @Schema(description = "版本")
    private String version;
    /**
     * 计划执行时间
     */
    @Schema(description = "计划执行时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date planExecuteDate;
    /**
     * 描述
     */
    @Schema(description = "描述")
    private String description;

    @Schema(description = "当前版本")
    @JsonIgnore
    private Integer currentRelease;

    @Schema(description = "是否发布")
    @JsonIgnore
    private Integer released;

    // 为前端提供字符串格式的getter方法
    @JsonProperty("currentRelease")
    public String getCurrentReleaseStr() {
        return currentRelease != null ? currentRelease.toString() : "0";
    }

    @JsonProperty("released")
    public String getReleasedStr() {
        return released != null ? released.toString() : "0";
    }

    // 为前端提供字符串格式的setter方法
    public void setCurrentReleaseStr(String currentReleaseStr) {
        this.currentRelease = (currentReleaseStr != null && !currentReleaseStr.isEmpty()) ? Integer.parseInt(currentReleaseStr) : 0;
    }

    public void setReleasedStr(String releasedStr) {
        this.released = (releasedStr != null && !releasedStr.isEmpty()) ? Integer.parseInt(releasedStr) : 0;
    }
    /**
     * 用例执行人
     */
    @Schema(description = "用例执行人")
    private String reportTo;
    /**
     * 关注者
     */
    @Schema(description = "测试方法")
    private String testMethod;
    /**
     * 平台
     */
    @Schema(description = "平台")
    private String testPlatform;
    /**
     * 环境
     */
    @Schema(description = "环境")
    private String env;


    @Schema(description = "allure 报告")
    private String allureReportUrl;
    @Schema(description = "instance_count")
    private Integer instanceCount;
    @Schema(description = "not_run_count")
    private Integer notRunCount;
    @Schema(description = "状态，草稿，待执行")
    private String testCycleStatus;
    @Schema(description = "计划运行job url")
    private String autoJobLink;
    @Schema(description = "auto_job_start")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date autoJobStart;
    @Schema(description = "auto_job_end")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date autoJobEnd;
    @Schema(description = "frequency")
    private String frequency;
    @Schema(description = "remarks")
    private String remarks;

    @Schema(description = "auto_job_run_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date autoJobRunTime;
    @Schema(description = "testcycle_expand")
    private String testcycleExpand;

    @Schema(description = "browser")
    private String browser;

    @Schema(description = "test_frame")
    private String testFrame;


}