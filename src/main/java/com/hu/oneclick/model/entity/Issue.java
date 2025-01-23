package com.hu.oneclick.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.hu.oneclick.config.ListTypeHandler;
import com.hu.oneclick.model.base.AssignBaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 缺陷(Issue)实体类
 *
 * @author makejava
 * @since 2021-02-17 16:20:43
 */
@TableName(autoResultMap = true)
@Data
public class Issue extends AssignBaseEntity implements Serializable {
    private static final long serialVersionUID = 418948698502600149L;

    /**
     * 关联项目id
     */
    @ApiModelProperty("关联项目id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long projectId;

    /**
     * 名称
     */
    @ApiModelProperty("名称")
    private String title;

    /**
     * 计划修复时间
     */
    @ApiModelProperty("计划修复时间")
    private Date planFixDate;

    @ApiModelProperty("关闭时间")
    private Date closeDate;

    @ApiModelProperty("关联测试用例")
    private String verifiedResult;

    @ApiModelProperty("优先级")
    private String priority;

    @ApiModelProperty("环境")
    private String env;

    @ApiModelProperty("浏览器")
    private String browser;

    @ApiModelProperty("平台")
    private String platform;

    @ApiModelProperty("版本")
    private String issueVersion;

    @ApiModelProperty("用例分类")
    private String caseCategory;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("状态")
    private String issueStatus;

    @ApiModelProperty("模块")
    private String module;

    @ApiModelProperty("当前负责人")
    private String reportTo;

    @ApiModelProperty("issue_expand")
    private String issueExpand;

    @ApiModelProperty("缺陷修改版本号")
    private String fixVersion;

    @ApiModelProperty("severity")
    private String severity;

    @ApiModelProperty("测试设备")
    private String testDevice;

    @ApiModelProperty("运行用例Id")
    private long runcaseId;



    /**
     * 修复分类，表示该问题属于哪种类别的修复措施
     */
    @ApiModelProperty("修复分类")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField(typeHandler = ListTypeHandler.class)
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
    @TableField(typeHandler = ListTypeHandler.class)
    private List<String> rootcauseCategory;

    /**
     * 期间
     */
    @ApiModelProperty("期间")
    private String duration;

}
