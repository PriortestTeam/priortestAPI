package com.hu.oneclick.model.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import lombok.Data;

/**
 * sys_user_business
 * @author 
 */
@ApiModel(value="用户角色模块关系表")
@Data
public class SysUserBusiness implements Serializable {
    /**
     * 主键
     */
    @ApiModelProperty(value="主键")
    private Long id;

    /**
     * 类别
     */
    @ApiModelProperty(value="类别")
    private String type;

    /**
     * 主id
     */
    @ApiModelProperty(value="主id")
    private String keyId;

    /**
     * 值
     */
    @ApiModelProperty(value="值")
    private String value;

    /**
     * 按钮权限
     */
    @ApiModelProperty(value="按钮权限")
    private String btnStr;

    /**
     * 租户id
     */
    @ApiModelProperty(value="租户id")
    private Long tenantId;

    /**
     * 删除标记，0未删除，1删除
     */
    @ApiModelProperty(value="删除标记，0未删除，1删除")
    private String deleteFlag;

    private static final long serialVersionUID = 1L;
}