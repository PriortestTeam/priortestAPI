package com.hu.oneclick.server.user;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.SysUser;
import com.hu.oneclick.model.domain.dto.PlatformUserDto;
import com.hu.oneclick.model.domain.dto.SubUserDto;

import java.util.List;

/**
 * @author xwf
 * @date 2021/7/19 21:42
 * 管理平台用户
 */
public interface PlatformUserService {


    /**
     * 创建平台用户
     * @param platformUserDto
     * @return
     */
    Resp<String> createPlatformUser(PlatformUserDto platformUserDto);


    /**
     * 查询平台成员列表
     * @param platformUserDto
     * @return
     */
    Resp<List<PlatformUserDto>> queryPlatformUsers(PlatformUserDto platformUserDto);

    /**
     * 更新平台用户
     * @param platformUserDto
     * @return
     */
    Resp<String> updatePlatformUser(PlatformUserDto platformUserDto);
}
