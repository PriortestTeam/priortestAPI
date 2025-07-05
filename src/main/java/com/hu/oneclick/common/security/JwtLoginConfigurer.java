package com.hu.oneclick.common.security;

import com.hu.oneclick.common.security.flutter.JwtAuthenticationFilter;
import com.hu.oneclick.common.security.handler.HttpStatusLoginFailureHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;

/**
 * @author qingyang
 */


public class JwtLoginConfigurer<T extends JwtLoginConfigurer<T, B>, B extends HttpSecurityBuilder<B>> extends AbstractHttpConfigurer<T, B> {

	private final JwtAuthenticationFilter authFilter;

	public JwtLoginConfigurer() {
		this.authFilter = new JwtAuthenticationFilter();
	}

	@Override
	public void configure(B http) {
		authFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class);
		authFilter.setAuthenticationFailureHandler(new HttpStatusLoginFailureHandler();

		JwtAuthenticationFilter filter = postProcess(authFilter);
		http.addFilterBefore(filter, LogoutFilter.class);
	}

	public JwtLoginConfigurer<T, B> permissiveRequestUrls(String ... urls){
		authFilter.setPermissiveUrl(urls);
		return this;
	}

	public JwtLoginConfigurer<T, B> tokenValidSuccessHandler(AuthenticationSuccessHandler successHandler){
		authFilter.setAuthenticationSuccessHandler(successHandler);
		return this;
	}

}
}
