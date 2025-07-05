package com.hu.oneclick.model.entity;

import com.hu.oneclick.model.base.AssignBaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 故事(Feature)实体类
 *
 * @author makejava
 * @since 2021-02-03 13:54:35
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class Feature extends AssignBaseEntity implements Serializable {
    private static final long serialVersionUID = 495256750642592776L;

    /**
     * 名称
     */
    @Schema(description = "名称"))
    private String title;

    /**
     * 记录
     */
    @Schema(description = "记录"))
    private String epic;
    /**
     * 关联项目id
     */
    @Schema(description = "关联项目id"))
    private Long projectId;
    /**
     * 指派给谁
     */
    @Schema(description = "指派给谁"))
    private String reportTo;
    /**
     * 状态
     */
    @Schema(description = "状态"))
    private String featureStatus;
    /**
     * 版本
     */
    @Schema(description = "版本"))
    private String version;
    /**
     * 描述
     */
    @Schema(description = "描述"))
    private String description;

    @Schema(description = "模块"))
    private String module;

    @Schema(description = "备注"))
    private String remarks;

    @Schema(description = "扩展数据"))
    private String featureExpand;
}
