package com.hu.oneclick.controller;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.Project;
import com.hu.oneclick.server.service.ProjectService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author qingyang
 */
@RestController
@RequestMapping("project")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }


    @GetMapping("queryDoesExistByTitle")
    public Resp<String> queryDoesExistByTitle(@RequestParam String title) {
        return projectService.queryDoesExistByTitle(title);
    }

    @GetMapping("queryById/{id}")
    private Resp<Project> queryById(@PathVariable String id){
        return projectService.queryById(id);
    }


    @PostMapping("queryForProjects")
    private Resp<List<Project>> queryForProjects(@RequestBody Project project){
        return projectService.queryForProjects(project);
    }

    @PostMapping("addProject")
    private Resp<String> addProject(@RequestBody Project project){
        return projectService.addProject(project);
    }

    @PostMapping("updateProject")
    private Resp<String> updateProject(@RequestBody Project project){
        return projectService.updateProject(project);
    }

    @DeleteMapping("deleteProject/{projectId}")
    private Resp<String> deleteProject(@PathVariable String projectId){
        return projectService.deleteProject(projectId);
    }


    //操作user_use_open_project 表

    /**
     * 选择项目
     */
    @GetMapping("checkProject/{projectId}")
    public Resp<String> checkProject(@PathVariable String projectId){
        return projectService.checkProject(projectId);
    }

}
