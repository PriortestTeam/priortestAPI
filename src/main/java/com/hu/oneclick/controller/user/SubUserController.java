package com.hu.oneclick.controller.user;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.entity.Project;
import com.hu.oneclick.model.entity.SubUserProject;
import com.hu.oneclick.model.domain.dto.SubUserDto;
import com.hu.oneclick.server.service.ProjectService;
import com.hu.oneclick.server.user.SubUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@Api(tags = "子用户管理")
public class SubUserController {

    private final SubUserService subUserService;

    private final ProjectService projectService;


    public SubUserController(SubUserService subUserService, ProjectService projectService) {
        this.subUserService = subUserService;
        this.projectService = projectService;
    }


    @PostMapping("querySubUsers")
    @ApiOperation("查询子用户")
    public Resp<List<SubUserDto>> querySubUsers(@RequestBody SubUserDto sysUser) {
        return subUserService.querySubUsers(sysUser);
    }

    @GetMapping("queryForProjects")
    @ApiOperation("查询项目")
    public Resp<List<Project>> queryForProjects() {
        return projectService.queryForProjects();
    }

    @PostMapping("createSubUser")
    @ApiOperation("添加子用户")
    public Resp<String> createSubUser(@RequestBody SubUserDto sysUser) {
        return subUserService.createSubUser(sysUser);
    }

    @PostMapping("updateSubUser")
    @ApiOperation("修改子用户")
    public Resp<String> updateSubUser(@RequestBody SubUserDto sysUser) {
        return subUserService.updateSubUser(sysUser);
    }

    @PostMapping("updateSubUserPassword")
    @ApiOperation("修改子用户密码")
    public Resp<String> updateSubUserPassword(@RequestBody SubUserDto sysUser) {
        return subUserService.updateSubUserPassword(sysUser);
    }


    @DeleteMapping("deleteSubUser/{id}")
    @ApiOperation("删除子用户")
    public Resp<String> deleteSubUser(@PathVariable String id) {
        return subUserService.deleteSubUser(id);
    }


    @GetMapping("getSubUserProject/{userId}")
    @ApiOperation("返回用户的项目列表")
    public Resp<SubUserProject> getSubUserProject(@PathVariable String userId) {
        return subUserService.getSubUserProject(userId);
    }

    @GetMapping("queryForProjectsbyUser")
    @ApiOperation("返回当前用户的项目列表")
    public Resp<List<Project>> getThePersonInCharge() {
        return subUserService.getProjectByUserId();
    }
}
