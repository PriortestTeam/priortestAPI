package com.hu.oneclick.server.service.impl;


import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.dao.ProjectDao;
import com.hu.oneclick.dao.SubUserProjectDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.entity.SubUserProject;
import com.hu.oneclick.model.entity.SysUser;
import com.hu.oneclick.model.domain.dto.LeftJoinDto;
import com.hu.oneclick.server.service.UserProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UserProjectServiceImpl implements UserProjectService {
    @Autowired
    SubUserProjectDao subUserProjectDao;
    @Autowired
    JwtUserServiceImpl jwtUserService;
    @Autowired
    ProjectDao projectDao;

    @Override
    public Resp<List<LeftJoinDto>> getUserByProject() {
        SysUser sysUser = jwtUserService.getUserLoginInfo().getSysUser();
        String userId = sysUser.getId();
        SubUserProject userProject = subUserProjectDao.queryByUserId(userId);
        String projectIds = userProject.getProjectId();
        // 通过projectIds 获取项目标题
        List<String> projectIdList = Arrays.asList(projectIds.split(","));
        List<LeftJoinDto> select = projectDao.queryTitleByIds(projectIdList);
        return new Resp.Builder<List<LeftJoinDto>>().setData(select).ok();
    }
}
