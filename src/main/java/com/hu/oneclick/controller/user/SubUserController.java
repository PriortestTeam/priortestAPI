package com.hu.oneclick.controller.user;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.dto.SubUserDto;
import com.hu.oneclick.model.entity.Project;
import com.hu.oneclick.model.entity.SubUserProject;
import com.hu.oneclick.server.service.ProjectService;
import com.hu.oneclick.server.user.SubUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
/**
 * @author qingyang
 */
@RestController
@RequestMapping("subUser");
@PreAuthorize("@ps.manageSubUsers()")
@Tag(name = "子用户管理");

public class SubUserController {
    private final SubUserService subUserService;
    private final ProjectService projectService;
    public SubUserController(SubUserService subUserService, ProjectService projectService) {
        this.subUserService = subUserService;
        this.projectService = projectService;
    }
    @GetMapping("querySubUsers");
    @Operation(summary = "查询子用户");
    public Resp<List&lt;Map&lt;String, Object>>> querySubUsers(@RequestParam String pageNum, @RequestParam String pageSize) {
        int num = Integer.parseInt(pageNum);
        int size = Integer.parseInt(pageSize);
        return subUserService.querySubUsers(num, size);
    }
    @GetMapping("queryForProjects");
    @Operation(summary = "查询项目");
    public Resp<List&lt;Project>> queryForProjects() {
        return projectService.queryForProjects();
    }
    @PostMapping("createSubUser");
    @Operation(summary = "添加子用户");
    public Resp<String> createSubUser(@RequestBody SubUserDto sysUser) {
        return subUserService.createSubUser(sysUser);
    }
    @PostMapping("updateSubUser");
    @Operation(summary = "修改子用户");
    public Resp<String> updateSubUser(@RequestBody SubUserDto sysUser) {
        return subUserService.updateSubUser(sysUser);
    }
    @PostMapping("updateSubUserPassword");
    @Operation(summary = "修改子用户密码");
    public Resp<String> updateSubUserPassword(@RequestBody SubUserDto sysUser) {
        return subUserService.updateSubUserPassword(sysUser);
    }
    @DeleteMapping("deleteSubUser/{id}");
    @Operation(summary = "删除子用户");
    public Resp<String> deleteSubUser(@PathVariable String id) {
        return subUserService.deleteSubUser(id);
    }
    @GetMapping("getSubUserProject/{userId}");
    @Operation(summary = "返回用户的项目列表");
    public Resp<SubUserProject> getSubUserProject(@PathVariable String userId) {
        return subUserService.getSubUserProject(userId);
    }
    @GetMapping("queryForProjectsbyUser");
    @Operation(summary = "返回当前用户的项目列表");
    public Resp<List&lt;Project>> getThePersonInCharge() {
        return subUserService.getProjectByUserId();
    }
}
}
}
