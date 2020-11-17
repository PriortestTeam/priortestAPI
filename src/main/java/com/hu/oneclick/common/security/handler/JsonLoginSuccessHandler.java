package com.hu.oneclick.common.security.handler;

import com.alibaba.fastjson.JSONObject;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.AuthLoginUser;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
        String token = jwtUserServiceImpl.saveUserLoginInfo((AuthLoginUser) authentication.getPrincipal());
        response.setHeader("Authorization", token);
        response.setContentType("application/json;charset=UTF-8");
        String s = JSONObject.toJSONString(new Resp.Builder<String>().setData(SysConstantEnum.LOGIN_SUCCESS.getValue()).ok());
        response.getWriter().write(s);
    }

}
