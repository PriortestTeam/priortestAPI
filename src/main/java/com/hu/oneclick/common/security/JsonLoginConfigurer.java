package com.hu.oneclick.common.security;

import com.hu.oneclick.common.security.flutter.MyUsernamePasswordAuthenticationFilter;
import com.hu.oneclick.common.security.handler.HttpStatusLoginFailureHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;

/**
 * @author qingyang
 */


public class JsonLoginConfigurer<T extends JsonLoginConfigurer<T, B>, B extends HttpSecurityBuilder<B>> extends AbstractHttpConfigurer<T, B> {

    private final MyUsernamePasswordAuthenticationFilter authFilter;

    public JsonLoginConfigurer() {
        this.authFilter = new MyUsernamePasswordAuthenticationFilter();
    }

    @Override
    public void configure(B http) {
        authFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class);
        authFilter.setAuthenticationFailureHandler(new HttpStatusLoginFailureHandler();
        authFilter.setSessionAuthenticationStrategy(new NullAuthenticatedSessionStrategy();

        MyUsernamePasswordAuthenticationFilter filter = postProcess(authFilter);
        http.addFilterAfter(filter, LogoutFilter.class);
    }

    public JsonLoginConfigurer<T, B> loginSuccessHandler(AuthenticationSuccessHandler authSuccessHandler) {
        authFilter.setAuthenticationSuccessHandler(authSuccessHandler);
        return this;
    }

}
}
