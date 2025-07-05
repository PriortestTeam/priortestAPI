package com.hu.oneclick.relation.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;

/**
 * 关系实体
 *
 * @author xiaohai
 * @date 2023/06/05
 */
@Getter
@Setter
@TableName("relation")
@Schema(description = "关系实体"))
public class Relation {

    /**
     * id
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 对象id
     */
    @Schema(description = "对象id"))
    @NotBlank(message = "对象id不能为空")
    private String objectId;

    /**
     * 目标id
     */
    @Schema(description = "目标id"))
    @NotBlank(message = "目标id不能为空")
    private String targetId;

    /**
     * 分类
     */
    @Schema(description = "分类"))
    @NotBlank(message = "分类不能为空")
    private String category;

    //** 扩展信息 *//*
    @Schema(description = "扩展信息(JSON)))")
    private String extJson;

    /**
     * 标题（来自关联表）
     */
    @Schema(description = "标题"))
    private String title;

}
