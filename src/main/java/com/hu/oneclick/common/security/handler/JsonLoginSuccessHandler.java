package com.hu.oneclick.common.security.handler;
import com.alibaba.fastjson2.JSON;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.dto.AuthLoginUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
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
    private final JwtUserServiceImpl jwtUserServiceImpl;
    public JsonLoginSuccessHandler(JwtUserServiceImpl jwtUserServiceImpl) {
        this.jwtUserServiceImpl = jwtUserServiceImpl;
    }
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String token = jwtUserServiceImpl.saveUserLoginInfo((AuthLoginUser) authentication.getPrincipal();
        response.setContentType("application/json;charset=UTF-8");
        Map&lt;String,String> result = new HashMap&lt;>(2);
        result.put("token",token);
        result.put("msg",SysConstantEnum.LOGIN_SUCCESS.getValue();
        Resp<Map&lt;String,String>> ok = new Resp.Builder<Map&lt;String,String>>().setData(result).ok();
        String s = JSON.toJSONString(ok);
        response.getWriter().write(s);
    }
}
}
}
