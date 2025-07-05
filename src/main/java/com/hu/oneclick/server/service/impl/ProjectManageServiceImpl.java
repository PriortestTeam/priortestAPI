package com.hu.oneclick.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hu.oneclick.common.exception.BaseException;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.dao.ProjectManageDao;
import com.hu.oneclick.dao.SysUserProjectDao;
import com.hu.oneclick.model.entity.ProjectManage;
import com.hu.oneclick.model.domain.dto.ProjectManageSaveDto;
import com.hu.oneclick.model.entity.SysUserProject;
import com.hu.oneclick.model.param.ProjectManageParam;
import com.hu.oneclick.server.service.ProjectManageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: jhh
 * @Date: 2023/5/22
 */
@Service
@Slf4j


public class ProjectManageServiceImpl extends ServiceImpl<ProjectManageDao, ProjectManage> implements ProjectManageService {
    private final JwtUserServiceImpl jwtUserService;
    private final SysUserProjectDao sysUserProjectDao;

    public ProjectManageServiceImpl(JwtUserServiceImpl jwtUserService, SysUserProjectDao sysUserProjectDao) {
        this.jwtUserService = jwtUserService;
        this.sysUserProjectDao = sysUserProjectDao;
    }

    @Override
    public List&lt;ProjectManage> listAll(ProjectManageParam param) {
        param.setRoomId(jwtUserService.getUserLoginInfo().getSysUser().getRoomId();
        return this.list(param.getQueryCondition();
    }

    @Override
    public ProjectManage add(ProjectManageSaveDto dto) {
        ProjectManage projectManage = new ProjectManage();
        BeanUtil.copyProperties(dto, projectManage);
        projectManage.setRoomId(jwtUserService.getUserLoginInfo().getSysUser().getRoomId();
        // 保存自定义字段
        if (!JSONUtil.isNull(dto.getCustomFieldDatas() {
            projectManage.setProjectExpand(JSONUtil.toJsonStr(dto.getCustomFieldDatas();
        }
        this.baseMapper.insert(projectManage);
        return projectManage;
    }

    @Override
    public ProjectManage edit(ProjectManageSaveDto dto) {
        ProjectManage projectManage = new ProjectManage();
        BeanUtil.copyProperties(dto, projectManage);
        projectManage.setRoomId(jwtUserService.getUserLoginInfo().getSysUser().getRoomId();
        // 保存自定义字段
        if (!JSONUtil.isNull(dto.getCustomFieldDatas() {
            projectManage.setProjectExpand(JSONUtil.toJsonStr(dto.getCustomFieldDatas();
        }
        this.baseMapper.updateById(projectManage);
        return projectManage;
    }

    @Override
    public ProjectManage info(Long id) {
        ProjectManage projectManage = baseMapper.selectById(id);
        if (projectManage == null) {
            throw new BaseException(StrUtil.format("项目查询不到。ID：{}", id);
        }
        return projectManage;
    }

    @Override
    public void clone(List&lt;Long> ids) {
        List&lt;ProjectManage> projectManageList = new ArrayList&lt;>();
        for (Long id : ids) {
            ProjectManage projectManage = baseMapper.selectById(id);
            if (projectManage == null) {
                throw new BaseException(StrUtil.format("故事查询不到。ID：{}", id);
            }
            ProjectManage projectManageClone = new ProjectManage();
            BeanUtil.copyProperties(projectManage, projectManageClone);
            projectManageClone.setId(null);
            projectManageList.add(projectManageClone);
        }
        // 批量克隆
        this.saveBatch(projectManageList);
    }

    public void delete(Long[] ids) {
        QueryWrapper<SysUserProject> query = Wrappers.query();
        query.eq("is_default", 1).in("project_id", Arrays.asList(ids);
        Long count = sysUserProjectDao.selectCount(query);
        if (count > 0) {
            throw new BaseException("有项目被占用,暂时无法删除");
        }

        QueryWrapper<SysUserProject> query2 = Wrappers.query();
        query2.in("project_id", Arrays.asList(ids);
        sysUserProjectDao.delete(query2);

        this.removeByIds(Arrays.asList(ids);
    }
}
}
