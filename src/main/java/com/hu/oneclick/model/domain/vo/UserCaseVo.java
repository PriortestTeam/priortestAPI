package com.hu.oneclick.model.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


/**
 * 用户故事返回对象
 */
@Data
@ApiModel(value = "用户故事返回对象")
public class UserCaseVo implements Serializable {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "类别")
    private String useCategory;
    /**
     * 级别
     */
    @ApiModelProperty(value = "级别")
    private Integer level;
    /**
     * 等级
     */
    @ApiModelProperty(value = "等级")
    private Integer grade;

    /**
     * 流程场景、
     */
    @ApiModelProperty(value = "流程场景")
    private String scenario;
    @ApiModelProperty(value = "故事用例扩展")
    private String useaseExpand;
    @ApiModelProperty(value = "所属故事ID")
    private String featureId;

    @ApiModelProperty(value = "备注")
    private String remarks;
}
