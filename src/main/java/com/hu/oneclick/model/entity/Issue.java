package com.hu.oneclick.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.hu.oneclick.config.ListTypeHandler;
import com.hu.oneclick.model.base.AssignBaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
@EqualsAndHashCode(callSuper=false)
public class Issue extends AssignBaseEntity implements Serializable {
    private static final long serialVersionUID = 418948698502600149L;

    /**
     * 关联项目id
     */
    @Schema(description = "关联项目id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long projectId;

    /**
     * 名称
     */
    @Schema(description = "名称")
    private String title;

    /**
     * 计划修复时间
     */
    @Schema(description = "计划修复时间")
    private Date planFixDate;

    @Schema(description = "关闭时间")
    private Date closeDate;

    @Schema(description = "关联测试用例")
    private String verifiedResult;

    @Schema(description = "优先级")
    private String priority;

    @Schema(description = "环境")
    private String env;

    @Schema(description = "浏览器")
    private String browser;

    @Schema(description = "平台")
    private String platform;

    @Schema(description = "版本")
    private String issueVersion;

    @Schema(description = "用例分类")
    private String caseCategory;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "状态")
    private String issueStatus;

    @Schema(description = "模块")
    private String module;

    @Schema(description = "当前负责人")
    private String reportTo;

    @Schema(description = "issue_expand")
    private String issueExpand;

    @Schema(description = "缺陷修改版本号")
    private String fixVersion;

    @Schema(description = "引入版本")
    private String introducedVersion;

    @Schema(description = "是否为遗留问题")
    private Integer isLegacy;

    @Schema(description = "发布后发现")
    private Integer foundAfterRelease;
    
    // 为前端提供字符串格式的getter方法
    public String getIsLegacyStr() {
        return isLegacy != null ? isLegacy.toString() : "0";
    }
    
    public String getFoundAfterReleaseStr() {
        return foundAfterRelease != null ? foundAfterRelease.toString() : "0";
    }
    
    // 为前端提供字符串格式的setter方法
    public void setIsLegacyStr(String isLegacyStr) {
        this.isLegacy = (isLegacyStr != null && !isLegacyStr.isEmpty()) ? Integer.parseInt(isLegacyStr) : 0;
    }
    
    public void setFoundAfterReleaseStr(String foundAfterReleaseStr) {
        this.foundAfterRelease = (foundAfterReleaseStr != null && !foundAfterReleaseStr.isEmpty()) ? Integer.parseInt(foundAfterReleaseStr) : 0;
    }

    @Schema(description = "severity")
    private String severity;

    @Schema(description = "测试设备")
    private String testDevice;

    @Schema(description = "运行用例Id")
    private long runcaseId;



    /**
     * 修复分类，表示该问题属于哪种类别的修复措施
     */
    @Schema(description = "修复分类")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField(typeHandler = ListTypeHandler.class)
    private List<String> fixCategory;

    /**
     * 发生频率，描述问题发生的频率，如每天、每周等
     */
    @Schema(description = "发生频率")
    private String frequency;

    /**
     * 问题来源，例如手动报告或自动检测到的问题
     */
    @Schema(description = "问题来源")
    private String issueSource;

    /**
     * 用户影响程度，评价问题对用户造成的影响，如轻微、重大等
     */
    @Schema(description = "用户影响程度")
    private String userImpact;

    /**
     * 根因分析，记录缺陷发生的原因以及解决方法
     */
    @Schema(description = "根因分析")
    private String rootCause;

    /**
     * 根因分类，进一步细分根因的类型
     */
    @Schema(description = "根因分类")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField(typeHandler = ListTypeHandler.class)
    private List<String> rootcauseCategory;

    /**
     * 期间
     */
    @Schema(description = "期间")
    private String duration;

}
