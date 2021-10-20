package com.hu.oneclick.server.user;

import com.hu.oneclick.model.domain.SysUserOrderRecord;

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
}
