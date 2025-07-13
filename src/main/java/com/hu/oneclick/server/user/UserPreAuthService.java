package com.hu.oneclick.server.user;

import com.hu.oneclick.controller.req.RegisterBody;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.dto.ActivateAccountDto;

/**
 * 用户登录前操作服务接口
 * 处理用户注册、忘记密码、申请延期等登录前的所有操作
 * 
 * @author oneclick
 */
public interface UserPreAuthService {

    /**
     * 用户注册
     * 
     * @param registerBody 注册信息
     * @return 注册结果
     */
    Resp<String> register(RegisterBody registerBody);

    /**
     * 忘记密码
     * 
     * @param email 用户邮箱
     * @return 操作结果
     */
    Resp<String> forgetThePassword(String email);

    /**
     * 激活账户
     * 
     * @param activateAccountDto 激活账户信息
     * @param activation 激活类型
     * @return 操作结果
     */
    Resp<String> activateAccount(ActivateAccountDto activateAccountDto, String activation);

    /**
     * 申请延期
     * 
     * @param email 用户邮箱
     * @return 操作结果
     */
    Resp<String> applyForAnExtension(String email);

    // 后续会添加其他登录前的方法：
    // 等等...
}