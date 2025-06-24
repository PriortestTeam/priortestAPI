package com.hu.oneclick.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schemavalue="订单表id"
    private Integer id;

    /**
     * 用户id
     */
    @Schemavalue="用户id"
    private String userId;

    /**
     * 业务id
     */
    @Schemavalue="业务id"
    private Long orderId;

    /**
     * 支付方式
     */
    @Schemavalue="支付方式"
    private String paymentType;

    /**
     * 支付账号
     */
    @Schemavalue="支付账号"
    private String payCard;

    /**
     * 支付姓名
     */
    @Schemavalue="支付姓名"
    private String payName;

    /**
     * 服务周期-一共订阅多久
     */
    @Schemavalue="服务周期"
    private String serviceDuration;

    /**
     * 容量大小
     */
    @Schemavalue="容量大小"
    private String dataStrorage;

    /**
     * 订阅时长-我是怎么付款，月付，季付
     */
    @Schemavalue="订阅时长"
    private String subScription;

    /**
     * apiCall
     */
    @Schemavalue="apiCall"
    private String apiCall;

    /**
     * 原价
     */
    @Schemavalue="原价"
    private BigDecimal originalPrice;
    /**
     * 现价
     */
    @Schemavalue="现价"
    private BigDecimal currentPrice;

    /**
     * 创建时间
     */
    @Schemavalue="创建时间"
    private Date createTime;

    /**
     * 修改时间
     */
    @Schemavalue="修改时间"
    private Date updateTime;

    /**
     * 状态0未支付1已支付
     */
    @Schemavalue="状态0未支付1已支付"
    private Boolean status;

    /**
     * 逻辑删除0未1已
     */
    @Schemavalue="逻辑删除0未1已"
    private Boolean isDel;


    private static final long serialVersionUID = 14587454654354644L;
}
