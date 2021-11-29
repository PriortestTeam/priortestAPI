package com.hu.oneclick.model.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * sys_order_discount
 * @author masiyi
 */
@ApiModel(value="折扣表")
@Data
public class SysOrderDiscountDto implements Serializable {

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


    private static final long serialVersionUID = 115194643216464464L;
}