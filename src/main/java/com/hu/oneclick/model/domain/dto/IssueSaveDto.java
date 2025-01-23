package com.hu.oneclick.model.domain.dto;

import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author: jhh
 * @Date: 2023/4/25
 */
@Data
public class IssueSaveDto implements Serializable {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty("关联项目id")
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
     * 计划修复时间
     */
    @ApiModelProperty("计划修复时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date planFixDate;

    @ApiModelProperty("优先级")
//    @NotBlank(message = "优先级不能为空")
    private String priority;

    @ApiModelProperty("环境")
//    @NotBlank(message = "环境不能为空")
    private String env;

    @ApiModelProperty("浏览器")
//    @NotBlank(message = "浏览器不能为空")
    private String browser;

    @ApiModelProperty("平台")
//    @NotBlank(message = "平台不能为空")
    private String platform;

    @ApiModelProperty("版本")
//    @NotBlank(message = "版本不能为空")
    private String issueVersion;

    @ApiModelProperty("用例分类")
//    @NotBlank(message = "用例分类不能为空")
    private String caseCategory;

    @ApiModelProperty("描述")
//    @NotBlank(message = "描述不能为空")
    private String description;

    @ApiModelProperty("状态")
//    @NotBlank(message = "状态不能为空")
    private String issueStatus;

    @ApiModelProperty("模块")
//    @NotBlank(message = "模块不能为空")
    private String module;

    @ApiModelProperty("当前负责人")
//    @NotBlank(message = "当前负责人不能为空")
    private String reportTo;

    @ApiModelProperty("关联测试用例")
//    @NotBlank(message = "关联测试用例不能为空")
    private String verifiedResult;

    @ApiModelProperty("严重程度")
//    @NotBlank(message = "严重程度不能为空")
    private String severity;

    @ApiModelProperty("测试设备")
//    @NotBlank(message = "测试设备不能为空")
    private String testDevice;

    @ApiModelProperty("缺陷修改版本号")
//    @NotBlank(message = "缺陷修改版本号不能为空")
    private String fixVersion;

    /**
     * 自定义字段值
     */
    @ApiModelProperty("自定义字段值")
    private JSONObject customFieldDatas;


    @ApiModelProperty("运行用例Id")
    private long runcaseId;


    /**
     * 修复分类，表示该问题属于哪种类别的修复措施
     */
    @ApiModelProperty("修复分类")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private List<String> fixCategory;

    /**
     * 发生频率，描述问题发生的频率，如每天、每周等
     */
    @ApiModelProperty("发生频率")
    private String frequency;

    /**
     * 问题来源，例如手动报告或自动检测到的问题
     */
    @ApiModelProperty("问题来源")
    private String issueSource;

    /**
     * 用户影响程度，评价问题对用户造成的影响，如轻微、重大等
     */
    @ApiModelProperty("用户影响程度")
    private String userImpact;

    /**
     * 根因分析，记录缺陷发生的原因以及解决方法
     */
    @ApiModelProperty("根因分析")
    private String rootCause;

    /**
     * 根因分类，进一步细分根因的类型
     */
    @ApiModelProperty("根因分类")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private List<String> rootcauseCategory;

    /**
     * 备注信息，提供任何附加信息或注解
     */
    @ApiModelProperty("备注信息")
    private String duration;


}
