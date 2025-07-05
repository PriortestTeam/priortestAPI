package com.hu.oneclick.common.security.handler;
import com.alibaba.fastjson2.JSON;
import com.hu.oneclick.common.constant.OneConstant.REDIS_KEY_PREFIX;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.dto.AuthLoginUser;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
/**
 * @author qingyang
 */
@Component

public class TokenClearLogoutHandler implements LogoutHandler {
    private final JwtUserServiceImpl jwtUserServiceImpl;
    public TokenClearLogoutHandler(JwtUserServiceImpl jwtUserServiceImpl) {
        this.jwtUserServiceImpl = jwtUserServiceImpl;
    }
    @Autowired
    private RedissonClient redissonClient;
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        clearToken(authentication, response);
    }
    protected void clearToken(Authentication authentication, HttpServletResponse response) {
        if (authentication == null) {
            return;
        }
        AuthLoginUser user = (AuthLoginUser)authentication.getPrincipal();
        String username = "";
        if (user != null && user.getUsername() != null) {
            username = user.getSysUser().getEmail();
            jwtUserServiceImpl.deleteUserLoginInfo(user.getUsername();
            // 退出登陆的时候，将登陆的token清理掉；
            RBucket<Object> bucket = redissonClient.getBucket(REDIS_KEY_PREFIX.LOGIN_JWT + username);
            if (!Objects.isNull(bucket) {
                bucket.delete();
            }
        }
        Map<String, String> result = new HashMap<>(2);
        result.put("status", SysConstantEnum.LOGOUT_SUCCESS.getCode();
        result.put("msg", SysConstantEnum.LOGOUT_SUCCESS.getValue();
        if (jwtUserServiceImpl.verifyUserExists(username) {
            //存在返回系统异常
            result.put("status", SysConstantEnum.SYS_ERROR.getCode();
            result.put("msg", SysConstantEnum.SYS_ERROR.getValue();
        }
        Resp<Map<String, String>> ok = new Resp.Builder<Map<String, String>>().setData(result).ok();
        String s = JSON.toJSONString(ok);
        try {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(s);
        } catch (IOException e) {
            throw new BizException(SysConstantEnum.SYS_ERROR.getCode(), SysConstantEnum.SYS_ERROR.getValue();
        }
    }
}
}
}
