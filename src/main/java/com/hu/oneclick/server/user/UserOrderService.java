package com.hu.oneclick.server.user;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.SysUser;
import com.hu.oneclick.model.domain.SysUserOrder;

/**
 * @author MaSiyi
 * @version 1.0.0 2021/10/9
 * @since JDK 1.8.0
 */
public interface UserOrderService {
    Resp<String> insertOrder(SysUserOrder sysUserOrder);

    Resp<String> insertUserDetail(SysUser sysUser);
}
