package com.hu.oneclick.server.service.impl;

import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.dao.SysRoleDao;
import com.hu.oneclick.dao.SysUserDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.SysRole;
import com.hu.oneclick.model.domain.dto.SysUserRoleDto;
import com.hu.oneclick.server.service.SysRoleService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author qingyang
 */
@Service
public class SysRoleServiceImpl implements SysRoleService {

    private final SysRoleDao sysRoleDao;

    private final JwtUserServiceImpl jwtUserService;

    private final SysUserDao sysUserDao;

    public SysRoleServiceImpl(SysRoleDao sysRoleDao, JwtUserServiceImpl jwtUserService, SysUserDao sysUserDao) {
        this.sysRoleDao = sysRoleDao;
        this.jwtUserService = jwtUserService;
        this.sysUserDao = sysUserDao;
    }

    @Override
    public Resp<List<SysRole>> queryRoles() {
        List<SysRole> sysRoles = sysRoleDao.queryAll(null);
        return new Resp.Builder<List<SysRole>>().setData(sysRoles).totalSize(sysRoles.size()).ok();
    }


    @Override
    public List<SysRole> findUserRole() {
        Resp<List<SysRole>> listResp = queryRoles();
        return listResp.getData();
    }

    /**
     * 查询全部角色为该角色的用户
     *
     * @param roleName
     * @Param: [roleName]
     * @return: com.hu.oneclick.model.base.Resp<java.util.List < com.hu.oneclick.model.domain.SysUser>>
     * @Author: MaSiyi
     * @Date: 2022/1/3
     */
    @Override
    public Resp<List<SysUserRoleDto>> getAccountRole(String roleName) {
        SysRole sysRole = sysRoleDao.queryByRoleName(roleName);
        String userId = jwtUserService.getId();
        String roleId = sysRole.getId();
        List<SysUserRoleDto> accountRole = sysUserDao.getAccountRole(userId, roleId);
        return new Resp.Builder<List<SysUserRoleDto>>().setData(accountRole).ok();
    }
}
