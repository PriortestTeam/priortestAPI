package com.hu.oneclick.server.user;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.SysUser;
import com.hu.oneclick.model.domain.dto.RegisterUser;

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
    Resp<String> register(RegisterUser registerUser);

    /**
     * 修改密码
     * @param args
     * @param sysUser
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
     * 发送邮箱 验证码
     * @param email
     * @param prefix
     * @return
     */
    Resp<String> sendEmailCode(String email,String prefix);

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
}
