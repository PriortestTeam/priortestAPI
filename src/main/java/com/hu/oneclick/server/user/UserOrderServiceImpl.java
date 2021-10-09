package com.hu.oneclick.server.user;

import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.dao.SysUserOrderDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.SysUserOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author MaSiyi
 * @version 1.0.0 2021/10/9
 * @since JDK 1.8.0
 */
@Service
public class UserOrderServiceImpl implements UserOrderService {

    @Autowired
    private SysUserOrderDao sysUserOrderDao;

    @Override
    public Resp<String> insertOrder(SysUserOrder sysUserOrder) {
        if (sysUserOrderDao.insertSelective(sysUserOrder) > 0) {
            return new Resp.Builder<String>().buildResult(SysConstantEnum.ADD_SUCCESS.getCode(), SysConstantEnum.ADD_SUCCESS.getValue());
        }
        return new Resp.Builder<String>().buildResult(SysConstantEnum.ADD_FAILED.getCode(), SysConstantEnum.ADD_FAILED.getValue());

    }
}
