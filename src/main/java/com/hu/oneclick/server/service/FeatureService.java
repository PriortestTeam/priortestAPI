package com.hu.oneclick.server.service;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.Feature;
import com.hu.oneclick.model.domain.dto.LeftJoinDto;

import java.util.List;
import java.util.Map;

/**
 * @author qingyang
 */
public interface FeatureService {

    Resp< List<LeftJoinDto>> queryTitles(String projectId, String title);

    Resp<Feature> queryById(String id);

    Resp<List<Feature>> queryList(Feature feature);

    Resp<String> insert(Feature feature);

    Resp<String> update(Feature feature);

    Resp<String> closeUpdate(String id);

    Resp<String> delete(String id);




}
