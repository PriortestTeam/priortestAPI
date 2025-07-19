package com.hu.oneclick.relation.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 关系表实体
 */
@Data
@TableName("relation")
@Schema(description = "关系表")
public class Relation {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "对象ID")
    private Long objectId;

    @Schema(description = "目标ID")
    private Long targetId;

    @Schema(description = "关系分类")
    private String category;

    @Schema(description = "扩展JSON")
    private String extJson;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "创建用户ID")
    private Long createUserId;

    /** 标题字段，用于查询时展示相关标题，不映射到数据库 */
    @TableField(exist = false)
    @Schema(description = "标题")
    private String title;
}