package com.hu.oneclick.server.service.impl;

import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.common.security.service.PermissionService;
import com.hu.oneclick.dao.ProjectDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.base.Result;
import com.hu.oneclick.model.domain.Project;
import com.hu.oneclick.server.service.ProjectService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author qingyang
 */
@Service
public class ProjectServiceImpl implements ProjectService {

    private final static Logger logger = LoggerFactory.getLogger(ProjectServiceImpl.class);

    private final PermissionService permissionService;

    private final JwtUserServiceImpl jwtUserService;

    private final ProjectDao projectDao;

    public ProjectServiceImpl(PermissionService permissionService, JwtUserServiceImpl jwtUserService, ProjectDao projectDao) {
        this.permissionService = permissionService;
        this.jwtUserService = jwtUserService;
        this.projectDao = projectDao;
    }

    @Override
    public Resp<Project> queryById(String id) {
        return new Resp.Builder<Project>().setData(projectDao.queryById(id)).ok();
    }

    @Override
    public Resp<String> queryDoesExistById(String title) {
        try {
            Result.verifyDoesExist(queryByTitle(title));
            return new Resp.Builder<String>().ok();
        }catch (BizException e){
            return new Resp.Builder<String>().buildResult(e.getCode(),e.getMessage());
        }
    }

    @Override
    public Resp<List<Project>> queryForProjects(Project project) {
        List<Project> projects = projectDao.queryAll(project);
        return new Resp.Builder<List<Project>>().setData(projects).total(projects.size()).ok();
    }

    @Override
    public Resp<String> addProject(Project project) {
        try {
            permissionService.hasPermission(OneConstant.PERMISSION.PROJECT,
                    OneConstant.PERMISSION.ADD,project.getId());
            Result.verifyDoesExist(queryByTitle(project.getTitle()));
            project.setUserId(jwtUserService.getMasterId());
            return Result.addResult(projectDao.insert(project));
        }catch (BizException e){
            logger.error("class: ProjectServiceImpl#addProject,error []" + e.getMessage());
            return new Resp.Builder<String>().buildResult(e.getCode(),e.getMessage());
        }
    }

    @Override
    public Resp<String> updateProject(Project project) {
        try {
            permissionService.hasPermission(OneConstant.PERMISSION.PROJECT,
                    OneConstant.PERMISSION.EDIT,project.getId());
            Result.verifyDoesExist(queryByTitle(project.getTitle()));
            return Result.updateResult(projectDao.update(project));
        }catch (BizException e){
            logger.error("class: ProjectServiceImpl#updateProject,error []" + e.getMessage());
            return new Resp.Builder<String>().buildResult(e.getCode(),e.getMessage());
        }
    }

    @Override
    public Resp<String> deleteProject(String projectId) {
        try {
            permissionService.hasPermission(OneConstant.PERMISSION.PROJECT,
                    OneConstant.PERMISSION.DELETE,projectId);
            return Result.deleteResult(projectDao.deleteById(projectId));
        }catch (BizException e){
            logger.error("class: ProjectServiceImpl#deleteProject,error []" + e.getMessage());
            return new Resp.Builder<String>().buildResult(e.getCode(),e.getMessage());
        }
    }


    /**
     * 查询项目是否存在
     * @param title
     * @return
     */
    private Integer queryByTitle(String title){
        if (StringUtils.isEmpty(title)){
            return null;
        }
        return projectDao.queryByTitle(jwtUserService.getMasterId(),title);
    }


}
