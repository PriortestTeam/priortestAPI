package com.hu.oneclick.model.param;

import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel(value = "用户故事请求对象")
@Data
public class UserCaseParam implements Serializable {


    @ApiModelProperty(value = "id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
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
    private JSONObject usecaseExpand;

    @ApiModelProperty(value = "所属故事ID")
    private long featureId;

    @ApiModelProperty(value = "备注")
    private String remarks;
}
