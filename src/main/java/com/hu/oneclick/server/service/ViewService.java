package com.hu.oneclick.server.service;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.View;

import java.util.List;

/**
 * @author qingyang
 */
public interface ViewService {

    Resp<List<View>> list(View view);

    Resp<String> queryDoesExistByTitle(String projectId,String title,String scope);

    Resp<String> addView(View view);

    Resp<String> updateView(View view);

    Resp<String> deleteView(String id);

}
