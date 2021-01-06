package com.hu.oneclick.controller.user;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.Project;
import com.hu.oneclick.model.domain.SysRole;
import com.hu.oneclick.model.domain.dto.SubUserDto;
import com.hu.oneclick.server.service.ProjectService;
import com.hu.oneclick.server.service.SysRoleService;
import com.hu.oneclick.server.user.SubUserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author qingyang
 */
@RestController
@RequestMapping("subUser")
@PreAuthorize("@ps.manageSubUsers()")
public class SubUserController {

    private final SubUserService subUserService;

    private final ProjectService projectService;

    private final SysRoleService sysRoleService;

    public SubUserController(SubUserService subUserService, ProjectService projectService, SysRoleService sysRoleService) {
        this.subUserService = subUserService;
        this.projectService = projectService;
        this.sysRoleService = sysRoleService;
    }


    @PostMapping("querySubUsers")
    public Resp<List<SubUserDto>> querySubUsers(@RequestBody SubUserDto sysUser){
        return  subUserService.querySubUsers(sysUser);
    }

    @GetMapping("queryForProjects")
    public Resp<List<Project>> queryForProjects(){
        return  projectService.queryForProjects();
    }

    @GetMapping("queryRoles")
    public Resp<List<SysRole>> queryRoles(){
        return  sysRoleService.queryRoles();
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
