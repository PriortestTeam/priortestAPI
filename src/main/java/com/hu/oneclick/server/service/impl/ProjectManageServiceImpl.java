package com.hu.oneclick.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hu.oneclick.common.exception.BaseException;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.dao.ProjectManageDao;
import com.hu.oneclick.model.entity.ProjectManage;
import com.hu.oneclick.model.domain.dto.ProjectManageSaveDto;
import com.hu.oneclick.model.param.ProjectManageParam;
import com.hu.oneclick.server.service.ProjectManageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: jhh
 * @Date: 2023/5/22
 */
@Service
@Slf4j
public class ProjectManageServiceImpl extends ServiceImpl<ProjectManageDao, ProjectManage> implements ProjectManageService {

    private final JwtUserServiceImpl jwtUserService;

    public ProjectManageServiceImpl(JwtUserServiceImpl jwtUserService) {
        this.jwtUserService = jwtUserService;
    }


    @Override
    public List<ProjectManage> listAll(ProjectManageParam param) {
        param.setRoomId(jwtUserService.getUserLoginInfo().getSysUser().getRoomId());
        return this.list(param.getQueryCondition());
    }

    @Override
    public ProjectManage add(ProjectManageSaveDto dto) {
        ProjectManage projectManage = new ProjectManage();
        BeanUtil.copyProperties(dto, projectManage);
        projectManage.setRoomId(jwtUserService.getUserLoginInfo().getSysUser().getRoomId());
        // 保存自定义字段
        if (!JSONUtil.isNull(dto.getCustomFieldDatas())) {
            projectManage.setProjectExpand(JSONUtil.toJsonStr(dto.getCustomFieldDatas()));
        }
        this.baseMapper.insert(projectManage);
        return projectManage;
    }

    @Override
    public ProjectManage edit(ProjectManageSaveDto dto) {
        ProjectManage projectManage = new ProjectManage();
        BeanUtil.copyProperties(dto, projectManage);
        projectManage.setRoomId(jwtUserService.getUserLoginInfo().getSysUser().getRoomId());
        // 保存自定义字段
        if (!JSONUtil.isNull(dto.getCustomFieldDatas())) {
            projectManage.setProjectExpand(JSONUtil.toJsonStr(dto.getCustomFieldDatas()));
        }
        this.baseMapper.updateById(projectManage);
        return projectManage;
    }

    @Override
    public ProjectManage info(Long id) {
        ProjectManage projectManage = baseMapper.selectById(id);
        if (projectManage == null) {
            throw new BaseException(StrUtil.format("项目查询不到。ID：{}", id));
        }
        return projectManage;
    }

    @Override
    public void clone(List<Long> ids) {
        List<ProjectManage> projectManageList = new ArrayList<>();
        for (Long id : ids) {
            ProjectManage projectManage = baseMapper.selectById(id);
            if (projectManage == null) {
                throw new BaseException(StrUtil.format("故事查询不到。ID：{}", id));
            }
            ProjectManage projectManageClone = new ProjectManage();
            BeanUtil.copyProperties(projectManage, projectManageClone);
            projectManageClone.setId(null);
            projectManageList.add(projectManageClone);
        }
        // 批量克隆
        this.saveBatch(projectManageList);
    }
}
