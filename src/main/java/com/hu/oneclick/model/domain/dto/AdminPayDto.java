package com.hu.oneclick.model.domain.dto;

import lombok.Data;

/**
 * @author MaSiyi
 * @version 1.0.0 2021/10/9
 * @since JDK 1.8.0
 */
@Data
public class AdminPayDto {
    /**
     * 支付方式
     */
    private Integer paymentType;
    /**
     * 支付账号
     */
    private String payCard;
    /**
     * 支付姓名
     */
    private String payName;
    /**
     * 容量大小
     */
    private Integer dataStrorage;
    /**
     * 订阅时间
     */
    private Integer subScription;

}
