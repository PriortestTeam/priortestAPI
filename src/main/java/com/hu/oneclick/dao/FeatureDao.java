package com.hu.oneclick.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hu.oneclick.model.annotation.Page;
import com.hu.oneclick.model.domain.Feature;
import com.hu.oneclick.model.domain.dto.LeftJoinDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author qingyang
 */
public interface FeatureDao extends BaseMapper<Feature> {

    Feature queryById(@Param("id") String id,@Param("masterId") String masterId);

    int update(Feature feature);

    List<LeftJoinDto> queryTitles(@Param("projectId")String projectId, @Param("title") String title, @Param("masterId") String masterId);

    @Page
    List<Feature> queryList(Feature feature);

    List<Feature> queryTitlesByTestCycleId(@Param("testCycleId")String testCycleId);

    List<Feature> findAllByFeature(Feature feature);
}
