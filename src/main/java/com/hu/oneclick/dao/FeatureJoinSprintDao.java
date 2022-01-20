package com.hu.oneclick.dao;

import com.hu.oneclick.model.domain.Feature;
import com.hu.oneclick.model.domain.FeatureJoinSprint;
import com.hu.oneclick.model.domain.Sprint;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * (FeatureJoinSprint)表数据库访问层
 *
 * @author makejava
 * @since 2021-03-16 09:53:29
 */
public interface FeatureJoinSprintDao {


    /**
     * 通过实体作为筛选条件查询
     *
     * @param featureJoinSprint 实例对象
     * @return 对象列表
     */
    List<FeatureJoinSprint> queryAll(FeatureJoinSprint featureJoinSprint);

    /**
     * 新增数据
     *
     * @param featureJoinSprint 实例对象
     * @return 影响行数
     */
    int insert(FeatureJoinSprint featureJoinSprint);
    int inserts(@Param("featureJoinSprint")List<FeatureJoinSprint> featureJoinSprint);
    /**
     * 修改数据
     *
     * @param featureJoinSprint 实例对象
     * @return 影响行数
     */
    int update(FeatureJoinSprint featureJoinSprint);

    /**
     * 通过主键删除数据
     *
     * @param 主键
     * @return 影响行数
     */
    int deleteById(@Param("featureId") String featureId,@Param("sprint") String sprint);
    int deleteByFeatureId(@Param("featureId") String featureId);

    int verifyIsExist(FeatureJoinSprint featureJoinSprint);

    List<Sprint> queryBindSprints(@Param("featureId") String featureId);

    List<Feature> findAllByFeature(Feature feature);
}
