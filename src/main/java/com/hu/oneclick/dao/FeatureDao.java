package com.hu.oneclick.dao;

import com.hu.oneclick.model.domain.Feature;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * @author qingyang
 */
public interface FeatureDao extends BaseMapper<Feature> {

    Feature queryById(@Param("id") String id,@Param("masterId") String masterId);

    int update(Feature feature);

    List<Map<String, String>> queryTitles(@Param("projectId")String projectId,@Param("title") String title, @Param("masterId") String masterId);

}
