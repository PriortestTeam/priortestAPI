package com.hu.oneclick.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hu.oneclick.model.domain.RoleFunction;
import org.apache.ibatis.annotations.Param;

public interface RoleFunctionDao extends BaseMapper<RoleFunction> {


    RoleFunction queryByRoleId(@Param("roleId") int roleId);

}
