package com.hu.oneclick.server.service.impl;

import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.dao.ProjectSignOffDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.ProjectSignOff;
import com.hu.oneclick.model.domain.SysUser;
import com.hu.oneclick.model.domain.dto.AuthLoginUser;
import com.hu.oneclick.server.service.ProjectSignOffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author MaSiyi
 * @version 1.0.0 2022/1/15
 * @since JDK 1.8.0
 */
@Service
public class ProjectSignOffServiceImpl implements ProjectSignOffService {

    @Autowired
    private ProjectSignOffDao projectSignOffDao;
    @Autowired
    private JwtUserServiceImpl jwtUserService;


    @Override
    public Resp< List<ProjectSignOff>> getPdf() {
        AuthLoginUser userLoginInfo = jwtUserService.getUserLoginInfo();
        SysUser sysUser = userLoginInfo.getSysUser();
        String projectId = sysUser.getUserUseOpenProject().getProjectId();

        List<ProjectSignOff> projectSignOffs = projectSignOffDao.selectByUserProject(sysUser.getId(), projectId);
        return new Resp.Builder<List<ProjectSignOff>>().setData(projectSignOffs).ok();
    }
}
