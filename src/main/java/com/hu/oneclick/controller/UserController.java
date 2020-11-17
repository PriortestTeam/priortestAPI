package com.hu.oneclick.controller;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.dto.RegisterUser;
import com.hu.oneclick.server.user.UserService;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("sendEmailCode")
    public Resp<String> sendEmailCode(@RequestParam String email) {
        return userService.sendEmailCode(email);
    }

}
