package com.hu.oneclick.model.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * sys_order_discount
 * @author 
 */
@ApiModel(value="com.hu.oneclick.model.domain.SysOrderDiscount折扣表")
@Data
public class SysOrderDiscount implements Serializable {
    /**
     * 折扣表id
     */
    @ApiModelProperty(value="折扣表id")
    private Integer id;

    /**
     * 订阅时长
     */
    @ApiModelProperty(value="订阅时长")
    private String subScription;

    /**
     * 容量大小
     */
    @ApiModelProperty(value="容量大小")
    private String dataStrorage;

    /**
     * apiCall
     */
    @ApiModelProperty(value="apiCall")
    private String apiCall;

    /**
     * 初始折扣
     */
    @ApiModelProperty(value="初始折扣")
    private BigDecimal normalDiscount;

    /**
     * 创建时间
     */
    @ApiModelProperty(value="创建时间")
    private Date createTime;

    /**
     * 修改时间
     */
    @ApiModelProperty(value="修改时间")
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}