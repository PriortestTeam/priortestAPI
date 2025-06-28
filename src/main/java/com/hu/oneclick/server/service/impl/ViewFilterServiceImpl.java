package com.hu.oneclick.server.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.hu.oneclick.model.domain.dto.ViewTreeDto;
import com.hu.oneclick.model.entity.OneFilter;
import com.hu.oneclick.model.entity.View;
import com.hu.oneclick.server.service.ViewFilterService;
import com.hu.oneclick.server.service.ViewService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 视图过滤服务实现类
 * 用于处理基于视图的复杂查询过滤逻辑
 *
 * @author xiaohai
 * @date 2025/06/28
 */
@Service
@Slf4j
public class ViewFilterServiceImpl implements ViewFilterService {

    @Resource
    private ViewService viewService;

    @Override
    public Map<String, Object> getFilterParamsByViewId(String viewId, String projectId) {
        if (StrUtil.isBlank(viewId)) {
            return null;
        }

        try {
            View view = viewService.getById(viewId);
            if (view == null) {
                log.warn("视图不存在，viewId: {}", viewId);
                return null;
            }

            List<List<OneFilter>> filterList = processAllFilters(view);
            return buildQueryParams(filterList, projectId);
        } catch (Exception e) {
            log.error("获取视图过滤参数失败，viewId: {}, projectId: {}", viewId, projectId, e);
            return null;
        }
    }

    @Override
    public Map<String, Object> getFilterParamsByViewTreeDto(ViewTreeDto viewTreeDto, String projectId) {
        if (!shouldApplyViewFilter(viewTreeDto)) {
            return null;
        }

        try {
            View view = viewService.getById(viewTreeDto.getId().toString());
            if (view == null) {
                log.warn("视图不存在，viewId: {}", viewTreeDto.getId());
                return null;
            }

            List<List<OneFilter>> filterList = processAllFilters(view);
            return buildQueryParams(filterList, projectId);
        } catch (Exception e) {
            log.error("获取视图过滤参数失败，viewTreeDto: {}, projectId: {}", viewTreeDto, projectId, e);
            return null;
        }
    }

    @Override
    public List<List<OneFilter>> processAllFilters(View view) {
        List<List<OneFilter>> filterList = new ArrayList<>();
        processAllFilterRecursive(view, filterList);
        return filterList;
    }

    @Override
    public Map<String, Object> buildQueryParams(List<List<OneFilter>> filterList, String projectId) {
        if (CollUtil.isEmpty(filterList)) {
            return null;
        }

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("P0.projectId", projectId);
        params.put("P0.projectId-op", "eq");

        // 参数增加逻辑关系
        StringBuilder gexpr = new StringBuilder();
        gexpr.append("P0");

        int j = 0;
        for (List<OneFilter> oneFilters : filterList) {
            if (CollUtil.isEmpty(oneFilters)) {
                continue;
            }
            
            gexpr.append("&(");
            for (int i = 0; i < oneFilters.size(); i++) {
                OneFilter filter = oneFilters.get(i);
                if (filter == null) {
                    continue;
                }
                
                String fieldName = StrUtil.format("A_{}_{}", j, i);
                params.put(StrUtil.format("{}.{}", fieldName, filter.getFieldNameEn()), filter.getSourceVal());
                params.put(StrUtil.format("{}.{}-op", fieldName, filter.getFieldNameEn()), filter.getCondition());
                
                if (i == 0) {
                    gexpr.append(fieldName);
                } else {
                    gexpr.append(StrUtil.equals(filter.getAndOr(), "and") ? "&" : "|");
                    gexpr.append(fieldName);
                }
            }
            gexpr.append(")");
            j++;
        }
        
        params.put("gexpr", gexpr.toString());
        return params;
    }

    @Override
    public boolean shouldApplyViewFilter(ViewTreeDto viewTreeDto) {
        return viewTreeDto != null && viewTreeDto.getId() != null && StrUtil.isNotBlank(viewTreeDto.getId().toString());
    }

    @Override
    public boolean shouldApplyViewFilter(String viewId) {
        return StrUtil.isNotBlank(viewId);
    }

    /**
     * 递归处理所有过滤条件（包括父视图）
     */
    private void processAllFilterRecursive(View view, List<List<OneFilter>> filterList) {
        if (view == null) {
            return;
        }

        // 如果有父视图，先处理父视图
        if (StringUtils.isNotEmpty(view.getParentId()) && view.getLevel() > 0) {
            View parentView = viewService.getById(view.getParentId());
            processAllFilterRecursive(parentView, filterList);
        }

        // 添加当前视图的过滤条件
        List<OneFilter> oneFilters = view.getOneFilters();
        if (CollUtil.isNotEmpty(oneFilters)) {
            filterList.add(oneFilters);
        }
    }
} 