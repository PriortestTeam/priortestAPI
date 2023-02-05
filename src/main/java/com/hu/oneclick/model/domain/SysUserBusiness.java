package com.hu.oneclick.model.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
     * 值
     */
    @ApiModelProperty(value="不可见项")
    private String invisible;

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

    @ApiModelProperty(name= "roleId", value = "角色id")
    private Long  roleId;

    @ApiModelProperty(name= "roleName", value = "角色名称")
    private String roleName;

    @ApiModelProperty(name= "projectId", value = "项目id")
    private Long projectId;

    @ApiModelProperty(name= "projectName", value = "项目名称")
    private String projectName;

    @ApiModelProperty(name= "userId", value = "用户id")
    private Long userId;

    @ApiModelProperty(name= "userName", value = "用户名称")
    private String userName;

    private static final long serialVersionUID = 1L;
}