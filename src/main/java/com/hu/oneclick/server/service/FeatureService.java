package com.hu.oneclick.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hu.oneclick.model.domain.Feature;
import com.hu.oneclick.model.domain.dto.FeatureSaveDto;
import com.hu.oneclick.model.domain.param.FeatureParam;

import java.util.List;

/**
 * @author qingyang
 */
public interface FeatureService extends IService<Feature> {

    /**
     * 列表
     *
     * @param param
     * @return
     */
    List<Feature> list(FeatureParam param);

    /**
     * 新增
     *
     * @param dto
     * @return
     */
    Feature add(FeatureSaveDto dto);

    /**
     * 修改
     *
     * @param dto
     * @return
     */
    Feature edit(FeatureSaveDto dto);

    Feature getByIdAndProjectId(Long id, Long projectId);

    /**
     * 详情
     *
     * @param id
     * @return
     */
    Feature info(Long id);

    void clone(List<Long> ids);
}
