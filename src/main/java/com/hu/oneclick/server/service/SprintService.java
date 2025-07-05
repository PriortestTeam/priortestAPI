package com.hu.oneclick.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.hu.oneclick.model.entity.Sprint;
import com.hu.oneclick.model.domain.dto.SprintSaveDto;
import com.hu.oneclick.model.param.SprintParam;

import java.util.List;

/**
 * @author qingyang
 */
public interface SprintService extends IService<Sprint> {


    /**
     * 列表
     *
     * @param param
     * @return
     */
    List<Sprint> list(SprintParam param);

    /**
     * 新增
     *
     * @param dto
     * @return
     */
    Sprint add(SprintSaveDto dto);

    /**
     * 修改
     *
     * @param dto
     * @return
     */
    Sprint edit(SprintSaveDto dto);

    Sprint getByIdAndProjectId(Long id, Long projectId);

    /**
     * 详情
     *
     * @param id
     * @return
     */
    Sprint info(Long id);

    void clone(List<Long> ids);

    /**
     * 第一种参数类型：普通列表查询
     * @param param
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageInfo<Sprint> listWithViewFilter(SprintParam param, int pageNum, int pageSize);

    /**
     * 第二种参数类型：视图过滤查询
     * @param viewId
     * @param projectId
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageInfo<Sprint> listWithBeanSearcher(String viewId, String projectId, int pageNum, int pageSize);

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
    PageInfo<Sprint> queryByFieldAndValue(String fieldNameEn, String value, String scopeName, String scopeId, int pageNum, int pageSize);
}
