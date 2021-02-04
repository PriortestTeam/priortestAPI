package com.hu.oneclick.server.service;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.Feature;

import java.util.List;

/**
 * @author qingyang
 */
public interface FeatureService {


    Resp<Feature> queryById(String id);

    Resp<List<Feature>> queryList(Feature feature);

    Resp<String> insert(Feature feature);

    Resp<String> update(Feature feature);

    Resp<String> delete(String id);



}
