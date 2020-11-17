package com.hu.oneclick.server.user;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.dto.RegisterUser;

/**
 * @author qingyang
 */
public interface UserService {

    /**
     * 用户注册
     * @param sysUser
     * @return
     */
    Resp<String> register(RegisterUser registerUser);

    /**
     * 发送邮箱 验证码
     * @param email
     * @return
     */
    Resp<String> sendEmailCode(String email);
}
