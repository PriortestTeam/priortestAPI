
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
     * 查询主版本Features下的Use Cases，支持版本过滤（情况1专用）
     * @param featureIds Feature ID列表
     * @param majorVersion 主版本号
     * @param includeVersions 包含的版本列表（可为空）
     * @return 符合版本条件的Use Case列表
     */
    List<UserCaseDto> selectByFeatureIdsWithVersionFilter(
        @Param("featureIds") List<Long> featureIds,
        @Param("majorVersion") String majorVersion,
        @Param("includeVersions") List<String> includeVersions
    );
    
    /**
     * 查询指定版本的Use Cases，排除指定的Use Case IDs（情况3专用）
     * @param projectId 项目ID
     * @param includeVersions 包含的版本列表
     * @param excludeUseCaseIds 要排除的Use Case ID列表
     * @return Use Case列表
     */
    List<UserCaseDto> selectByVersionsExcludeIds(
        @Param("projectId") Long projectId,
        @Param("includeVersions") List<String> includeVersions,
        @Param("excludeUseCaseIds") List<Long> excludeUseCaseIds
    );
    
    /**
     * 查询Feature下版本不匹配的Use Cases（用于versionNotMatchedInfo）
     * @param featureIds Feature ID列表
     * @param majorVersion 主版本号
     * @param includeVersions 包含的版本列表（可为空）
     * @return 版本不匹配的Use Case列表
     */
    List<UserCaseDto> selectVersionNotMatchedByFeatureIds(
        @Param("featureIds") List<Long> featureIds,
        @Param("majorVersion") String majorVersion,
        @Param("includeVersions") List<String> includeVersions
    );eId(@Param("featureId") Long featureId);
}
