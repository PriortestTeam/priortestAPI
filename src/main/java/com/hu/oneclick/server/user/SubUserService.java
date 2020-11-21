package com.hu.oneclick.server.user;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.SysUser;

import java.util.List;

/**
 * @author qingyang
 */
public interface SubUserService {

    /**
     * 查询成员列表
     * @return
     */
    Resp<List<SysUser>> querySubUsers(SysUser sysUser);

}

