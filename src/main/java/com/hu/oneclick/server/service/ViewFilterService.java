package com.hu.oneclick.server.service;

import com.hu.oneclick.model.domain.dto.ViewTreeDto;
import com.hu.oneclick.model.entity.OneFilter;

import java.util.List;
import java.util.Map;

/**
 * 视图过滤服务接口
 * 用于处理基于视图的复杂查询过滤逻辑
 *
 * @author xiaohai
 * @date 2025/06/28
 */
public interface ViewFilterService {

    /**
     * 根据视图ID获取过滤条件
     *
     * @param viewId 视图ID
     * @param projectId 项目ID
     * @return 过滤条件Map
     */
    Map<String, Object> getFilterParamsByViewId(String viewId, String projectId);

    /**
     * 根据视图树DTO获取过滤条件
     *
     * @param viewTreeDto 视图树DTO
     * @param projectId 项目ID
     * @return 过滤条件Map
     */
    Map<String, Object> getFilterParamsByViewTreeDto(ViewTreeDto viewTreeDto, String projectId);

    /**
     * 处理所有过滤条件（包括父视图）
     *
     * @param view 视图对象
     * @return 过滤条件列表
     */
    List<List<OneFilter>> processAllFilters(com.hu.oneclick.model.entity.View view);

    /**
     * 构建查询参数
     *
     * @param filterList 过滤条件列表
     * @param projectId 项目ID
     * @return 查询参数Map
     */
    Map<String, Object> buildQueryParams(List<List<OneFilter>> filterList, String projectId);

    /**
     * 检查是否需要应用视图过滤
     *
     * @param viewTreeDto 视图树DTO
     * @return 是否需要过滤
     */
    boolean shouldApplyViewFilter(ViewTreeDto viewTreeDto);

    /**
     * 检查是否需要应用视图过滤（基于viewId）
     *
     * @param viewId 视图ID
     * @return 是否需要过滤
     */
    boolean shouldApplyViewFilter(String viewId);
} 