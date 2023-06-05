package com.hu.oneclick.relation.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

/**
 * 关系实体
 *
 * @author xiaohai
 * @date 2023/06/05
 */
@Getter
@Setter
@TableName("relation")
public class Relation {

    /** id */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 对象id */
    private String objectId;

    /** 目标id */
    private String targetId;

    /** 分类 */
    private String category;

    /** 扩展信息 */
    private String extJson;

}
