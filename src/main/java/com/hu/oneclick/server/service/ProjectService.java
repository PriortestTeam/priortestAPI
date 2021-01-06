package com.hu.oneclick.server.service;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.Project;

import java.util.List;

/**
 * @author qingyang
 */
public interface ProjectService {

    Resp<Project> queryById(String id);

    Resp<String> queryDoesExistByTitle(String title);

    Resp<List<Project>> queryForProjects(Project project);

    Resp<List<Project>> queryForProjects();

    Resp<String> addProject(Project project);

    Resp<String> updateProject(Project project);

    Resp<String> deleteProject(String projectId);

    Resp<String> checkProject(String projectId);
}
