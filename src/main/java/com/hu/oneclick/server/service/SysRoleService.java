package com.hu.oneclick.server.service;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.entity.SysRole;
import com.hu.oneclick.model.domain.dto.SysUserRoleDto;

import java.util.List;

/**
 * @author qingyang
 */
public interface SysRoleService {

    Resp<List<SysRole>> queryRoles();

    List<SysRole> findUserRole();

    /** 查询全部角色为该角色的用户
     * @Param: [roleName]
     * @return: com.hu.oneclick.model.base.Resp<java.util.List<com.hu.oneclick.model.entity.SysUser>>
     * @Author: MaSiyi
     * @Date: 2022/1/3
     */
    Resp<List<SysUserRoleDto>> getAccountRole(String roleName);
}
