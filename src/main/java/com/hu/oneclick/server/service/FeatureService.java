package com.hu.oneclick.server.service;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.Feature;
import com.hu.oneclick.model.domain.FeatureJoinSprint;
import com.hu.oneclick.model.domain.Sprint;
import com.hu.oneclick.model.domain.dto.LeftJoinDto;

import java.util.List;

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


    Resp<List<Sprint>> queryBindSprints(String featureId);

    Resp<String> bindSprintInsert(FeatureJoinSprint featureJoinSprint);

    Resp<String> bindSprintDelete(String sprint,String featureId);

}
