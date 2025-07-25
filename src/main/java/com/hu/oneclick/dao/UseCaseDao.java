
package com.hu.oneclick.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hu.oneclick.model.entity.UseCase;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * UseCase数据访问层
 */
public interface UseCaseDao extends BaseMapper<UseCase> {
    
    /**
     * 根据feature ID列表查询所有use case
     */
    List<UseCase> selectByFeatureIds(@Param("featureIds") List<Long> featureIds);
    
    /**
     * 统计指定feature下的use case数量
     */
    int countByFeatureId(@Param("featureId") Long featureId);
}
