package com.hu.oneclick.controller.user;

import com.hu.oneclick.model.annotation.Page;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.dto.SubUserDto;
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
    @PostMapping("querySubUsers")
    public Resp<List<SubUserDto>> querySubUsers(@RequestBody SubUserDto sysUser){
        return  subUserService.querySubUsers(sysUser);
    }

    @PostMapping("createSubUser")
    public Resp<String> createSubUser(@RequestBody SubUserDto sysUser){
        return  subUserService.createSubUser(sysUser);
    }

    @PostMapping("updateSubUser")
    public Resp<String> updateSubUser(@RequestBody SubUserDto sysUser){
        return  subUserService.updateSubUser(sysUser);
    }

}
