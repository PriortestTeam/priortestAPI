package com.hu.oneclick.dao;

import com.hu.oneclick.model.entity.SysUserBusiness;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysUserBusinessDao {
    int deleteByPrimaryKey(Long id);

    int deleteByUserId(String userId);

    int deleteByUserIdAndProjectId(String userId,String projectId);

    int insert(SysUserBusiness record);

    int insertSelective(SysUserBusiness record);

    SysUserBusiness selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SysUserBusiness record);

    int updateByPrimaryKey(SysUserBusiness record);

    List<SysUserBusiness> checkIsValueExist(String type, String keyId);

    Integer updateByExampleSelective(String keyId, String type, String btnStr);

    SysUserBusiness findByRoleIdAndProjectIdAndUserId(Long roleId, Long ProjectId, Long userId);
}
