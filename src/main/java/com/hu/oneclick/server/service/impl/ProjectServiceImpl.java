package com.hu.oneclick.server.service.impl;

import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.common.security.service.SysPermissionService;
import com.hu.oneclick.dao.ProjectDao;
import com.hu.oneclick.model.annotation.Page;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.base.Result;
import com.hu.oneclick.model.domain.Project;
import com.hu.oneclick.model.domain.SysUser;
import com.hu.oneclick.model.domain.UserUseOpenProject;
import com.hu.oneclick.server.service.ProjectService;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author qingyang
 */
@Service
public class ProjectServiceImpl implements ProjectService {

    private final static Logger logger = LoggerFactory.getLogger(ProjectServiceImpl.class);

    private final SysPermissionService sysPermissionService;

    private final JwtUserServiceImpl jwtUserService;

    private final ProjectDao projectDao;

    public ProjectServiceImpl(SysPermissionService sysPermissionService, JwtUserServiceImpl jwtUserService, ProjectDao projectDao, RedissonClient redisClient) {
        this.sysPermissionService = sysPermissionService;
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
        project.setUserId(jwtUserService.getMasterId());
        List<Project> projects = projectDao.queryAll(project);
        return new Resp.Builder<List<Project>>().setData(projects).total(projects.size()).ok();
    }

    @Override
    public Resp<String> addProject(Project project) {
        try {
            sysPermissionService.hasPermission(OneConstant.PERMISSION.PROJECT,
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
            sysPermissionService.hasPermission(OneConstant.PERMISSION.PROJECT,
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
            sysPermissionService.hasPermission(OneConstant.PERMISSION.PROJECT,
                    OneConstant.PERMISSION.DELETE,projectId);
            return Result.deleteResult(projectDao.deleteById(projectId));
        }catch (BizException e){
            logger.error("class: ProjectServiceImpl#deleteProject,error []" + e.getMessage());
            return new Resp.Builder<String>().buildResult(e.getCode(),e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> checkProject(String projectId) {
        int flag = 0;
        try {
            SysUser sysUser = jwtUserService.getUserLoginInfo().getSysUser();

            Project project = projectDao.queryById(projectId);

            if(project != null){
                UserUseOpenProject userUseOpenProject = new UserUseOpenProject();
                userUseOpenProject.setProjectId(projectId);
                userUseOpenProject.setUserId(sysUser.getId());
                userUseOpenProject.setTitle(project.getTitle());

                if (projectDao.deleteUseOpenProject(sysUser.getUserUseOpenProject().getId()) > 0
                        && projectDao.insertUseOpenProject(userUseOpenProject) > 0){
                    sysUser.setUserUseOpenProject(userUseOpenProject);
                    jwtUserService.saveUserLoginInfo2(sysUser);
                    flag = 1;
                }
            }
            return Result.updateResult(flag);
        }catch (BizException e){
            logger.error("class: ProjectServiceImpl#checkProject,error []" + e.getMessage());
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
        if(projectDao.queryByTitle(jwtUserService.getMasterId(),title) > 0){
            return 1;
        }
        return null;
    }


}
