package com.hu.oneclick.common.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.hu.oneclick.model.domain.dto.AuthLoginUser;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * @author masiyi
 */
public class ApiToken implements Authentication {

	private Boolean isAuth;
	private String tokenName;


	public ApiToken() {
	}

	public ApiToken(Boolean isAuth) {
		this.isAuth = isAuth;
	}

	public ApiToken(Boolean isAuth, String tokenName) {
		this.isAuth = isAuth;
		this.tokenName = tokenName;
	}

	public String getTokenName() {
		return tokenName;
	}

	@Override
	public String getName() {
		return null;
	}
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return null;
	}
	@Override
	public Object getCredentials() {
		return null;
	}
	@Override
	public Object getDetails() {
		return null;
	}
	@Override
	public Object getPrincipal() {
		return null;
	}
	@Override
	public boolean isAuthenticated() {
		return isAuth;
	}
	@Override
	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

	}
}
