package com.hu.oneclick.server.user;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.SysUserOrderRecord;

import java.util.Date;
import java.util.List;

/**
 * @author MaSiyi
 * @version 1.0.0 2021/10/20
 * @since JDK 1.8.0
 */
public interface SysUserOrderRecordService {

    /** 增
     * @Param: [sysUserOrderRecord]
     * @return: void
     * @Author: MaSiyi
     * @Date: 2021/10/20
     */
    void insert(SysUserOrderRecord sysUserOrderRecord);

    /** 付款
     * @Param: [id]
     * @return: com.hu.oneclick.model.base.Resp<java.lang.String>
     * @Author: MaSiyi
     * @Date: 2021/10/22
     */
    Resp<String> payment(String id);

    /** 获取用户订单详细
     * @Param: []
     * @return: com.hu.oneclick.model.base.Resp<java.util.List<java.lang.String>>
     * @Author: MaSiyi
     * @Date: 2021/10/22
     */
    Resp<List<SysUserOrderRecord>> getUserOrderRecord();
}
