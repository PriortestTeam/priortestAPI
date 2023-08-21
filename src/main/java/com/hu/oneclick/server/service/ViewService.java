package com.hu.oneclick.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.View;
import com.hu.oneclick.model.domain.dto.ViewScopeChildParams;
import com.hu.oneclick.model.domain.dto.ViewTreeDto;

import java.util.List;
import java.util.Map;

/**
 * @author qingyang
 */
public interface ViewService extends IService<View> {

    Resp<View> queryById(String id);


    Resp<List<View>> list(View view);


    Resp<String> queryDoesExistByTitle(String projectId, String title, String scope);

    @Deprecated
    Resp<String> addView(View view);

    View updateView(View view);

    Resp<String> deleteView(String id);

    @Deprecated
    Resp<List<ViewScopeChildParams>> getViewScopeChildParams(String scope);


    Resp<List<View>> queryViewParents(String scope, String projectId);

    Resp<List<ViewTreeDto>> queryViewTrees(String scope);

    /**
     * 添加视图
     *
     * @return
     * @Param: [view]
     * @Author: MaSiyi
     * @Date: 2021/11/27
     */
    View addViewRE(View view);

    /**
     * 渲染视图
     *
     * @Param: [viewId]
     * @return: com.hu.oneclick.model.base.Resp<java.lang.String>
     * @Author: MaSiyi
     * @Date: 2021/12/22
     */
    Resp<Object> renderingView(String viewId) throws Exception;

    /**
     * 获取filter字段
     *
     * @Param: []
     * @return: com.hu.oneclick.model.base.Resp<java.lang.Object>
     * @Author: MaSiyi
     * @Date: 2021/12/23
     */
    Resp<Object> getViewFilter();

    /**
     * 根据范围搜索所有字段
     *
     * @Param: [scope]
     * @return: com.hu.oneclick.model.base.Resp<java.util.List < java.lang.Object>>
     * @Author: MaSiyi
     * @Date: 2021/12/29
     */
    Resp<Map<String, Object>> getViewScope(String scope);
}
