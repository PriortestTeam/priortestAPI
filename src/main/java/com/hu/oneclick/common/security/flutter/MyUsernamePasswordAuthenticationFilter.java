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
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.http.InvalidMediaTypeException;

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
        super(new OrRequestMatcher(
            new AntPathRequestMatcher("/api/login", "POST"),
            new AntPathRequestMatcher("/login", "POST")
        ));
        System.out.println(">>> 初始化MyUsernamePasswordAuthenticationFilter，URL匹配模式: /api/login 和 /login (POST)");
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(getAuthenticationManager(), "authenticationManager must be specified");
        Assert.notNull(getSuccessHandler(), "AuthenticationSuccessHandler must be specified");
        Assert.notNull(getFailureHandler(), "AuthenticationFailureHandler must be specified");
        System.out.println(">>> MyUsernamePasswordAuthenticationFilter.afterPropertiesSet() 完成初始化");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
        throws AuthenticationException, IOException, ServletException {

        System.out.println(">>> 进入 attemptAuthentication 方法");
        System.out.println(">>> 请求URI: " + request.getRequestURI());
        System.out.println(">>> 请求方法: " + request.getMethod());
        System.out.println(">>> Content-Type: " + request.getContentType());

        // 使用Spring MediaType进行更规范的Content-Type检查
        try {
            if (request.getContentType() != null) {
                MediaType contentType = MediaType.parseMediaType(request.getContentType());
                if (!MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
                    System.out.println(">>> 不支持的Content-Type: " + request.getContentType());
                    throw new HttpMediaTypeNotSupportedException(
                        request.getContentType(), 
                        Collections.singletonList(MediaType.APPLICATION_JSON));
                }
                System.out.println(">>> Content-Type验证通过");
            } else {
                System.out.println(">>> Content-Type为空，拒绝请求");
                throw new HttpMediaTypeNotSupportedException(
                    "Content-Type不能为空", 
                    Collections.singletonList(MediaType.APPLICATION_JSON));
            }
        } catch (InvalidMediaTypeException e) {
            System.out.println(">>> Content-Type格式无效: " + request.getContentType());
            throw new HttpMediaTypeNotSupportedException(
                request.getContentType(), 
                Collections.singletonList(MediaType.APPLICATION_JSON));
        }

        String body = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        System.out.println(">>> 请求体: " + body);

        String username = null, password = null, masterIdentifier = null;

        if (StringUtils.hasText(body)) {
            try {
                JSONObject jsonObj = JSON.parseObject(body);
                username = jsonObj.getString("username");
                password = jsonObj.getString("password");
                masterIdentifier = jsonObj.getString("masterIdentifier");
                System.out.println(">>> 解析JSON成功");
                System.out.println(">>> username: " + username);
                System.out.println(">>> masterIdentifier: " + masterIdentifier);
            } catch (Exception e) {
                System.out.println(">>> JSON解析失败: " + e.getMessage());
                throw new AuthenticationException("Invalid JSON format") {};
            }
        } else {
            System.out.println(">>> 请求体为空");
        }

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

        System.out.println(">>> 创建认证token");
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
            username, password);

        try {
            System.out.println(">>> 开始认证");
            Authentication authResult = this.getAuthenticationManager().authenticate(authRequest);
            System.out.println(">>> 认证成功");
            return authResult;
        } catch (AuthenticationException e) {
            System.out.println(">>> ========== MyUsernamePasswordAuthenticationFilter 认证失败 ==========");
            System.out.println(">>> 认证失败异常类型: " + e.getClass().getName());
            System.out.println(">>> 认证失败异常消息: " + e.getMessage());
            System.out.println(">>> 认证失败异常原因: " + (e.getCause() == null ? "null" : e.getCause().getClass().getName()));
            System.out.println(">>> 即将调用 failureHandler 处理失败");
            System.out.println(">>> =======================================================");
            throw e;
        }
    }
}