package com.hu.oneclick.server.service;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.Project;
import com.hu.oneclick.model.domain.dto.ProjectDto;
import com.hu.oneclick.model.domain.dto.SignOffDto;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
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

    Resp<List<String>> getProject();


    Resp<String> generate(SignOffDto signOffDto, HttpServletRequest req);

    Resp<String> upload(MultipartFile file, HttpServletRequest req);
}
