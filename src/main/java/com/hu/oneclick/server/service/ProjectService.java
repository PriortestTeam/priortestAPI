package com.hu.oneclick.server.service;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.Project;
import com.hu.oneclick.model.domain.UserUseOpenProject;
import com.hu.oneclick.model.domain.dto.ProjectDto;
import com.hu.oneclick.model.domain.dto.SignOffDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author qingyang
 */
public interface ProjectService {

    Resp<Project> queryById(String id);

    Resp<String> queryDoesExistByTitle(String title);

    Resp<List<Project>> queryForProjects(ProjectDto project);

    Resp<List<Project>> queryForProjects();

    Resp<String> addProject(Project project);

    Resp<String> updateProject(Project project);

    Resp<String> deleteProject(String projectId);

    Resp<String> checkProject(String projectId);

    Resp<String> getCloseProject(String id, String closeDesc);

    Resp<String> generate(SignOffDto signOffDto);

    Resp<String> upload(MultipartFile file);

    /** 初始化仓库
     * @Param: []
     * @return: java.lang.Integer
     * @Author: MaSiyi
     * @Date: 2021/12/16
     */
    Integer initProject(Project project, UserUseOpenProject userUseOpenProject);
}
