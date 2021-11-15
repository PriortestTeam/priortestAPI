package com.hu.oneclick.server.service;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.SysRole;

import java.util.List;

/**
 * @author qingyang
 */
public interface SysRoleService {

    Resp<List<SysRole>> queryRoles();

    List<SysRole> findUserRole();

}
