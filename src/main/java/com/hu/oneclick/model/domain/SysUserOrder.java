package com.hu.oneclick.model.domain;

import com.hu.oneclick.model.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Table;
import lombok.Data;

/**
 * sys_user_order
 * @author 
 */
@Data
@Table(name="sys_user_order")
@ApiModel(value="com.hu.oneclick.model.domain.SysUserOrder")
public class SysUserOrder extends BaseEntity implements Serializable {

    /**
     * 用户id
     */
    @ApiModelProperty(value="用户id")
    private Long userId;

    /**
     * 支付方式
     */
    @ApiModelProperty(value="支付方式")
    private Integer paymentType;

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

    private static final long serialVersionUID = 14587454654354644L;
}