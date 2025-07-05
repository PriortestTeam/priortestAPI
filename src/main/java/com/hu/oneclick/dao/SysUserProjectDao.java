package com.hu.oneclick.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hu.oneclick.model.entity.SysUserProject;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public interface SysUserProjectDao extends BaseMapper<SysUserProject> {
    Map<String, Object> queryUserDefaultProject(BigInteger userId);

    List<Map<String, Object>> queryProjectByUserId(BigInteger userId);

    List<Map<String, Object>> queryProjectWithUsers(List<BigInteger> userIds);
}
