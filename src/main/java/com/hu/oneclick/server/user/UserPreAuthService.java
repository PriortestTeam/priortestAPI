package com.hu.oneclick.server.user;

import com.hu.oneclick.controller.req.RegisterBody;
import com.hu.oneclick.model.base.Resp;

/**
 * 用户登录前操作服务接口
 *
 * @author oneclick
 */
public interface UserPreAuthService {

    /**
     * 用户注册
     */
    Resp<String> register(RegisterBody registerBody);

    /**
     * 忘记密码
     */
    Resp<String> forgetThePassword(String email);
}