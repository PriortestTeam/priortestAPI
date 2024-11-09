package com.hu.oneclick.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hu.oneclick.model.entity.Feature;
import com.hu.oneclick.model.entity.Sprint;
import com.hu.oneclick.model.domain.dto.LeftJoinDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author qingyang
 */
public interface SprintDao extends BaseMapper<Sprint> {


    Sprint queryById(@Param("id") String id,@Param("masterId")String masterId);

    int updateByPrimaryKeySelective(Sprint sprint);

    List<LeftJoinDto> queryTitles(@Param("projectId") String projectId, @Param("title") String title, @Param("masterId") String masterId);

    List<Sprint> queryList(Sprint sprint);

    List<Sprint> queryTitlesInFeatureId(@Param("features") List<Feature> features);

    List<Sprint> querySprintList(@Param("title") String title,@Param("projectId") String projectId);
}
