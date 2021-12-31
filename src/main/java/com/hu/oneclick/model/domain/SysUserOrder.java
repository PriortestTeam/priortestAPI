package com.hu.oneclick.model.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * sys_user_order
 * @author 
 */
@Data
public class SysUserOrder implements Serializable {
    /**
     * 订单表id
     */
    @ApiModelProperty(value="订单表id")
    private Integer id;

    /**
     * 用户id
     */
    @ApiModelProperty(value="用户id")
    private Long userId;

    /**
     * 业务id
     */
    @ApiModelProperty(value="业务id")
    private Long orderId;

    /**
     * 支付方式
     */
    @ApiModelProperty(value="支付方式")
    private String paymentType;

    /**
     * 支付账号
     */
    @ApiModelProperty(value="支付账号")
    private String payCard;

    /**
     * 支付姓名
     */
    @ApiModelProperty(value="支付姓名")
    private String payName;

    /**
     * 服务周期
     */
    @ApiModelProperty(value="服务周期")
    private Integer serviceDuration;

    /**
     * 容量大小
     */
    @ApiModelProperty(value="容量大小")
    private Integer dataStrorage;

    /**
     * 订阅时长
     */
    @ApiModelProperty(value="订阅时长")
    private String subScription;

    /**
     * apiCall
     */
    @ApiModelProperty(value="apiCall")
    private String apiCall;

    /**
     * 原价
     */
    @ApiModelProperty(value="原价")
    private BigDecimal originalPrice;
    /**
     * 现价
     */
    @ApiModelProperty(value="现价")
    private BigDecimal currentPrice;

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

    /**
     * 状态0未支付1已支付
     */
    @ApiModelProperty(value="状态0未支付1已支付")
    private Boolean status;

    /**
     * 逻辑删除0未1已
     */
    @ApiModelProperty(value="逻辑删除0未1已")
    private Boolean isDel;


    private static final long serialVersionUID = 14587454654354644L;
}