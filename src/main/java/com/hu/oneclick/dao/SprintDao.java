package com.hu.oneclick.dao;

import com.hu.oneclick.model.domain.Sprint;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * @author qingyang
 */
public interface SprintDao extends BaseMapper<Sprint> {


    Sprint queryById(@Param("id") String id,@Param("masterId")String masterId);

    int update(Sprint sprint);

    List<Map<String,String>> queryTitles(@Param("projectId") String projectId,@Param("title") String title,@Param("masterId") String masterId);
}
