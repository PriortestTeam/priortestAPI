package com.hu.oneclick.dao;

import com.hu.oneclick.model.domain.SysConfig;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.BaseMapper;

import java.util.List;

/**
 * @ClassName SysConfigDao.java
 * @Description
 * @Author Vince
 * @CreateTime 2022年12月24日 18:31:00
 */
public interface SysConfigDao extends BaseMapper<SysConfig> {

    List<SysConfig> selectByGroup(@Param("scope") String scope);
}
