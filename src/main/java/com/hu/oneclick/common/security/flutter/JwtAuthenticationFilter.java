package com.hu.oneclick.common.security.flutter;

import com.alibaba.fastjson2.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.hu.oneclick.common.constant.OneConstant.REDIS_KEY_PREFIX;
import com.hu.oneclick.common.security.ApiToken;
import com.hu.oneclick.common.security.JwtAuthenticationToken;
import com.hu.oneclick.dao.SysUserTokenDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.dto.AuthLoginUser;
import com.hu.oneclick.model.entity.SysUserToken;
import com.hu.oneclick.server.user.UserService;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * @author qingyang
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final RequestMatcher requiresAuthenticationRequestMatcher;
    private List<RequestMatcher> permissiveRequestMatchers;
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;
    @Autowired
    private SysUserTokenDao sysUserTokenDao;
    @Autowired
    private RedissonClient redisClient;
    @Autowired
    private UserDetailsService userDetailsService;

    private AuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
    private AuthenticationFailureHandler failureHandler = new SimpleUrlAuthenticationFailureHandler();

    public JwtAuthenticationFilter() {
        // 默认不处理任何请求
        this.requiresAuthenticationRequestMatcher = request -> false;
        // 初始化白名单，添加登录接口
        this.permissiveRequestMatchers = new ArrayList<>();
        setPermissiveUrl("/login", "/register", "/logout");
        System.out.println(">>> 初始化JwtAuthenticationFilter，白名单URLs: " + permissiveRequestMatchers);
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(authenticationManager, "authenticationManager must be specified");
        Assert.notNull(successHandler, "AuthenticationSuccessHandler must be specified");
        Assert.notNull(failureHandler, "AuthenticationFailureHandler must be specified");
        System.out.println(">>> JwtAuthenticationFilter.afterPropertiesSet() 完成初始化");
    }

    @Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		System.out.println(">>> [" + request.getMethod() + "] JwtAuthenticationFilter 收到请求: " + request.getRequestURI());
		System.out.println(">>> 请求头信息:");
		System.out.println(">>>   Authorization: " + request.getHeader("Authorization"));
		System.out.println(">>>   emailId: " + request.getHeader("emailId"));

		String path = request.getRequestURI();

		// 检查是否是需要跳过JWT验证的路径
		if (path.equals("/api/login") || path.equals("/login") || 
		    path.startsWith("/api/apiAdpater/") ||
		    path.startsWith("/api/swagger-ui/") ||
		    path.startsWith("/api/v3/api-docs") ||
		    path.equals("/api/swagger-ui.html") ||
		    path.startsWith("/swagger-ui/") ||
		    path.startsWith("/v3/api-docs") ||
		    path.equals("/swagger-ui.html") ||
		    path.startsWith("/api/swagger-resources/") ||
		    path.startsWith("/api/webjars/") ||
		    path.startsWith("/swagger-resources/") ||
		    path.startsWith("/webjars/")) {
		    System.out.println(">>> 跳过JWT验证，直接放行请求: " + path);
		    filterChain.doFilter(request, response);
		    return;
		}

		System.out.println(">>> 检查是否是白名单URL...");
		for (RequestMatcher matcher : permissiveRequestMatchers) {
			System.out.println(">>>   检查matcher: " + matcher);
			if (matcher.matches(request)) {
				System.out.println(">>> 是白名单URL，直接放行: " + request.getRequestURI());
				filterChain.doFilter(request, response);
				return;
			}
		}
		System.out.println(">>> 不是白名单URL，继续处理...");

		try {
			Authentication authResult = attemptAuthentication(request, response, filterChain);
			if(authResult != null) {
				successfulAuthentication(request, response, filterChain, authResult);
			} else {
				unsuccessfulAuthentication(request, response, new BadCredentialsException("Authentication failed"));
			}
		} catch (AuthenticationException e) {
			System.out.println(">>> JWT认证失败: " + e.getMessage());
			unsuccessfulAuthentication(request, response, e);
		}
		filterChain.doFilter(request, response);
	}

    protected String getJwtToken(HttpServletRequest request) {
        String authInfo = request.getHeader("Authorization");
        return StringUtils.removeStart(authInfo, "Bearer ");
    }

    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response, AuthenticationException failed)
        throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        System.out.println(">>> 认证失败处理开始");
        System.out.println(">>> 错误信息: " + failed.getMessage());
        System.out.println(">>> 错误类型: " + failed.getClass().getSimpleName());
        if (failed.getCause() != null) {
            System.out.println(">>> 原始错误: " + failed.getCause().getMessage());
        }
        failureHandler.onAuthenticationFailure(request, response, failed);
        System.out.println(">>> 认证失败处理完成");
    }

    protected boolean requiresAuthentication(HttpServletRequest request,
                                             HttpServletResponse response) {
        return requiresAuthenticationRequestMatcher.matches(request);
    }

    protected boolean permissiveRequest(HttpServletRequest request) {
        if (permissiveRequestMatchers == null) {
            return false;
        }
        for (RequestMatcher permissiveMatcher : permissiveRequestMatchers) {
            if (permissiveMatcher.matches(request)) {
                return true;
            }
        }
        return false;
    }

    public void setPermissiveUrl(String... urls) {
        if (permissiveRequestMatchers == null) {
            permissiveRequestMatchers = new ArrayList<>();
        }
        System.out.println(">>> 添加白名单URLs:");
        for (String url : urls) {
            System.out.println(">>>   原始URL: " + url);
            permissiveRequestMatchers.add(new AntPathRequestMatcher(url));
            if (!url.startsWith("/api/")) {
                String apiUrl = "/api" + url;
                System.out.println(">>>   添加API版本: " + apiUrl);
                permissiveRequestMatchers.add(new AntPathRequestMatcher(apiUrl));
            }
        }
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public void setAuthenticationSuccessHandler(AuthenticationSuccessHandler successHandler) {
        Assert.notNull(successHandler, "successHandler cannot be null");
        this.successHandler = successHandler;
    }

    public void setAuthenticationFailureHandler(AuthenticationFailureHandler failureHandler) {
        Assert.notNull(failureHandler, "failureHandler cannot be null");
        this.failureHandler = failureHandler;
    }

    protected AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    protected AuthenticationSuccessHandler getSuccessHandler() {
        return successHandler;
    }

    protected AuthenticationFailureHandler getFailureHandler() {
        return failureHandler;
    }

	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws AuthenticationException, IOException {
		return null;
	}

	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
	}
}