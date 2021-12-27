package com.hu.oneclick.model.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * custom_field_data
 * @author 
 */
@ApiModel(value="com.hu.oneclick.model.domain.CustomFieldData")
@Data
public class CustomFieldData implements Serializable {

    private Integer id;

    /**
     * 用户id
     */
    @ApiModelProperty(value="用户id")
    private Long userId;

    /**
     * 项目id
     */
    @ApiModelProperty(value="项目id")
    private Long projectId;

    /**
     * 自定义字段id
     */
    @ApiModelProperty(value="自定义字段id")
    private String customFieldId;

    /**
     * scope对应值的id
     */
    @ApiModelProperty(value="scope对应值的id")
    private String scopeId;

    /**
     * 范围
     */
    @ApiModelProperty(value="范围")
    private String scope;

    /**
     * 自定义存储字段的值
     */
    @ApiModelProperty(value="自定义存储字段的值")
    private String valueData;

    /**
     * 创建时间
     */
    @ApiModelProperty(value="创建时间")
    private Date createTime;

    /**
     * 是否删除
     */
    @ApiModelProperty(value="是否删除")
    private Boolean isDel;

    /**
     * 创建用户id
     */
    @ApiModelProperty(value="创建用户id")
    private Long createUserId;

    private static final long serialVersionUID = 1L;
}