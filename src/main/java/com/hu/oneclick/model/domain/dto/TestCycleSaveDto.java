package com.hu.oneclick.model.domain.dto;

import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 测试周期DTO
 *
 * @author xiaohai
 * @date 2023/05/12
 */
@Setter
@Getter
@ApiModel("测试周期DTO")
public class TestCycleSaveDto implements Serializable {

    private static final long serialVersionUID = 3621834190197699211L;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "主键id")
    private Long id;

    /**
     * 项目id
     */
    @ApiModelProperty("项目id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @NotNull(message = "项目ID不能为空")
    private Long projectId;
    /**
     * 名称
     */
    @ApiModelProperty("名称")
    @NotBlank(message = "名称不能为空")
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
    /**
     * allure 报告
     */
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
    /**
     * auto_job_start
     */
    @ApiModelProperty("auto_job_start")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date autoJobStart;
    /**
     * auto_job_end
     */
    @ApiModelProperty("auto_job_end")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date autoJobEnd;
    @ApiModelProperty("frequency")
    private String frequency;
    @ApiModelProperty("remarks")
    private String remarks;
    /**
     * auto_job_run_time
     */
    @ApiModelProperty("auto_job_run_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date autoJobRunTime;
    @ApiModelProperty("browser")
    private String browser;
    @ApiModelProperty("test_frame")
    private String testFrame;
    @ApiModelProperty("testcycle_expand")
    private String testcycleExpand;
    /**
     * 自定义字段值
     */
    @ApiModelProperty("自定义字段值")
    private JSONObject customFieldDatas;

}
