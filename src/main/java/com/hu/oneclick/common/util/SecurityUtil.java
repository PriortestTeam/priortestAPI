package com.hu.oneclick.common.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


/**
 * 安全服务工具类
 * 暂时适用，随时可能删除
 * @author xiaohai
 * @date 2023/03/03
 */
public class SecurityUtil {
    ///**
    // * 用户ID
    // **/
    //public static Long getUserId() {
    //    try {
    //        return getLoginUser().getUserId();
    //    } catch (Exception e) {
    //        throw new ServiceException("获取用户ID异常", HttpStatus.UNAUTHORIZED);
    //    }
    //}
    //
    //public static Long getUserIdOrDefault() {
    //    try {
    //        return getUserId();
    //    } catch (Exception e) {
    //        return UserConstants.DEFAULT_USER_ID;
    //    }
    //}
    //
    ///**
    // * 获取部门ID
    // **/
    //public static Long getDeptId() {
    //    try {
    //        return getLoginUser().getDeptId();
    //    } catch (Exception e) {
    //        throw new ServiceException("获取部门ID异常", HttpStatus.UNAUTHORIZED);
    //    }
    //}
    //
    ///**
    // * 获取用户账户
    // **/
    //public static String getUsername() {
    //    try {
    //        return getLoginUser().getUsername();
    //    } catch (Exception e) {
    //        throw new ServiceException("获取用户账户异常", HttpStatus.UNAUTHORIZED);
    //    }
    //}

    //public static String getUserNameOrDefault() {
    //    try {
    //        return getUsername();
    //    } catch (Exception e) {
    //        return UserConstants.DEFAULT_USER_NAME;
    //    }
    //}

    /**
     * 获取用户
     **/
    public static Object getLoginUser() {
        try {
            return getAuthentication().getPrincipal();
        } catch (Exception e) {
            throw new UsernameNotFoundException("获取用户信息异常");
        }
    }

    /**
     * 获取Authentication
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 生成BCryptPasswordEncoder密码
     *
     * @param password 密码
     * @return 加密字符串
     */
    public static String encryptPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    /**
     * 判断密码是否相同
     *
     * @param rawPassword     真实密码
     * @param encodedPassword 加密后字符
     * @return 结果
     */
    public static boolean matchesPassword(String rawPassword, String encodedPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    ///**
    // * 是否为管理员
    // *
    // * @return 结果
    // */
    //public static boolean isAdmin() {
    //    return getUserId() != null && 1L == getUserId();
    //}

}
