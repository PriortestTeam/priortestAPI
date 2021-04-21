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

    Resp<List<ViewScopeChildParams>> getViewScopeChildParams(String scope);

    Resp<List<View>> queryViewParents(String scope, String viewTitle);

    Resp<List<ViewTreeDto>> queryViewTrees(String scope);
}
