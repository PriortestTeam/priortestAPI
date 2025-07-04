package com.hu.oneclick.common.security.flutter;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.hu.oneclick.common.constant.OneConstant;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpMediaTypeNotSupportedException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

/**
 * @author qingyang
 * 登录 login
 */
public class MyUsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public MyUsernamePasswordAuthenticationFilter() {
        super(new AndRequestMatcher(
            new AntPathRequestMatcher("/api/login", "POST"),
            new MediaTypeRequestMatcher(MediaType.APPLICATION_JSON)
        ));
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(getAuthenticationManager(), "authenticationManager must be specified");
        Assert.notNull(getSuccessHandler(), "AuthenticationSuccessHandler must be specified");
        Assert.notNull(getFailureHandler(), "AuthenticationFailureHandler must be specified");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
        throws AuthenticationException, IOException, ServletException {
        
        if (!request.getContentType().contains(MediaType.APPLICATION_JSON_VALUE)) {
            throw new HttpMediaTypeNotSupportedException(
                request.getContentType(), 
                Collections.singletonList(MediaType.APPLICATION_JSON));
        }

        System.out.println(">>> 进入 attemptAuthentication 方法，收到登录请求: " + request.getRequestURI());
        String body = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        String username = null, password = null, masterIdentifier = null;

        if (StringUtils.hasText(body)) {
            try {
                JSONObject jsonObj = JSON.parseObject(body);
                username = jsonObj.getString("username");
                password = jsonObj.getString("password");
                masterIdentifier = jsonObj.getString("masterIdentifier");
            } catch (Exception e) {
                throw new AuthenticationException("Invalid JSON format") {};
            }
        }

        System.out.println(">>> 登录参数 username: " + username + ", password: " + password);

        if (username == null) {
            username = "";
        }
        if (password == null) {
            password = "";
        }

        //判断是否是子用户登录,不为空则拼接登录邮箱账号
        if (!StringUtils.isEmpty(masterIdentifier)) {
            username = masterIdentifier + OneConstant.COMMON.SUB_USER_SEPARATOR + username;
        }

        username = username.trim();

        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
            username, password);

        return this.getAuthenticationManager().authenticate(authRequest);
    }
}
