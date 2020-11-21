package com.hu.oneclick.server.user;

import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.dao.SysUserDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.SysUser;
import com.hu.oneclick.model.domain.dto.AuthLoginUser;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author qingyang
 */
@Service
public class SubUserServiceImpl implements SubUserService{

    private final JwtUserServiceImpl jwtUserServiceImpl;

    private final SysUserDao sysUserDao;

    public SubUserServiceImpl(JwtUserServiceImpl jwtUserServiceImpl, SysUserDao sysUserDao) {
        this.jwtUserServiceImpl = jwtUserServiceImpl;
        this.sysUserDao = sysUserDao;
    }

    @Override
    public Resp<List<SysUser>> querySubUsers(SysUser sysUser) {
        AuthLoginUser userLoginInfo = jwtUserServiceImpl.getUserLoginInfo();
        sysUser.setParentId(userLoginInfo.getSysUser().getId());
        List<SysUser> sysUsers = sysUserDao.querySubUsers(sysUser);
        return new Resp.Builder<List<SysUser>>().setData(sysUsers).total(sysUsers.size()).ok();
    }
}
