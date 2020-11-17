package com.hu.oneclick.security.handler;

import com.alibaba.fastjson.JSONObject;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.model.base.Resp;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author qingyang
 */
public class HttpStatusLoginFailureHandler implements AuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
		String result = "";
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType("application/json;charset=UTF-8");
		if (exception instanceof BadCredentialsException) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			result = JSONObject.toJSONString(new Resp.Builder<String>().setData(SysConstantEnum.LOGIN_FAILED.getValue()).fail());
		} else if (exception instanceof InsufficientAuthenticationException){
			result = JSONObject.toJSONString(new Resp.Builder<String>().setData(SysConstantEnum.AUTH_FAILED.getValue()).fail());
		} else if (exception instanceof UsernameNotFoundException){
			result = JSONObject.toJSONString(new Resp.Builder<String>().setData(SysConstantEnum.USERNAME_ERROR.getValue()).fail());
		} else {
			result = "系统异常。";
		}
		response.getWriter().write(result);
	}


}
