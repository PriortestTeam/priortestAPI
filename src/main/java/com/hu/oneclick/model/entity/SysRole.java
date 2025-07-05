package com.hu.oneclick.model.entity;
import com.hu.oneclick.model.base.BaseEntity;
import java.io.Serializable;
/**
 * 平台角色表(SysRole)实体类
 *
 * @author makejava
 * @since 2021-01-06 13:10:06
 */

public class SysRole extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -64922478479324066L;
    /**
     * 角色名称
     */
    private String roleName;
    private Integer sort;
    /**
     * 角色描述
     */
    private String roleDesc;
    public String getRoleName() {
        return roleName;
    }
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
    public Integer getSort() {
        return sort;
    }
    public void setSort(Integer sort) {
        this.sort = sort;
    }
    public String getRoleDesc() {
        return roleDesc;
    }
    public void setRoleDesc(String roleDesc) {
        this.roleDesc = roleDesc;
    }
}
}
}
