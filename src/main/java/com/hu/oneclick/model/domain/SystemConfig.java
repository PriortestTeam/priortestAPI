package com.hu.oneclick.model.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * system_config
 * @author 
 */
@ApiModel(value="com.hu.oneclick.model.domain.SystemConfig系统配置表")
@Data
public class SystemConfig implements Serializable {
    /**
     * id
     */
    @ApiModelProperty(value="id")
    private Integer id;

    /**
     * key
     */
    @ApiModelProperty(value="key")
    private String key;

    /**
     * value
     */
    @ApiModelProperty(value="value")
    private String value;

    /**
     * 组别
     */
    @ApiModelProperty(value="组别")
    private String group;

    /**
     * 创建时间
     */
    @ApiModelProperty(value="创建时间")
    private Date create_time;

    /**
     * 修改时间
     */
    @ApiModelProperty(value="修改时间")
    private Date update_time;

    /**
     * ui显示
     */
    @ApiModelProperty(value="ui显示0显示1不显示")
    private Integer uiDisplay;

    /**
     * 分组中文名
     */
    @ApiModelProperty(value="分组中文名")
    private String groupLabelCN;



    private static final long serialVersionUID = 1L;
}