package com.hu.oneclick.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hu.oneclick.model.entity.ProjectManage;
import com.hu.oneclick.model.domain.dto.ProjectManageSaveDto;
import com.hu.oneclick.model.param.ProjectManageParam;

import java.util.List;

/**
 * @Author: jhh
 * @Date: 2023/5/22
 */
public interface ProjectManageService extends IService<ProjectManage> {

    /**
     * 列表
     *
     * @param param
     * @return
     */
    List<ProjectManage> listAll(ProjectManageParam param);

    /**
     * 新增
     *
     * @param dto
     * @return
     */
    ProjectManage add(ProjectManageSaveDto dto);

    /**
     * 修改
     *
     * @param dto
     * @return
     */
    ProjectManage edit(ProjectManageSaveDto dto);

    /**
     * 详情
     *
     * @param id
     * @return
     */
    ProjectManage info(Long id);

    /**
     * 克隆
     *
     * @param ids
     */
    void clone(List<Long> ids);

    void delete(Long[] id);
}
