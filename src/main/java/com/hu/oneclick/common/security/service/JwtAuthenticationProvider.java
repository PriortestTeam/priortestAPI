package com.hu.oneclick.common.security.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.hu.oneclick.model.domain.dto.AuthLoginUser;
import com.hu.oneclick.common.security.JwtAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.www.NonceExpiredException;
import org.springframework.stereotype.Component;

import java.util.Calendar;

/**
 * @author qingyang
 */
@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

	private final JwtUserServiceImpl userService;

	public JwtAuthenticationProvider(JwtUserServiceImpl userService) {
		this.userService = userService;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		DecodedJWT jwt = ((JwtAuthenticationToken)authentication).getToken();
		if(jwt.getExpiresAt().before(Calendar.getInstance().getTime())) {
			throw new NonceExpiredException("Token expires");
		}
		String username = jwt.getSubject();
		System.out.println(">>> JwtAuthenticationProvider.authenticate - 用户名: " + username);
		AuthLoginUser user;
		try {
			user = userService.getUserLoginInfo(username);
		} catch (UsernameNotFoundException e) {
			System.out.println(">>> JwtAuthenticationProvider 捕获到 UsernameNotFoundException，直接抛出");
			throw e;
		}

		if(user == null || user.getPassword()==null) {
			throw new NonceExpiredException("Token expires");
		}
		String encryptSalt = user.getPassword();
		try {
            Algorithm algorithm = Algorithm.HMAC256(encryptSalt);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withSubject(username)
                    .build();
            verifier.verify(jwt.getToken());
        } catch (Exception e) {
			System.out.println(">>> JwtAuthenticationProvider 捕获到其他异常: " + e.getClass().getName());
            throw new BadCredentialsException("JWT token verify fail", e);
        }
		return new JwtAuthenticationToken(user, jwt, user.getAuthorities());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return JwtAuthenticationToken.class.isAssignableFrom(authentication);
	}

}