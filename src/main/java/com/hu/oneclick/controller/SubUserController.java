package com.hu.oneclick.controller;

import com.hu.oneclick.model.annotation.Page;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.SysUser;
import com.hu.oneclick.server.user.SubUserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author qingyang
 */
@RestController
@RequestMapping("subUser")
@PreAuthorize("@ps.manageSubUsers()")
public class SubUserController {

    private final SubUserService subUserService;

    public SubUserController(SubUserService subUserService) {
        this.subUserService = subUserService;
    }


    @Page
    @PostMapping
    public Resp<List<SysUser>> querySubUsers(@RequestBody SysUser sysUser){
        return  subUserService.querySubUsers(sysUser);
    }

}
