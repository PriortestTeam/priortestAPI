package com.hu.oneclick.common.security.handler;

import com.hu.oneclick.model.domain.dto.AuthLoginUser;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author qingyang
 */
@Component
public class TokenClearLogoutHandler implements LogoutHandler {

	private final JwtUserServiceImpl jwtUserServiceImpl;

	public TokenClearLogoutHandler(JwtUserServiceImpl jwtUserServiceImpl) {
		this.jwtUserServiceImpl = jwtUserServiceImpl;
	}

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		clearToken(authentication);
	}

	protected void clearToken(Authentication authentication) {
		if(authentication == null) {
			return;
		}
		AuthLoginUser user = (AuthLoginUser)authentication.getPrincipal();
		if(user!=null && user.getUsername()!=null) {
			jwtUserServiceImpl.deleteUserLoginInfo(user.getUsername());
		}
	}

}
