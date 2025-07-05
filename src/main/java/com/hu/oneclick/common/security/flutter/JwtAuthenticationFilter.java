package com.hu.oneclick.common.security.flutter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.hu.oneclick.common.security.JwtAuthenticationToken;
import com.hu.oneclick.dao.SysUserTokenDao;
import com.hu.oneclick.model.domain.dto.AuthLoginUser;
import com.hu.oneclick.model.entity.SysUserToken;
import com.hu.oneclick.server.user.UserService;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author qingyang
 * JWT认证过滤器，支持两种Token认证方式：
 * 1. Bearer Token: 用户登录后生成的JWT token，用于前端应用访问
 * 2. API Token: 用户生成的token，包含emailId和Authorization，用于访问/api/apiAdpater路径
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
        // 初始化白名单，添加登录接口和Swagger相关接口
        this.permissiveRequestMatchers = new ArrayList<>();
        setPermissiveUrl("/login", "/register", "/logout", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**");
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
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println(">>> [" + request.getMethod() + "] JwtAuthenticationFilter 收到请求: " + request.getRequestURI());
        String path = request.getRequestURI();

        // 检查是否是需要跳过JWT验证的路径（登录、Swagger等）
        if (isPermissivePath(path)) {
            System.out.println(">>> 跳过JWT验证，直接放行请求: " + path);
            filterChain.doFilter(request, response);
            return;
        }

        // 检查是否是白名单URL
        for (RequestMatcher matcher : permissiveRequestMatchers) {
            if (matcher.matches(request)) {
                System.out.println(">>> 是白名单URL，直接放行: " + request.getRequestURI());
                filterChain.doFilter(request, response);
                return;
            }
        }

		// 第二种认证方式：emailId + token (用户生成的token)
		String emailId = request.getHeader("emailId");
		String token = request.getHeader("Authorization");

		if (StringUtils.isNotBlank(emailId) && StringUtils.isNotBlank(token)) {
			System.out.println(">>> 检测到emailId和token认证方式");

			// 验证用户账号和token
			Boolean isValid = userService.getUserAccountInfo(emailId, token);
			if (isValid != null && isValid) {
				System.out.println(">>> Token验证成功，设置认证信息");

				// 创建认证token并设置到SecurityContext
				org.springframework.security.authentication.UsernamePasswordAuthenticationToken authToken =
					new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(emailId, token, null);
				SecurityContextHolder.getContext().setAuthentication(authToken);

				filterChain.doFilter(request, response);
				return;
			}
			System.out.println(">>> Token验证失败，返回401错误");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().write("Invalid token");
			return;
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

	private boolean isApiAdapterRequest(String requestURI) {
		return requestURI != null && requestURI.contains("/api/apiAdapter");
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {
		String requestURI = request.getRequestURI();
		System.out.println(">>> 尝试认证URL: " + requestURI);

		// 针对/api/apiAdapter路径的特殊处理
		if (isApiAdapterRequest(requestURI)) {
			String emailId = request.getHeader("emailId");
			String token = request.getHeader("Authorization");

			if (StringUtils.isNotBlank(emailId) && StringUtils.isNotBlank(token)) {
				// 验证用户账号和token
				Boolean isValid = userService.getUserAccountInfo(emailId, token);
				if (isValid != null && isValid) {
					// 创建认证token
					return new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(emailId, token);
				}
			}
			throw new BadCredentialsException("Invalid emailId or token for API adapter");
		}

        // 检查是否是需要跳过JWT验证的路径
    }

    /**
     * 检查是否是需要跳过JWT验证的路径
     */
    private boolean isPermissivePath(String path) {
        return path.equals("/api/login") || path.equals("/login") ||
               path.startsWith("/api/swagger-ui/") ||
               path.startsWith("/api/v3/api-docs") ||
               path.equals("/api/swagger-ui.html") ||
               path.startsWith("/swagger-ui/") ||
               path.startsWith("/v3/api-docs") ||
               path.equals("/swagger-ui.html") ||
               path.startsWith("/api/swagger-resources/") ||
               path.startsWith("/api/webjars/") ||
               path.startsWith("/swagger-resources/") ||
               path.startsWith("/webjars/");
    }

    /**
     * API Token认证 - 用于 /api/apiAdpater 路径
     * 需要同时验证 emailId 和 Authorization 头
     */
    private Authentication attemptApiTokenAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        String emailId = request.getHeader("emailId");
        String authorization = request.getHeader("Authorization");

        System.out.println(">>> API Token认证 - emailId: " + emailId);
        System.out.println(">>> API Token认证 - Authorization: " + authorization);

        if (StringUtils.isBlank(emailId) || StringUtils.isBlank(authorization)) {
            throw new BadCredentialsException("API Token认证失败：缺少emailId或Authorization头");
        }

        // 验证API Token
        SysUserToken userToken = sysUserTokenDao.selectByUserIdAndToken(emailId, authorization);
        if (userToken == null) {
            throw new BadCredentialsException("API Token认证失败：无效的token");
        }

        // 获取用户信息
        AuthLoginUser user = userService.getUserAccountInfo(emailId, authorization);
        if (user == null) {
            throw new BadCredentialsException("API Token认证失败：用户不存在");
        }

        System.out.println(">>> API Token认证成功，用户: " + emailId);
        return new JwtAuthenticationToken(user, null, user.getAuthorities());
    }

    /**
     * Bearer Token认证 - 用于普通前端应用访问
     * 验证JWT Bearer token
     */
    private Authentication attemptBearerTokenAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        String authorization = request.getHeader("Authorization");
        System.out.println(">>> Bearer Token认证 - Authorization: " + authorization);

        if (StringUtils.isBlank(authorization) || !authorization.startsWith("Bearer ")) {
            throw new BadCredentialsException("Bearer Token认证失败：缺少或格式错误的Authorization头");
        }

        String token = authorization.substring(7); // 移除 "Bearer " 前缀

        try {
            // 解码JWT token
            DecodedJWT jwt = JWT.decode(token);
            String username = jwt.getSubject();

            if (StringUtils.isBlank(username)) {
                throw new BadCredentialsException("Bearer Token认证失败：token中缺少用户信息");
            }

            // 获取用户信息用于验证
            AuthLoginUser user = userService.getUserLoginInfo(username);
            if (user == null || StringUtils.isBlank(user.getPassword())) {
                throw new BadCredentialsException("Bearer Token认证失败：用户不存在或密码为空");
            }

            // 使用用户密码作为密钥验证JWT
            String encryptSalt = user.getPassword();
            Algorithm algorithm = Algorithm.HMAC256(encryptSalt);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withSubject(username)
                    .build();
            verifier.verify(token);

            System.out.println(">>> Bearer Token认证成功，用户: " + username);
            return new JwtAuthenticationToken(user, jwt, user.getAuthorities());

        } catch (Exception e) {
            throw new BadCredentialsException("Bearer Token认证失败：" + e.getMessage(), e);
        }
    }

    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws AuthenticationException, IOException, ServletException {

        String path = request.getRequestURI();
        if (path.startsWith("/api/apiAdpater")) {
            return attemptApiTokenAuthentication(request, response);
        } else {
            return attemptBearerTokenAuthentication(request, response);
        }
    }

    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult)
            throws IOException, ServletException {
        SecurityContextHolder.getContext().setAuthentication(authResult);
        successHandler.onAuthenticationSuccess(request, response, authResult);
    }

    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed)
            throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        failureHandler.onAuthenticationFailure(request, response, failed);
    }

    public void setPermissiveUrl(String... urls) {
        List<RequestMatcher> requestMatchers = new ArrayList<>();
        for (String url : urls) {
            requestMatchers.add(new AntPathRequestMatcher(url));
        }
        setPermissiveRequestMatchers(requestMatchers);
    }

    public void setPermissiveRequestMatchers(List<RequestMatcher> requestMatchers) {
        this.permissiveRequestMatchers = requestMatchers;
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
}