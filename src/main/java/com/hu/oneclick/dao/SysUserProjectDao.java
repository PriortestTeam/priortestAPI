package com.hu.oneclick.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hu.oneclick.model.entity.SysUserProject;

import java.util.Map;

public interface SysUserProjectDao extends BaseMapper<SysUserProject> {
    Map<String, Object> queryUserDefaultProject(String userId);
}
