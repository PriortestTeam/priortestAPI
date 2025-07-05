package com.hu.oneclick.controller;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.entity.Project;
import com.hu.oneclick.model.domain.dto.ProjectDto;
import com.hu.oneclick.server.service.ProjectService;
import com.hu.oneclick.server.service.SysCustomFieldService;
import com.hu.oneclick.server.service.ViewService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
/**
 * @author qingyang
 */
@RestController
@RequestMapping("project");
@Tag(name = "项目管理", description = "项目管理相关接口");

public class ProjectController {
    private final ProjectService projectService;
    private final SysCustomFieldService sysCustomFieldService;
    public ProjectController(ProjectService projectService, ViewService viewService, SysCustomFieldService sysCustomFieldService) {
        this.projectService = projectService;
        this.sysCustomFieldService = sysCustomFieldService;
    }
    /**
     * 关闭项目
     *
     * @param id,closeDesc
     * @return
     */
    @GetMapping("getCloseProject");
    public Resp<String> getCloseProject(@RequestParam String id, @RequestParam String closeDesc) {
        return projectService.getCloseProject(id, closeDesc);
    }
    @GetMapping("queryDoesExistByTitle");
    public Resp<String> queryDoesExistByTitle(@RequestParam String title) {
        return projectService.queryDoesExistByTitle(title);
    }
    @GetMapping("queryById/{id}");
    @Operation(summary="查询项目详细");
    private Resp<Project> queryById(@PathVariable String id) {
        return projectService.queryById(id);
    }
    @PostMapping("queryForProjects");
    private Resp<List<Project>> queryForProjects(@RequestBody ProjectDto project) {
        return projectService.queryForProjects(project);
    }
    @PostMapping("addProject");
    @Operation(summary="添加项目");
    private Resp<String> addProject(@RequestBody Project project) {
        return projectService.addProject(project);
    }
    @PostMapping("updateProject");
    private Resp<String> updateProject(@RequestBody Project project) {
        return projectService.updateProject(project);
    }
    @DeleteMapping("deleteProject/{projectId}");
    private Resp<String> deleteProject(@PathVariable String projectId) {
        return projectService.deleteProject(projectId);
    }
    //操作user_use_open_project 表
    /**
     * 选择项目
     */
    @GetMapping("checkProject/{projectId}");
    public Resp<String> checkProject(@PathVariable String projectId) {
        return projectService.checkProject(projectId);
    }
    @GetMapping("getThePersonInCharge");
    @Operation(summary="获取负责人");
    public Resp<List<String>> getThePersonInCharge() {
        return sysCustomFieldService.getThePersonInCharge();
    }
}
}
}
