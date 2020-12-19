package com.hu.oneclick.server.service.impl;

import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.dao.SysUserDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.SysUser;
import com.hu.oneclick.model.domain.dto.SubUserDto;
import com.hu.oneclick.model.domain.dto.SubUserPermissionDto;
import com.hu.oneclick.server.service.SettingPermissionService;
import org.springframework.stereotype.Service;

/**
 * @author qingyang
 */
@Service
public class SettingPermissionServiceImpl implements SettingPermissionService {

    private final JwtUserServiceImpl jwtUserServiceImpl;

    private final SysUserDao sysUserDao;

    public SettingPermissionServiceImpl(JwtUserServiceImpl jwtUserServiceImpl, SysUserDao sysUserDao) {
        this.jwtUserServiceImpl = jwtUserServiceImpl;
        this.sysUserDao = sysUserDao;
    }


    @Override
    public Resp<SubUserPermissionDto> getPermissions(String subUserId) {
        SysUser masterUser = jwtUserServiceImpl.getUserLoginInfo().getSysUser();

        SubUserDto subUserDto = sysUserDao.querySubUserInfo(masterUser.getId(), subUserId);
        if (subUserDto == null){
            return new Resp.Builder<SubUserPermissionDto>().ok();
        }


        return null;
    }
}
