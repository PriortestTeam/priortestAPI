
package com.hu.oneclick.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hu.oneclick.model.entity.VersionMapping;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface VersionMappingDao extends BaseMapper<VersionMapping> {
    
    /**
     * 根据发布版本ID查询所有映射关系
     */
    List<VersionMapping> selectByReleaseId(@Param("releaseId") Long releaseId);
    
    /**
     * 根据项目ID和发布版本查询映射关系
     */
    List<VersionMapping> selectByProjectAndRelease(@Param("projectId") Long projectId, @Param("releaseVersion") String releaseVersion);
}
