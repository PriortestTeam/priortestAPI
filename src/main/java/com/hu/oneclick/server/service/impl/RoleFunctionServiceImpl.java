package com.hu.oneclick.server.service.impl;

import com.hu.oneclick.dao.RoleFunctionDao;
import com.hu.oneclick.model.base.Resp;

import com.hu.oneclick.model.entity.RoleFunction;
import com.hu.oneclick.server.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service


public class RoleFunctionServiceImpl implements RoleFunctionService {

    private final static Logger logger = LoggerFactory.getLogger(RoleFunctionServiceImpl.class);

    private final RoleFunctionDao roleFunctionDao;



    public RoleFunctionServiceImpl(RoleFunctionDao roleFunctionDao) {
        this.roleFunctionDao = roleFunctionDao;
    }



    @Override
    public Resp<RoleFunction> queryByRoleId(int roleId) {
        RoleFunction roleFunction = roleFunctionDao.queryByRoleId(roleId);
        return new Resp.Builder<RoleFunction>().setData(roleFunction).ok();
    }


}
}
}
