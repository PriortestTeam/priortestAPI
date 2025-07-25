
package com.hu.oneclick.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hu.oneclick.model.domain.dto.UserCaseDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * UseCase数据访问层
 */
public interface UseCaseDao extends BaseMapper<UserCaseDto> {
    
    /**
     * 根据feature ID列表查询所有use case
     */
    List<UserCaseDto> selectByFeatureIds(@Param("featureIds") List<Long> featureIds);
    
    /**
     * 统计指定feature下的use case数量
     */
    int countByFeatureId(@Param("featureId") Long featureId);
    
    /**
     * 根据项目ID和版本列表查询UseCase
     */
    List<UserCaseDto> selectByProjectIdAndVersions(@Param("projectId") Long projectId, 
                                                   @Param("versions") List<String> versions);
    
    /**
     * 查询UseCase并排除指定的Feature
     */
    List<UserCaseDto> selectByProjectIdAndVersionsExcludeFeatures(@Param("projectId") Long projectId,
                                                                  @Param("versions") List<String> versions,
                                                                  @Param("excludeFeatureIds") List<Long> excludeFeatureIds);
}
