
package com.hu.oneclick.server.user;

import com.hu.oneclick.controller.req.RegisterBody;
import com.hu.oneclick.model.base.Resp;

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
    
    // 后续会添加其他登录前的方法：
    // Resp<String> forgetThePassword(ForgetPasswordDto dto);
    // Resp<String> applyForAnExtension(ExtensionDto dto);
    // 等等...
}
