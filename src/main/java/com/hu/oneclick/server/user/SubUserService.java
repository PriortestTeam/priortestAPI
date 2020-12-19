package com.hu.oneclick.server.user;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.SysUser;
import com.hu.oneclick.model.domain.dto.SubUserDto;

import java.util.List;

/**
 * @author qingyang
 */
public interface SubUserService {

    /**
     * 查询成员列表
     * @param sysUser
     * @return
     */
    Resp<List<SubUserDto>> querySubUsers(SubUserDto sysUser);

    /**
     * 创建一个成员用户
     * @param sysUser
     * @return
     */
    Resp<String> createSubUser(SubUserDto sysUser);

    /**
     * 修改成员用户
     * @param sysUser
     * @return
     */
    Resp<String> updateSubUser(SubUserDto sysUser);
}

