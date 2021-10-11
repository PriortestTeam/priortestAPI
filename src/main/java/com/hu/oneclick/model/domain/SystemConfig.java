package com.hu.oneclick.model.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

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
     * 拓展value
     */
    @ApiModelProperty(value="拓展value")
    private String value1;

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

    private static final long serialVersionUID = 1L;
}