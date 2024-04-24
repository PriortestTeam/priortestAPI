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
    private long id;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "类别")
    private String useCategory;
    /**
     * 级别
     */
    @ApiModelProperty(value = "级别")
    private String level;
    /**
     * 等级
     */
    @ApiModelProperty(value = "等级")
    private String grade;

    /**
     * 流程场景、
     */
    @ApiModelProperty(value = "流程场景")
    private String scenario;
    @ApiModelProperty(value = "故事用例扩展")
    private String usecaseExpand;


    @ApiModelProperty(value = "所属故事ID")
    private long featureId;

    @ApiModelProperty(value = "备注")
    private String remarks;
}
