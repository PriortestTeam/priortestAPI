package com.hu.oneclick.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * sys_user_business
 * @author 
 */
@Schemavalue="用户角色模块关系表"
@Data
public class SysUserBusiness implements Serializable {
    /**
     * 主键
     */
    @Schemavalue="主键"
    private Long id;

    /**
     * 类别
     */
    @Schemavalue="类别"
    private String type;

    /**
     * 主id
     */
    @Schemavalue="主id"
    private String keyId;

    /**
     * 值
     */
    @Schemavalue="值"
    private String value;

    /**
     * 值
     */
    @Schemavalue="不可见项"
    private String invisible;

    /**
     * 按钮权限
     */
    @Schemavalue="按钮权限"
    private String btnStr;

    /**
     * 租户id
     */
    @Schemavalue="租户id"
    private Long tenantId;

    /**
     * 删除标记，0未删除，1删除
     */
    @Schemavalue="删除标记，0未删除，1删除"
    private String deleteFlag;

    @Schemaname= "roleId", value = "角色id"
    private Long  roleId;

    @Schemaname= "roleName", value = "角色名称"
    private String roleName;

    @Schemaname= "projectId", value = "项目id"
    private Long projectId;

    @Schemaname= "projectName", value = "项目名称"
    private String projectName;

    @Schemaname= "userId", value = "用户id"
    private Long userId;

    @Schemaname= "userName", value = "用户名称"
    private String userName;

    private static final long serialVersionUID = 1L;
}
