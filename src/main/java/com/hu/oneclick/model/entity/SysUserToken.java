package com.hu.oneclick.model.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * sys_user_token
 * @author 
 */
@ApiModel(value="com.hu.oneclick.model.entity.SysUserToken")
@Data
public class SysUserToken implements Serializable {
    private Integer id;

    /**
     * 用户id
     */
    @ApiModelProperty(value="用户id")
    private String userId;

    /**
     * token名称
     */
    @ApiModelProperty(value="token名称")
    private String tokenName;

    /**
     * token值
     */
    @ApiModelProperty(value="token值")
    private String tokenValue;

    /**
     * 过期时间
     */
    @ApiModelProperty(value="过期时间")
    private Date expirationTime;

    /**
     * 创建时间
     */
    @ApiModelProperty(value="创建时间")
    private Date createTime;

    /**
     * 是否删除
     */
    @ApiModelProperty(value="是否删除")
    private Boolean isDel;

    /**
     * 状态
     */
    @ApiModelProperty(value="状态")
    private Boolean status;

    /**
     * 剩余调用api次数
     */
    @ApiModelProperty(value="剩余调用api次数")
    private Long apiTimes;

    /**
     * 创建人
     */
    @ApiModelProperty(value="创建人")
    private String createId;

    private static final long serialVersionUID = 1L;
}
