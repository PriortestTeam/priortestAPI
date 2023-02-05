package com.hu.oneclick.dao;

import com.hu.oneclick.model.domain.SysFunction;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysFunctionDao {
    int deleteByPrimaryKey(Long id);

    int insert(SysFunction record);

    int insertSelective(SysFunction record);

    SysFunction selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SysFunction record);

    int updateByPrimaryKey(SysFunction record);

    List<SysFunction> getRoleFunction(String pNumber);

    List<SysFunction> findByIds(@Param("idList") List<Long> idList);

}