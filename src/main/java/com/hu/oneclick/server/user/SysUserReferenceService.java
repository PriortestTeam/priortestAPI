package com.hu.oneclick.server.user;

import com.hu.oneclick.model.entity.SysUser;

import java.util.Date;

/**
 * @author MaSiyi
 * @version 1.0.0 2021/10/15
 * @since JDK 1.8.0
 */
public interface SysUserReferenceService {
    /** 推荐人数
     * @Param: [sysUser]
     * @return: int
     * @Author: MaSiyi
     * @Date: 2021/10/15
     */
    int getReferenceTime(SysUser sysUser, Date startTime, Date endTime);
    /** 引用别人人数
     * @Param: [sysUser]
     * @return: int
     * @Author: MaSiyi
     * @Date: 2021/10/15
     */
    int getReferencePersonNo(SysUser sysUser, Date startTime, Date endTime);
}
