package com.hu.oneclick.server.service;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.entity.SysUser;
import com.hu.oneclick.model.domain.dto.SysOrderDiscountDto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * @author MaSiyi
 * @version 1.0.0 2021/10/14
 * @since JDK 1.8.0
 */
public interface SysOrderDiscountService {

    /** 计算折扣
     * @Param: [sysOrderDiscountDto]
     * @return: com.hu.oneclick.model.base.Resp<java.lang.String>
     * @Author: MaSiyi
     * @Date: 2021/10/14
     */
    Resp<Map<String, BigDecimal>> calculateOrderPrice(SysOrderDiscountDto sysOrderDiscountDto);

    /** 计算推荐折扣
     * @Param:
     * @return:
     * @Author: MaSiyi
     * @Date: 2021/10/22
     * @param sysUser
     * @param date
     * @param date1
     */
    BigDecimal getReferenceDiscount(SysUser sysUser, Date date, Date date1);
}
