package com.hu.oneclick.server.service.impl;

import com.hu.oneclick.dao.SysRoleDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.SysRole;
import com.hu.oneclick.server.service.SysRoleService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author qingyang
 */
@Service
public class SysRoleServiceImpl implements SysRoleService {

    private final SysRoleDao sysRoleDao;

    public SysRoleServiceImpl(SysRoleDao sysRoleDao) {
        this.sysRoleDao = sysRoleDao;
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
}
