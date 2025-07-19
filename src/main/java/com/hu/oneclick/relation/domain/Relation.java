package com.hu.oneclick.relation.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.hu.oneclick.model.base.AssignBaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 关系表
 *
 * @author xiaohai
 * @date 2023/06/05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "关系表")
public class Relation extends AssignBaseEntity {

    /** 对象id */
    @Schema(description = "对象id")
    private String objectId;

    /** 目标id */
    @Schema(description = "目标id")
    private String targetId;

    /** 分类 */
    @Schema(description = "分类")
    private String category;

    /** 扩展json */
    @Schema(description = "扩展json")
    private String extJson;

    /** 标题（用于查询时关联显示） */
    @Schema(description = "标题")
    @TableField(exist = false)
    private String title;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /** 创建用户ID */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建用户ID")
    private Long createUserId;

    /** 重写父类的updateUserId字段，标记为非数据库字段 */
    @Override
    @TableField(exist = false)
    public Long getUpdateUserId() {
        return super.getUpdateUserId();
    }

    /** 重写父类的updateTime字段，标记为非数据库字段 */
    @Override
    @TableField(exist = false)
    public Date getUpdateTime() {
        return super.getUpdateTime();
    }
}