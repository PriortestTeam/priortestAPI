
package com.hu.oneclick.dao;

import com.hu.oneclick.model.entity.Feature;
import com.hu.oneclick.model.entity.FeatureJoinSprint;
import com.hu.oneclick.model.entity.Sprint;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * (FeatureJoinSprint)表数据库访问层
 *
 * @author makejava
 * @since 2021-03-16 09:53:29
 */
public interface FeatureJoinSprintDao {

    List<FeatureJoinSprint> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);

    List<FeatureJoinSprint> queryAll(FeatureJoinSprint featureJoinSprint);

    FeatureJoinSprint queryById(Object id);

    int insert(FeatureJoinSprint featureJoinSprint);

    int insertBatch(@Param("entities") List<FeatureJoinSprint> entities);

    int insertOrUpdateBatch(@Param("entities") List<FeatureJoinSprint> entities);

    int update(FeatureJoinSprint featureJoinSprint);

    int deleteById(Object id);

    List<Feature> queryBySprintId(@Param("sprintId") String sprintId);

    List<Sprint> querySprintByFeatureId(@Param("featureId") String featureId);
}
