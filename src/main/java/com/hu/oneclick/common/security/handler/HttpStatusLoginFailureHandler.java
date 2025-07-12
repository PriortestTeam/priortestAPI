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
		System.out.println(">>> ========== HttpStatusLoginFailureHandler ==========");
		System.out.println(">>> 登录失败，异常信息: " + (exception == null ? "null" : exception.getMessage()));
		System.out.println(">>> 异常类型: " + (exception == null ? "null" : exception.getClass().getName()));
		System.out.println(">>> 异常原因: " + (exception == null || exception.getCause() == null ? "null" : exception.getCause().getClass().getName()));
		
		String result = "";
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType("application/json;charset=UTF-8");
		
        // 首先处理具体的异常类型
        if (exception instanceof BadCredentialsException) {
			// 密码错误的情况
			System.out.println(">>> 处理路径: BadCredentialsException - 密码错误");
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			result = JSONObject.toJSONString(new Resp.Builder<String>().buildResult(SysConstantEnum.PASSWORD_ERROR.getCode(), SysConstantEnum.PASSWORD_ERROR.getValue(), HttpStatus.UNAUTHORIZED.value()));
			System.out.println(">>> 密码错误响应: " + result);
        } else if (exception.getCause() instanceof BizException) {
            System.out.println(">>> 处理路径: BizException");
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
		} else if (exception instanceof InternalAuthenticationServiceException) {
			System.out.println(">>> 处理路径: InternalAuthenticationServiceException");
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			result = JSONObject.toJSONString(new Resp.Builder<String>().setData(SysConstantEnum.LOGIN_FAILED.getValue()).fail());
		} else if (exception instanceof InsufficientAuthenticationException
				|| exception instanceof  NonceExpiredException) {
			System.out.println(">>> 处理路径: InsufficientAuthenticationException 或 NonceExpiredException");
			result = JSONObject.toJSONString(new Resp.Builder<String>().setData(SysConstantEnum.AUTH_FAILED.getValue()).fail());
		} else if (exception instanceof UsernameNotFoundException) {
			System.out.println(">>> 处理路径: UsernameNotFoundException - 用户名不存在");
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			result = JSONObject.toJSONString(new Resp.Builder<String>().buildResult(SysConstantEnum.USER_NOT_FOUND_401.getCode(), SysConstantEnum.USER_NOT_FOUND_401.getValue(), HttpStatus.UNAUTHORIZED.value()));
	    } else if (exception == null) {
            System.out.println(">>> 处理路径: exception为null");
            result = JSONObject.toJSONString(new Resp.Builder<String>().setData(SysConstantEnum.AUTH_FAILED.getValue()).httpBadRequest().fail());
        } else {
			System.out.println(">>> 处理路径: 其他异常类型");
			result = JSONObject.toJSONString(new Resp.Builder<String>().setData(SysConstantEnum.AUTH_FAILED.getValue()).httpBadRequest().fail());
		}
		
		System.out.println(">>> 最终响应HTTP状态码: " + response.getStatus());
		System.out.println(">>> 最终响应内容: " + result);
		System.out.println(">>> ===============================================");
		
		response.getWriter().write(result);
	}


}