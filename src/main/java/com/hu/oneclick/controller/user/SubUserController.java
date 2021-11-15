package com.hu.oneclick.controller.user;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.Project;
import com.hu.oneclick.model.domain.dto.SubUserDto;
import com.hu.oneclick.server.service.ProjectService;
import com.hu.oneclick.server.user.SubUserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    private final ProjectService projectService;


    public SubUserController(SubUserService subUserService, ProjectService projectService) {
        this.subUserService = subUserService;
        this.projectService = projectService;
    }


    @PostMapping("querySubUsers")
    public Resp<List<SubUserDto>> querySubUsers(@RequestBody SubUserDto sysUser){
        return  subUserService.querySubUsers(sysUser);
    }

    @GetMapping("queryForProjects")
    public Resp<List<Project>> queryForProjects(){
        return  projectService.queryForProjects();
    }

    @PostMapping("createSubUser")
    public Resp<String> createSubUser(@RequestBody SubUserDto sysUser){
        return  subUserService.createSubUser(sysUser);
    }

    @PostMapping("updateSubUser")
    public Resp<String> updateSubUser(@RequestBody SubUserDto sysUser){
        return  subUserService.updateSubUser(sysUser);
    }

    @PostMapping("updateSubUserPassword")
    public Resp<String> updateSubUserPassword(@RequestBody SubUserDto sysUser){
        return  subUserService.updateSubUserPassword(sysUser);
    }


    @DeleteMapping("deleteSubUser/{id}")
    public Resp<String> deleteSubUser(@PathVariable String id){
        return  subUserService.deleteSubUser(id);
    }

}
