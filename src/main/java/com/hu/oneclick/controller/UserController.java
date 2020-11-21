package com.hu.oneclick.controller;

import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.SysUser;
import com.hu.oneclick.model.domain.dto.RegisterUser;
import com.hu.oneclick.server.user.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author qingyang
 */
@RestController
@RequestMapping("user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("register")
    public Resp<String> register(@RequestBody RegisterUser registerUser) {
        return userService.register(registerUser);
    }

    @GetMapping("sendEmailRegisterCode")
    public Resp<String> sendEmailRegisterCode(@RequestParam String email) {
        return userService.sendEmailCode(email, OneConstant.REDIS_KEY_PREFIX.REGISTRY);
    }

    @PostMapping("modifyPassword")
    public Resp<String> modifyPassword(@RequestBody Map<String,String> args){
        return userService.modifyPassword(args);
    }

    @GetMapping("sendModifyPasswordEmailCode")
    public Resp<String> sendModifyPasswordEmailCode(@RequestParam String email) {
        return userService.sendEmailCode(email,OneConstant.REDIS_KEY_PREFIX.MODIFY_PASSWORD);
    }

    @PostMapping("resetPassword")
    public Resp<String> resetPassword(@RequestBody Map<String,String> args){
        return userService.resetPassword(args);
    }

    @GetMapping("sendResetPasswordEmailUrl")
    public Resp<String> sendResetPasswordEmailCode(@RequestParam String email) {
        return userService.sendEmailCode(email,OneConstant.REDIS_KEY_PREFIX.RESET_PASSWORD);
    }

    @GetMapping("queryEmailDoesItExist")
    public Resp<String> queryEmailDoesItExist(@RequestParam String email) {
        return userService.queryEmailDoesItExist(email);
    }

    @PostMapping("updateUserInfo")
    public Resp<String> updateUserInfo(@RequestBody SysUser sysUser) {
        return userService.updateUserInfo(sysUser);
    }

    @PostMapping("queryUserInfo")
    public Resp<SysUser> queryUserInfo() {
        return userService.queryUserInfo();
    }



}
