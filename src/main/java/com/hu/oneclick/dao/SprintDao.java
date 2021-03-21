package com.hu.oneclick.dao;

import com.hu.oneclick.model.domain.Feature;
import com.hu.oneclick.model.domain.Sprint;
import com.hu.oneclick.model.domain.dto.LeftJoinDto;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.BaseMapper;

import java.util.List;

/**
 * @author qingyang
 */
public interface SprintDao extends BaseMapper<Sprint> {


    Sprint queryById(@Param("id") String id,@Param("masterId")String masterId);

    int update(Sprint sprint);

    List<LeftJoinDto> queryTitles(@Param("projectId") String projectId, @Param("title") String title, @Param("masterId") String masterId);

    List<Sprint> queryList(Sprint sprint);

    List<Sprint> queryTitlesInFeatureId(@Param("features") List<Feature> features);

}
