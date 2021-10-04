package com.hu.oneclick.server.user;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.SysUser;
import com.hu.oneclick.model.domain.dto.ActivateAccountDto;
import com.hu.oneclick.model.domain.dto.SubUserDto;
import com.hu.oneclick.model.domain.dto.SysProjectPermissionDto;

import java.util.List;
import java.util.Map;

/**
 * @author qingyang
 */
public interface UserService {

    /**
     * 用户注册
     * @param registerUser
     * @return
     */
    Resp<String> register(SysUser registerUser);

    /**
     * 修改密码
     * @param args
     * @return
     */
    Resp<String> modifyPassword(Map<String, String> args);
    /**
     * 重置密码
     * @param args
     * @return
     */
    Resp<String> resetPassword(Map<String, String> args);



    /**
     * 查询邮箱是否存在
     * @param email
     * @return
     */
    Resp<String> queryEmailDoesItExist(String email);

    /**
     * 更新用户信息
     * @param sysUser
     * @return
     */
    Resp<String> updateUserInfo(SysUser sysUser);

    /**
     * 查询用户信息
     * @return
     */
    Resp<SysUser> queryUserInfo();

    /**
     * 查询用户权限
     * @return
     */
    Resp<List<SysProjectPermissionDto>> queryUserPermissions();

    Resp<List<SubUserDto>> queryByNameSubUsers(String subUserName);

    Resp<String> activateAccount(ActivateAccountDto activateAccountDto, String activation);

    Resp<String> forgetThePassword(String email);

    Resp<String> forgetThePasswordIn(ActivateAccountDto activateAccountDto);

    Resp<String> applyForAnExtension(String activateAccountDto);

    Resp<String> applyForAnExtensionIn(ActivateAccountDto activateAccountDto);

    /**
     * 删除用户
     * @param id
     * @return
     */
    Resp<String> deleteUserById(String id);


}
