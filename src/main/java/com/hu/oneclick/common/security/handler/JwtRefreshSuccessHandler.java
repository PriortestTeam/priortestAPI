package com.hu.oneclick.common.security.handler;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.hu.oneclick.model.domain.dto.AuthLoginUser;
import com.hu.oneclick.common.security.JwtAuthenticationToken;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author qingyang
 */
@Component
public class JwtRefreshSuccessHandler implements AuthenticationSuccessHandler {
	/**
	 * 刷新间隔5分钟
	 */
	private static final int TOKEN_REFRESH_INTERVAL = 300;

	private final JwtUserServiceImpl jwtUserServiceImpl;

	public JwtRefreshSuccessHandler(JwtUserServiceImpl jwtUserServiceImpl) {
		this.jwtUserServiceImpl = jwtUserServiceImpl;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {
		DecodedJWT jwt = ((JwtAuthenticationToken)authentication).getToken();
		boolean shouldRefresh = shouldTokenRefresh(jwt.getIssuedAt());
		if(shouldRefresh) {
            String newToken = jwtUserServiceImpl.saveUserLoginInfo((AuthLoginUser)authentication.getPrincipal());
            response.setHeader("Authorization", newToken);
        }
	}

	protected boolean shouldTokenRefresh(Date issueAt){
        LocalDateTime issueTime = LocalDateTime.ofInstant(issueAt.toInstant(), ZoneId.systemDefault());
        return LocalDateTime.now().minusSeconds(TOKEN_REFRESH_INTERVAL).isAfter(issueTime);
    }

}