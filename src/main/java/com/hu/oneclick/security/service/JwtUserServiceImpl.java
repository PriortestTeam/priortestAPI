package com.hu.oneclick.security.service;

import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.hu.oneclick.dao.SysUserDao;
import com.hu.oneclick.model.domain.AuthLoginUser;
import com.hu.oneclick.model.domain.SysUser;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author qingyang
 */
@Service("jwtUserService")
public class JwtUserServiceImpl implements UserDetailsService {

	private final PasswordEncoder passwordEncoder;

	private final SysUserDao sysUserDao;

	private final RedissonClient redisClient;

	public JwtUserServiceImpl(SysUserDao sysUserDao, RedissonClient redisClient) {
		this.passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		this.sysUserDao = sysUserDao;
		this.redisClient = redisClient;
	}

	public AuthLoginUser getUserLoginInfo(String username) {
		String salt = "123456ef";
		String key = "login_" + username;
    	//将salt放到password字段返回
		RBucket<String> bucket = redisClient.getBucket(key);
		AuthLoginUser authLoginUser = JSONObject.parseObject(bucket.get(), AuthLoginUser.class);
		if (authLoginUser == null) {
			throw new InsufficientAuthenticationException("token invalidation");
		}
		authLoginUser.setUsername(username);
		authLoginUser.setPassword(salt);
		return authLoginUser;
	}

	public String saveUserLoginInfo(AuthLoginUser user) {
		//正式开发时可以调用该方法实时生成加密的salt,BCrypt.gensalt();
		String salt = "123456ef";
		Algorithm algorithm = Algorithm.HMAC256(salt);
		//设置1小时后过期
		Date date = new Date(System.currentTimeMillis()+3600*1000);
		//创建token
		String sign = JWT.create()
				.withSubject(user.getUsername())
				.withExpiresAt(date)
				.withIssuedAt(new Date())
				.sign(algorithm);
		String s = JSONObject.toJSONString(user);
		String key = "login_" + user.getUsername();
		RBucket<String> bucket = redisClient.getBucket(key);
		if (bucket.isExists()){
			bucket.delete();
		}
		bucket.set(s);
		bucket.expire(1, TimeUnit.HOURS);
		return sign;
	}

	@Override
	public AuthLoginUser loadUserByUsername(String username) throws UsernameNotFoundException {
		SysUser user = sysUserDao.queryByEmail(username);
		AuthLoginUser authLoginUser = new AuthLoginUser(user);
		authLoginUser.setUsername(username);
		authLoginUser.setPassword(user.getPassword());
		return authLoginUser;
	}

	/**
	 * 加密密码
	 * @param password
	 * @return
	 */
	public String encryptPassword(String password) {
		return passwordEncoder.encode(password);
	}

	public void deleteUserLoginInfo(String username) {
		String key = "login_" + username;
		RBucket<String> bucket = redisClient.getBucket(key);
		bucket.delete();
		SecurityContextHolder.clearContext();
	}

	/**
	 * 登录
	 * @param user
	 * @return
	 */
	public void login(AuthLoginUser user) {
		loadUserByUsername(user.getUsername());
	}
}
