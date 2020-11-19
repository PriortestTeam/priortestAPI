package com.hu.oneclick.model.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.NotNull;
import java.util.Collection;

/**
 * @author qingyang
 */
public class AuthLoginUser implements UserDetails {
    /**
     * 登录用户的邮箱
     */
    @NotNull(message = "邮箱不能为空")
    private String username;
    /**
     * 密码
     */
    @NotNull(message = "密码不能为空")
    private String password;
    /**
     * 邮箱验证码
     */
    @NotNull(message = "验证码不能为空")
    private String code;

    private SysUser sysUser;


    public AuthLoginUser(SysUser sysUser) {
        this.sysUser = sysUser;
    }


    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }


    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public SysUser getSysUser() {
        return sysUser;
    }
    public void setSysUser(SysUser user) {
        this.sysUser =  user;
    }
}
