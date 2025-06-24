package com.hu.oneclick.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * sys_order_discount
 * @author 
 */
@Schemavalue="com.hu.oneclick.model.domain.SysOrderDiscount折扣表"
@Data
public class SysOrderDiscount implements Serializable {
    /**
     * 折扣表id
     */
    @Schemavalue="折扣表id"
    private Integer id;

    /**
     * 订阅时长
     */
    @Schemavalue="订阅时长"
    private String subScription;

    /**
     * 容量大小
     */
    @Schemavalue="容量大小"
    private String dataStrorage;

    /**
     * apiCall
     */
    @Schemavalue="apiCall"
    private String apiCall;

    /**
     * 初始折扣
     */
    @Schemavalue="初始折扣"
    private BigDecimal normalDiscount;

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

    private static final long serialVersionUID = 1L;
}
