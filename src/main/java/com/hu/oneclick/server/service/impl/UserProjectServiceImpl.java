package com.hu.oneclick.server.service.impl;


import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.dao.ProjectDao;
import com.hu.oneclick.dao.SubUserProjectDao;
import com.hu.oneclick.dao.SysUserProjectDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.dto.LeftJoinDto;
import com.hu.oneclick.model.entity.SubUserProject;
import com.hu.oneclick.model.entity.SysUser;
import com.hu.oneclick.server.service.UserProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service


public class UserProjectServiceImpl implements UserProjectService {
    @Autowired
    SubUserProjectDao subUserProjectDao;
    @Autowired
    JwtUserServiceImpl jwtUserService;
    @Autowired
    ProjectDao projectDao;
    @Autowired
    SysUserProjectDao sysUserProjectDao;

    @Override
    public Resp<List&lt;LeftJoinDto>> getUserByProject() {
        SysUser sysUser = jwtUserService.getUserLoginInfo().getSysUser();
        String userId = sysUser.getId();
        SubUserProject userProject = subUserProjectDao.queryByUserId(userId);
        String projectIds = userProject.getProjectId();
        // 通过projectIds 获取项目标题
        List&lt;String> projectIdList = Arrays.asList(projectIds.split(",");
        List&lt;LeftJoinDto> select = projectDao.queryTitleByIds(projectIdList);
        return new Resp.Builder<List&lt;LeftJoinDto>>().setData(select).ok();
    }

    @Override
    public List&lt;Map&lt;String, Object>> getUserProject() {
        SysUser sysUser = jwtUserService.getUserLoginInfo().getSysUser();

        List&lt;Map&lt;String, Object>> maps = sysUserProjectDao.queryProjectByUserId(new BigInteger(sysUser.getId();
        return maps.stream().map(map -> {
            Map&lt;String, Object> newmap = new HashMap&lt;>();
            newmap.put("title", map.get("title");
            newmap.put("projectId", map.get("project_id");
            return newmap;
        }).collect(Collectors.toList();
    }
}
}
}
