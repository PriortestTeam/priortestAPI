package com.hu.oneclick.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.hu.oneclick.model.base.AssignBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel("测试周期")
@TableName("test_cycle")
public class TestCycle extends AssignBaseEntity implements Serializable {

    private static final long serialVersionUID = -5508923063848235392L;

    /**
     * 项目id
     */
    @ApiModelProperty("项目id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long projectId;
    /**
     * 名称
     */
    @ApiModelProperty("名称")
    private String title;
    /**
     * 执行进度 0: 未开始; 1:未完成; 2: 完成
     */
    @ApiModelProperty("执行进度 0: 未开始; 1:未完成; 2: 完成")
    private Integer exeucteProgress;
    /**
     * 运行状态
     */
    @ApiModelProperty("运行状态")
    private Integer runStatus;
    /**
     * 最后一次运行时间
     */
    @ApiModelProperty("最后一次运行时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastRunDate;
    /**
     * 版本
     */
    @ApiModelProperty("版本")
    private String version;
    /**
     * 计划执行时间
     */
    @ApiModelProperty("计划执行时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date planExecuteDate;
    /**
     * 描述
     */
    @ApiModelProperty("描述")
    private String description;
    /**
     * 0 默认 1 选中
     */
    @ApiModelProperty("0 默认 1 选中")
    private Integer currentRelease;
    /**
     * 0 未选择 1选中 (当 currentVersion 选中， 此值一定选择。)
     */
    @ApiModelProperty("0 未选择 1选中 (当 currentVersion 选中， 此值一定选择。)")
    private Integer released;
    /**
     * 用例执行人
     */
    @ApiModelProperty("用例执行人")
    private String reportTo;
    /**
     * 关注者
     */
    @ApiModelProperty("测试方法")
    private String testMethod;
    /**
     * 平台
     */
    @ApiModelProperty("平台")
    private String testPlatform;
    /**
     * 环境
     */
    @ApiModelProperty("环境")
    private String env;


    @ApiModelProperty("allure 报告")
    private String allureReportUrl;
    @ApiModelProperty("instance_count")
    private Integer instanceCount;
    @ApiModelProperty("not_run_count")
    private Integer notRunCount;
    @ApiModelProperty("状态，草稿，待执行")
    private String testCycleStatus;
    @ApiModelProperty("计划运行job url")
    private String autoJobLink;
    @ApiModelProperty("auto_job_start")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date autoJobStart;
    @ApiModelProperty("auto_job_end")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date autoJobEnd;
    @ApiModelProperty("frequency")
    private String frequency;
    @ApiModelProperty("remarks")
    private String remarks;

    @ApiModelProperty("auto_job_run_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date autoJobRunTime;
    @ApiModelProperty("testcycle_expand")
    private String testcycleExpand;

    @ApiModelProperty("browser")
    private String browser;

    @ApiModelProperty("test_frame")
    private String testFrame;


}
