package com.hu.oneclick.server.service;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.RoleFunction;

public interface RoleFunctionService {

    Resp<RoleFunction> queryByRoleId(int roleId);



}
