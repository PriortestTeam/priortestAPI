package com.hu.oneclick.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.hu.oneclick.model.entity.Feature;
import com.hu.oneclick.model.domain.dto.FeatureSaveDto;
import com.hu.oneclick.model.param.FeatureParam;

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

    /**
     * 第一种参数类型：普通列表查询
     * @param param
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageInfo<Feature> listWithViewFilter(FeatureParam param, int pageNum, int pageSize);

    /**
     * 第二种参数类型：视图过滤查询
     * @param viewId
     * @param projectId
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageInfo<Feature> listWithBeanSearcher(String viewId, String projectId, int pageNum, int pageSize);

    /**
     * 第三种参数类型：字段过滤查询
     * @param fieldNameEn
     * @param value
     * @param scopeName
     * @param scopeId
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageInfo<Feature> queryByFieldAndValue(String fieldNameEn, String value, String scopeName, String scopeId, int pageNum, int pageSize);
}
