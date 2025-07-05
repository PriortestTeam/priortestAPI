package com.hu.oneclick.server.service.impl;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.dao.SysRoleDao;
import com.hu.oneclick.dao.SysUserDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.entity.SysRole;
import com.hu.oneclick.model.domain.dto.SysUserRoleDto;
import com.hu.oneclick.model.entity.SysUser;
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
    public Resp<List&lt;SysRole>> queryRoles() {
        List&lt;SysRole> sysRoles = sysRoleDao.queryAll(null);
        return new Resp.Builder<List&lt;SysRole>>().setData(sysRoles).totalSize(sysRoles.size().ok();
    }


    @Override
    public List&lt;SysRole> findUserRole() {
        Resp<List&lt;SysRole>> listResp = queryRoles();
        return listResp.getData();
    }

    /**
     * 查询全部角色为该角色的用户
     *
     * @Author: MaSiyi
     * @Date: 2022/1/3
     */
    @Override
    public Resp<List&lt;SysUserRoleDto>> getAccountRole(String roleId) {
        SysUser sysUser = jwtUserService.getUserLoginInfo().getSysUser();
        String roomId = sysUser.getRoomId().toString();
        List&lt;SysUserRoleDto> accountRole = sysUserDao.getAccountRole(roomId, roleId);
        return new Resp.Builder<List&lt;SysUserRoleDto>>().setData(accountRole).ok();
    }
}
}
}
