package com.hu.oneclick.controller;

import com.hu.oneclick.model.domain.AuthLoginUser;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author qingyang
 */
@RestController
public class LoginController {

    private final JwtUserServiceImpl jwtUserServiceImpl;

    public LoginController(JwtUserServiceImpl jwtUserServiceImpl) {
        this.jwtUserServiceImpl = jwtUserServiceImpl;
    }


    @PostMapping("login")
    public void login(@RequestBody AuthLoginUser user){
        jwtUserServiceImpl.login(user);
    }

    @GetMapping("getHello")
    public String getHello(){
        return "登录成功请求获取hello 接口成功";
    }
}
