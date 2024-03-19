package com.hu.oneclick.relation.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hu.oneclick.relation.domain.Relation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RelationDao extends BaseMapper<Relation> {

    List<Relation> getRelationListByObjectIdAndTargetIdAndCategory(@Param("id") Long testCaseId,
                                                                   @Param("categorys") String[] categorys);

}
