package com.hu.oneclick.server.service;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.View;
import com.hu.oneclick.model.domain.dto.ViewScopeChildParams;
import com.hu.oneclick.model.domain.dto.ViewTreeDto;

import java.util.List;

/**
 * @author qingyang
 */
public interface ViewService {

    Resp<View> queryById(String id);


    Resp<List<View>> list(View view);


    Resp<String> queryDoesExistByTitle(String projectId,String title,String scope);

    Resp<String> addView(View view);

    Resp<String> updateView(View view);

    Resp<String> deleteView(String id);

    @Deprecated
    Resp<List<ViewScopeChildParams>> getViewScopeChildParams(String scope);


    Resp<List<View>> queryViewParents(String scope, String projectId);

    Resp<List<ViewTreeDto>> queryViewTrees(String scope);

    /** 添加视图
     * @Param: [view]
     * @return: com.hu.oneclick.model.base.Resp<java.lang.String>
     * @Author: MaSiyi
     * @Date: 2021/11/27
     */
    Resp<String> addViewRE(View view);

    /** 渲染视图
     * @Param: [viewId]
     * @return: com.hu.oneclick.model.base.Resp<java.lang.String>
     * @Author: MaSiyi
     * @Date: 2021/12/22
     */
    Resp<String> renderingView(String viewId);

    /** 获取filter字段
     * @Param: []
     * @return: com.hu.oneclick.model.base.Resp<java.lang.Object>
     * @Author: MaSiyi
     * @Date: 2021/12/23
     */
    Resp<Object> getViewFilter();

    /** 根据范围搜索所有字段
     * @Param: [scope]
     * @return: com.hu.oneclick.model.base.Resp<java.util.List<java.lang.Object>>
     * @Author: MaSiyi
     * @Date: 2021/12/29
     */
    Resp<List<Object>> getViewScope(String scope);
}
