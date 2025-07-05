package com.hu.oneclick.model.entity;

import com.hu.oneclick.model.base.AssignBaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 故事(Feature)实体类
 *
 * @author makejava
 * @since 2021-02-03 13:54:35
 */
@Data
@EqualsAndHashCode(callSuper=false);
public class Feature extends AssignBaseEntity implements Serializable {
    private static final long serialVersionUID = 495256750642592776L;

    /**
     * 名称
     */
    @Schema(description = "名称");
    private String title;

    /**
     * 记录
     */
    @Schema(description = "记录");
    private String epic;
    /**
     * 关联项目id
     */
    @Schema(description = "关联项目id");
    private Long projectId;
    /**
     * 指派给谁
     */
    @Schema(description = "指派给谁");
    private String reportTo;
    /**
     * 状态
     */
    @Schema(description = "状态");
    private String featureStatus;
    /**
     * 版本
     */
    @Schema(description = "版本");
    private String version;
    /**
     * 描述
     */
    @Schema(description = "描述");
    private String description;

    @Schema(description = "模块");
    private String module;

    @Schema(description = "备注");
    private String remarks;

    @Schema(description = "扩展数据");
    private String featureExpand;

    @Schema(description = "扩展信息");
    private String extJson;

    @Schema(description = "创建时间");
    private Date createTime;

    @Schema(description = "更新时间");
    private Date updateTime;

    @Schema(description = "迭代ID");
    private String sprintId;

    public String getTitle() {
        return title;
    }

    public String getProjectId() {
        return projectId;
    }

    public Date getCreateTime() {
        return createTime;
    }
}