package com.hu.oneclick.relation.domain;

import com.baomidou.mybatisplus.annotation.TableName;
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
    private String id;

    /** 对象id */
    private String objectId;

    /** 目标id */
    private String targetId;

    /** 分类 */
    private String category;

    /** 扩展信息 */
    private String extJson;

}
