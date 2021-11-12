package com.hu.oneclick.dao;

import com.hu.oneclick.model.domain.SysUserToken;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysUserTokenDao {
    int deleteByPrimaryKey(Integer id);

    int insert(SysUserToken record);

    int insertSelective(SysUserToken record);

    SysUserToken selectByPrimaryKey(Integer id);

    List<SysUserToken> selectByUserId(String userId);

    int updateByPrimaryKeySelective(SysUserToken record);

    int updateByPrimaryKey(SysUserToken record);

    void decreaseApiTimes(Integer id);

    SysUserToken selectByTokenValue(String tokenVale);
}