package com.hu.oneclick.dao;

import com.hu.oneclick.model.domain.RoleFunction;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.BaseMapper;

public interface RoleFunctionDao extends BaseMapper<RoleFunction> {


    RoleFunction queryByRoleId(@Param("roleId") int roleId);

}
