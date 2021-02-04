package com.hu.oneclick.dao;

import com.hu.oneclick.dao.sql.FeatureSql;
import com.hu.oneclick.model.domain.Feature;
import org.apache.ibatis.annotations.UpdateProvider;
import tk.mybatis.mapper.common.BaseMapper;

/**
 * @author qingyang
 */
public interface FeatureDao extends BaseMapper<Feature> {


    Feature queryById(String id, String masterId);

    @UpdateProvider(type = FeatureSql.class,method = "update")
    int update(Feature feature);
}
