
package com.hu.oneclick.common.security.handler;

import com.alibaba.fastjson2.JSON;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.dto.AuthLoginUser;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author qingyang
 */
@Component
public class JsonLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private JwtUserServiceImpl jwtUserServiceImpl;

    @Autowired
    private RedissonClient redissonClient;

    public JsonLoginSuccessHandler(JwtUserServiceImpl jwtUserServiceImpl) {
        this.jwtUserServiceImpl = jwtUserServiceImpl;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        
        System.out.println(">>> 用户登录成功，开始生成JWT token");
        
        // 生成JWT token
        String token = jwtUserServiceImpl.saveUserLoginInfo((AuthLoginUser) authentication.getPrincipal());
        
        // 获取用户邮箱（作为用户标识）
        AuthLoginUser loginUser = (AuthLoginUser) authentication.getPrincipal();
        String userEmail = loginUser.getUsername(); // 假设username就是email
        
        System.out.println(">>> 为用户 " + userEmail + " 生成了新的JWT token");
        System.out.println(">>> 新生成的token前20位: " + token.substring(0, Math.min(20, token.length())) + "...");
        
        // 将最新的token存储到Redis中，键格式: LOGIN_JWT:用户邮箱
        String redisKey = "LOGIN_JWT:" + userEmail;
        RBucket<String> bucket = redissonClient.getBucket(redisKey);
        
        // 设置token，过期时间1小时
        bucket.set(token, 1, TimeUnit.HOURS);
        
        System.out.println(">>> Redis中存储当前有效token: " + redisKey);
        System.out.println(">>> Redis存储完成，过期时间: 1小时");
        
        // 返回响应
        response.setContentType("application/json;charset=UTF-8");
        Map<String,String> result = new HashMap<>(2);
        result.put("token",token);
        result.put("msg",SysConstantEnum.LOGIN_SUCCESS.getValue());
        Resp<Map<String,String>> ok = new Resp.Builder<Map<String,String>>().setData(result).ok();
        String s = JSON.toJSONString(ok);
        response.getWriter().write(s);
    }

}
