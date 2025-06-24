package com.hu.oneclick.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * sys_user_order_record
 * @author 
 */
@Schemavalue="com.hu.oneclick.model.domain.SysUserOrderRecord订单记录表"
@Data
public class SysUserOrderRecord implements Serializable {
    /**
     * id
     */
    @Schemavalue="id"
    private Integer id;

    /**
     * 订单id
     */
    @Schemavalue="订单id"
    private Long order_id;

    /**
     * 原价
     */
    @Schemavalue="原价"
    private BigDecimal original_price;

    /**
     * 折扣价
     */
    @Schemavalue="折扣价"
    private BigDecimal discount_price;

    /**
     * 支付状态
     */
    @Schemavalue="支付状态"
    private Boolean status;

    /**
     * 创建时间
     */
    @Schemavalue="创建时间"
    private Date create_time;

    /**
     * 是否删除
     */
    @Schemavalue="是否删除"
    private Boolean is_del;

    /**
     * 支付时间
     */
    @Schemavalue="支付时间"
    private Date payment_time;

    /**
     * 支付方式
     */
    @Schemavalue="支付方式"
    private String payment_type;

    /**
     * 服务周期
     */
    @Schemavalue="服务周期"
    private String service_plan_duration;

    /**
     * 容量大小
     */
    @Schemavalue="容量大小"
    private String data_strorage;

    /**
     * 容量价格
     */
    @Schemavalue="容量价格"
    private BigDecimal data_price;

    /**
     * apiCall
     */
    @Schemavalue="apiCall"
    private String api_call;

    /**
     * apiCall价格
     */
    @Schemavalue="apiCall价格"
    private BigDecimal api_call_price;

    /**
     * 采购模式
     */
    @Schemavalue="采购模式"
    private String sub_scription;

    /**
     * 折扣
     */
    @Schemavalue="折扣"
    private BigDecimal discount;

    /**
     * 实际支付
     */
    @Schemavalue="实际支付"
    private BigDecimal expenditure;

    /**
     * 发票转态
     */
    @Schemavalue="发票转态"
    private Boolean invoice;

    private static final long serialVersionUID = 1L;
}
