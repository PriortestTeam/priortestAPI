package com.hu.oneclick.model.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * sys_user_order_record
 * @author 
 */
@ApiModel(value="com.hu.oneclick.model.domain.SysUserOrderRecord订单记录表")
@Data
public class SysUserOrderRecord implements Serializable {
    /**
     * id
     */
    @ApiModelProperty(value="id")
    private Integer id;

    /**
     * 订单id
     */
    @ApiModelProperty(value="订单id")
    private Long order_id;

    /**
     * 原价
     */
    @ApiModelProperty(value="原价")
    private BigDecimal original_price;

    /**
     * 折扣价
     */
    @ApiModelProperty(value="折扣价")
    private BigDecimal discount_price;

    /**
     * 支付状态
     */
    @ApiModelProperty(value="支付状态")
    private Boolean status;

    /**
     * 创建时间
     */
    @ApiModelProperty(value="创建时间")
    private Date create_time;

    /**
     * 是否删除
     */
    @ApiModelProperty(value="是否删除")
    private Boolean is_del;

    /**
     * 支付时间
     */
    @ApiModelProperty(value="支付时间")
    private Date payment_time;

    /**
     * 支付方式
     */
    @ApiModelProperty(value="支付方式")
    private String payment_type;

    /**
     * 服务周期
     */
    @ApiModelProperty(value="服务周期")
    private String service_plan_duration;

    /**
     * 容量大小
     */
    @ApiModelProperty(value="容量大小")
    private String data_strorage;

    /**
     * 容量价格
     */
    @ApiModelProperty(value="容量价格")
    private BigDecimal data_price;

    /**
     * apiCall
     */
    @ApiModelProperty(value="apiCall")
    private String api_call;

    /**
     * apiCall价格
     */
    @ApiModelProperty(value="apiCall价格")
    private BigDecimal api_call_price;

    /**
     * 采购模式
     */
    @ApiModelProperty(value="采购模式")
    private String sub_scription;

    /**
     * 折扣
     */
    @ApiModelProperty(value="折扣")
    private BigDecimal discount;

    /**
     * 实际支付
     */
    @ApiModelProperty(value="实际支付")
    private BigDecimal expenditure;

    /**
     * 发票转态
     */
    @ApiModelProperty(value="发票转态")
    private Boolean invoice;

    private static final long serialVersionUID = 1L;
}
