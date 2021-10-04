package com.hu.oneclick.controller.user;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.dto.PlatformUserDto;
import com.hu.oneclick.model.domain.dto.SubUserDto;
import com.hu.oneclick.server.service.ProjectService;
import com.hu.oneclick.server.service.SysRoleService;
import com.hu.oneclick.server.user.PlatformUserService;
import com.hu.oneclick.server.user.SubUserService;
import com.hu.oneclick.server.user.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author xwf
 *
 * 超级管理员用户，管理平台用户
 */
@RestController()
@RequestMapping("/platformUser")
@PreAuthorize("@ps.backstageManagement()")
public class PlatformUserController {

    private final PlatformUserService platformUserService;

    private final UserService userService;

    public PlatformUserController(PlatformUserService platformUserService,UserService userService) {
        this.platformUserService = platformUserService;
        this.userService = userService;
    }

    @PostMapping("/queryPlatformUser")
    public Resp<List<PlatformUserDto>> queryPlatformUser(@RequestBody PlatformUserDto platformUserDto){

        return platformUserService.queryPlatformUsers(platformUserDto);
    }

    @PostMapping("createPlatformUser")
    public Resp<String> createPlatformUser(@RequestBody PlatformUserDto platformUserDto){
        return platformUserService.createPlatformUser(platformUserDto);
    }

    @PostMapping("updatePlatformUser")
    public Resp<String> updatePlatformUser(@RequestBody PlatformUserDto platformUserDto){
        return platformUserService.updatePlatformUser(platformUserDto);
    }

    @DeleteMapping("deletePlatformUser/{id}")
    public Resp<String> deletePlatformUser(@PathVariable String id){
        return platformUserService.deletePlatformUserByid(id);
    }


}
