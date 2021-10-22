package com.hu.oneclick.server.user;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.SysUser;
import com.hu.oneclick.model.domain.SysUserOrder;
import com.hu.oneclick.model.domain.SysUserOrderRecord;

import java.util.List;

/**
 * @author MaSiyi
 * @version 1.0.0 2021/10/9
 * @since JDK 1.8.0
 */
public interface UserOrderService {
    Resp<String> insertOrder(SysUserOrder sysUserOrder);

    Resp<String> insertUserDetail(SysUser sysUser);

    /** 查询付款方式
     * @Param: []
     * @return: com.hu.oneclick.model.base.Resp<java.lang.String>
     * @Author: MaSiyi
     * @Date: 2021/10/21
     */
    Resp<List<String>> getPaymentMethod();


    List<SysUserOrder> listOrder(String userId);
}
