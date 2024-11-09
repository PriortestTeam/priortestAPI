package com.hu.oneclick.server.user;

import com.hu.oneclick.controller.req.RegisterBody;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.entity.SysUser;
import com.hu.oneclick.model.entity.SysUserToken;
import com.hu.oneclick.model.domain.dto.ActivateAccountDto;
import com.hu.oneclick.model.domain.dto.SubUserDto;
import com.hu.oneclick.model.domain.dto.SysProjectPermissionDto;
import com.hu.oneclick.model.domain.dto.SysUserTokenDto;
import java.util.List;
import java.util.Map;

/**
 * @author qingyang
 */
public interface UserService {

    /**
     * 用户注册
     *
     * @param registerBody 注册体
     * @return
     */
    Resp<String> register(RegisterBody registerBody);

    /**
     * 修改密码
     *
     * @param args
     * @return
     */
    Resp<String> modifyPassword(Map<String, String> args);

    /**
     * 重置密码
     *
     * @param args
     * @return
     */
    Resp<String> resetPassword(Map<String, String> args);


    /**
     * 查询邮箱是否存在
     *
     * @param email
     * @return
     */
    Resp<String> queryEmailDoesItExist(String email);

    /**
     * 更新用户信息
     *
     * @param sysUser
     * @return
     */
    Resp<String> updateUserInfo(SysUser sysUser);

    /**
     * 查询用户信息
     *
     * @return
     */
    Resp<SysUser> queryUserInfo();

    /**
     * 查询用户权限
     *
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
     *
     * @param id
     * @return
     */
    Resp<String> deleteUserById(String id);

    /**
     * 管理员生成token
     *
     * @param sysUserTokenDto
     * @Param: []
     * @return: com.hu.oneclick.model.base.Resp<java.lang.String>
     * @Author: MaSiyi
     * @Date: 2021/11/10
     */
    Resp<SysUserToken> makeToken(SysUserTokenDto sysUserTokenDto);

    /**
     * 获取生成的token列表
     *
     * @Param: []
     * @return: com.hu.oneclick.model.base.Resp<java.lang.String>
     * @Author: MaSiyi
     * @Date: 2021/11/10
     */
    Resp<List<SysUserToken>> listTokens();

    /** 删除token
     * @Param: [tokenId]
     * @return: com.hu.oneclick.model.base.Resp<java.lang.String>
     * @Author: MaSiyi
     * @Date: 2021/11/10
     */
    Resp<String> deleteToken(Integer tokenId);

    /** 获取用户账号信息
     * @Param: [emailId]
     * @return: void
     * @Author: MaSiyi
     * @Date: 2021/11/10
     */
    Boolean getUserAccountInfo(String emailId, String token);

    Resp<String> verifyLinkString(String linkStr);

    /** 查询用户和子用户
     * @Param: [masterId]
     * @return: java.util.List<com.hu.oneclick.model.entity.SysUser>
     * @Author: MaSiyi
     * @Date: 2021/12/15
     */
    List<SysUser> queryByUserIdAndParentId(String masterId);

    /** 返回用户的激活次数
     * @Param: [email]
     * @return: com.hu.oneclick.model.base.Resp<java.lang.String>
     * @Author: MaSiyi
     * @Date: 2021/12/18
     */
    Resp<String> getUserActivNumber(String email);
    /**
     * @description 通过项目ID获取用户信息
     * @author Vince
     * @createTime 2022/12/24 19:56
     * @param projectId 项目id
     * @return Resp<List<Map<String, Object>>>
     */
    Resp<List<Map<String, Object>>> listUserByProjectId(Long projectId);
}
