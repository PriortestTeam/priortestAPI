package com.hu.oneclick.common.security.handler;

import com.alibaba.fastjson2.JSONObject;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.model.base.Resp;
import java.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.www.NonceExpiredException;

/**
 * @author qingyang
 */
public class HttpStatusLoginFailureHandler implements AuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
		System.out.println(">>> 登录失败，异常信息: " + (exception == null ? "null" : exception.getMessage()));
		String result = "";
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType("application/json;charset=UTF-8");
        // 如果 token 过期，exception中没有数据，返回认证失败结果
        if (exception == null || exception.getCause() == null) {
            result = JSONObject.toJSONString(new Resp.Builder<String>().setData(SysConstantEnum.AUTH_FAILED.getValue()).httpBadRequest().fail());
        } else if (exception.getCause() instanceof BizException) {
			final BizException bizException = (BizException) exception.getCause();
			String code = bizException.getCode();
            String message = bizException.getMessage();

            // 用户过期场景使用专门的HTTP状态码
            if ("4001".equals(code)) {
                response.setStatus(HttpStatus.PAYMENT_REQUIRED.value()); // 402
				result = JSONObject.toJSONString(new Resp.Builder<String>().buildResult(code, message, HttpStatus.PAYMENT_REQUIRED.value()));
            } else {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
				result = JSONObject.toJSONString(new Resp.Builder<String>().buildResult(code, message, HttpStatus.BAD_REQUEST.value()));
            }
		} else if (exception instanceof BadCredentialsException || exception instanceof InternalAuthenticationServiceException) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			result = JSONObject.toJSONString(new Resp.Builder<String>().setData(SysConstantEnum.LOGIN_FAILED.getValue()).fail());
		} else if (exception instanceof InsufficientAuthenticationException
				|| exception instanceof  NonceExpiredException) {
			result = JSONObject.toJSONString(new Resp.Builder<String>().setData(SysConstantEnum.AUTH_FAILED.getValue()).fail());
		} else if (exception instanceof UsernameNotFoundException) {
			result = JSONObject.toJSONString(new Resp.Builder<String>().setData(SysConstantEnum.USERNAME_ERROR.getValue()).fail());
	    } else {
			result = JSONObject.toJSONString(new Resp.Builder<String>().setData(SysConstantEnum.AUTH_FAILED.getValue()).httpBadRequest().fail());
		}
		response.getWriter().write(result);
	}


}