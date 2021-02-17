package com.hu.oneclick.server.service;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.Sprint;

import java.util.List;
import java.util.Map;

/**
 * @author qingyang
 */
public interface SprintService {

    Resp< List<Map<String,String>>> queryTitles(String projectId,String title);

    Resp<Sprint> queryById(String id);

    Resp<List<Sprint>> queryList(Sprint sprint);

    Resp<String> insert(Sprint sprint);

    Resp<String> update(Sprint sprint);

    Resp<String> delete(String id);


}
