package com.hu.oneclick.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hu.oneclick.model.base.AssignBaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 缺陷(Issue)实体类
 *
 * @author makejava
 * @since 2021-02-17 16:20:43
 */
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

}
