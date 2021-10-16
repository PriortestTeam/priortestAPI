package com.hu.oneclick.server.service.impl;

import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.dao.SysOrderDiscountDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.SysUser;
import com.hu.oneclick.model.domain.dto.AuthLoginUser;
import com.hu.oneclick.model.domain.dto.SysOrderDiscountDto;
import com.hu.oneclick.server.service.SysOrderDiscountService;
import com.hu.oneclick.server.service.SystemConfigService;
import com.hu.oneclick.server.user.SysUserReferenceService;
import org.redisson.misc.Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author MaSiyi
 * @version 1.0.0 2021/10/14
 * @since JDK 1.8.0
 */
@Service
public class SysOrderDiscountImpl implements SysOrderDiscountService {

    @Autowired
    private SysOrderDiscountDao sysOrderDiscountDao;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private JwtUserServiceImpl jwtUserService;
    @Autowired
    private SysUserReferenceService sysUserReferenceService;
    /**
     * 计算折扣
     *
     * @param sysOrderDiscountDto
     * @Param: [sysOrderDiscountDto]
     * @return: com.hu.oneclick.model.base.Resp<java.lang.String>
     * @Author: MaSiyi
     * @Date: 2021/10/14
     */
    @Override
    public Resp<Map<String, BigDecimal>> calculateOrderPrice(SysOrderDiscountDto sysOrderDiscountDto) {
        //根据所选的选出折扣表里面的基础折扣
        BigDecimal normalDiscount = sysOrderDiscountDao.getNormalDiscount(sysOrderDiscountDto);
        //根据所选的对应系统配置表中选出原始价钱
        String dataStPrice = systemConfigService.getData(sysOrderDiscountDto.getDataStrorage());
        String dataApPrice = systemConfigService.getData(sysOrderDiscountDto.getApiCall());
        BigDecimal dataStPriceBd = new BigDecimal(dataStPrice);
        BigDecimal dataApPriceBd = new BigDecimal(dataApPrice);
        BigDecimal allPrice = dataApPriceBd.add(dataStPriceBd);
        //根据推荐表里面获取当前的推荐人折扣
        SysUser sysUser = jwtUserService.getUserLoginInfo().getSysUser();
        int referenceTimeCount = sysUserReferenceService.getReferenceTime(sysUser);
        String referenceTimeCountDiscount = systemConfigService
                .getDateForKeyAndGroup(String.valueOf(referenceTimeCount), OneConstant.SystemConfigGroup.REFERENCETIME);
        BigDecimal referenceTimeCountDiscountGg = new BigDecimal(referenceTimeCountDiscount);
        int referencePersonNoCount = sysUserReferenceService.getReferencePersonNo(sysUser);
        String referencePersonNoCountDiscount = systemConfigService
                .getDateForKeyAndGroup(String.valueOf(referencePersonNoCount), OneConstant.SystemConfigGroup.REFERENCEPERSONNO);
        BigDecimal referencePersonNoCountDiscountBg = new BigDecimal(referencePersonNoCountDiscount);
        BigDecimal allReferenceDiscount = referenceTimeCountDiscountGg.add(referencePersonNoCountDiscountBg);
        //两个折扣相加，计算出折扣之后的价钱
        BigDecimal addDiscount = normalDiscount.add(allReferenceDiscount);
        BigDecimal currentPrice = allPrice.multiply(addDiscount);
        Map<String, BigDecimal> map = new HashMap<>(3);
        map.put("originalPrice", allPrice);
        map.put("currentPrice", allPrice.subtract(currentPrice));
        return new Resp.Builder<Map<String, BigDecimal>>().setData(map).ok();
    }
}
