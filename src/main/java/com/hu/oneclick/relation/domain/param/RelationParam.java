package com.hu.oneclick.relation.domain.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel("关系查询参数")
public class RelationParam {

    /** 对象id */
    @ApiModelProperty("对象id")
    private String objectId;

    /** 目标id */
    @ApiModelProperty("目标id")
    private String targetId;

    /** 分类 */
    @ApiModelProperty(value = "分类", notes = "com.hu.oneclick.relation.enums.RelationCategoryEnum")
    private String category;

}
