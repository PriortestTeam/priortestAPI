package com.hu.oneclick.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hu.oneclick.model.domain.Feature;
import com.hu.oneclick.model.domain.dto.FeatureSaveDto;
import com.hu.oneclick.model.domain.param.FeatureParam;

import java.util.List;
import java.util.Map;

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

    /**
     * @Description: 模糊查询 故事标题
     * @Param: [title, projectId]
     * @return: java.util.Map<java.lang.String,java.lang.String>
     * @Author: Bruce
     * @Date: 2024/4/20
     */
    List<Map<String, String>> getFeatureByTitle(String title, Long projectId);
}
