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
import org.apache.commons.lang3.StringUtils;
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
        String normalDiscountFlag = systemConfigService.getDateForKeyAndGroup("NormalDiscount", OneConstant.SystemConfigGroup.SYSTEMCONFIG);
        //如果基础折扣开关为打开
        BigDecimal normalDiscount;
        if (OneConstant.SystemConfigStatus.ON.equals(normalDiscountFlag)) {
            normalDiscount = sysOrderDiscountDao.getNormalDiscount(sysOrderDiscountDto);
        } else {
            normalDiscount = BigDecimal.ZERO;
        }
        //根据所选的对应系统配置表中选出原始价钱
        String dataStPrice = systemConfigService.getData(sysOrderDiscountDto.getDataStrorage());
        String dataApPrice = systemConfigService.getData(sysOrderDiscountDto.getApiCall());
        BigDecimal dataStPriceBd = new BigDecimal(dataStPrice);
        BigDecimal dataApPriceBd = new BigDecimal(dataApPrice);
        BigDecimal allPrice = dataApPriceBd.add(dataStPriceBd);
        //根据推荐表里面获取当前的推荐人折扣
        SysUser sysUser = jwtUserService.getUserLoginInfo().getSysUser();
        //如果推荐开关为打开
        String referencedTime = systemConfigService.getDateForKeyAndGroup("ReferencedTime", OneConstant.SystemConfigGroup.SYSTEMCONFIG);
        BigDecimal referenceTimeCountDiscountGg;
        if (OneConstant.SystemConfigStatus.ON.equals(referencedTime)) {

            int referenceTimeCount = sysUserReferenceService.getReferenceTime(sysUser);
            String referenceTimeCountDiscount = systemConfigService
                    .getDateForKeyAndGroup(String.valueOf(referenceTimeCount), OneConstant.SystemConfigGroup.REFERENCETIME);
            referenceTimeCountDiscountGg = new BigDecimal(referenceTimeCountDiscount);
        } else {
            referenceTimeCountDiscountGg = BigDecimal.ZERO;
        }
        //如果引用开关为打开
        String referencePersonNo = systemConfigService.getDateForKeyAndGroup("ReferencePersonNo", OneConstant.SystemConfigGroup.SYSTEMCONFIG);
        BigDecimal referencePersonNoCountDiscountBg;
        if (OneConstant.SystemConfigStatus.ON.equals(referencePersonNo)) {
            int referencePersonNoCount = sysUserReferenceService.getReferencePersonNo(sysUser);
            String referencePersonNoCountDiscount = systemConfigService
                    .getDateForKeyAndGroup(String.valueOf(referencePersonNoCount), OneConstant.SystemConfigGroup.REFERENCEPERSONNO);
            referencePersonNoCountDiscountBg = new BigDecimal(referencePersonNoCountDiscount);
        } else {
            referencePersonNoCountDiscountBg = BigDecimal.ZERO;
        }
        //总折扣
        BigDecimal allReferenceDiscount = referenceTimeCountDiscountGg.add(referencePersonNoCountDiscountBg);
        //两个折扣相加，计算出折扣之后的价钱
        BigDecimal addDiscount = normalDiscount.add(allReferenceDiscount);
        //如果特别折扣开关为打开
        String specialDiscount = systemConfigService.getDateForKeyAndGroup("SpecialDiscount", OneConstant.SystemConfigGroup.SYSTEMCONFIG);
        if (OneConstant.SystemConfigStatus.ON.equals(specialDiscount)) {
            String specialDiscountNu = systemConfigService.getDateForKeyAndGroup(sysUser.getEmail(), OneConstant.SystemConfigGroup.SPECICALDISCOUNT);
            if (StringUtils.isNotEmpty(specialDiscountNu)) {
                addDiscount = addDiscount.add(new BigDecimal(specialDiscountNu));
            }
        }
        //如果VIP开关为打开
        String vip = systemConfigService.getDateForKeyAndGroup("VIP", OneConstant.SystemConfigGroup.SYSTEMCONFIG);
        if (OneConstant.SystemConfigStatus.ON.equals(vip)) {
            if ("VIP".equals(sysUser.getUserClass())) {
                //vip折扣力度
                String vipNu = systemConfigService.getDateForKeyAndGroup("VIP", OneConstant.SystemConfigGroup.VIP);
                addDiscount = addDiscount.add(new BigDecimal(vipNu));
            }
        }
        //如果节假日折扣开关为打开
        String flashDiscount = systemConfigService.getDateForKeyAndGroup("FlashDiscount", OneConstant.SystemConfigGroup.SYSTEMCONFIG);
        if (OneConstant.SystemConfigStatus.ON.equals(flashDiscount)) {
            String flashDiscountNu = systemConfigService.getDateForKeyAndGroup("FlashDiscount", OneConstant.SystemConfigGroup.FLASHDISCOUNT);
            addDiscount = addDiscount.add(new BigDecimal(flashDiscountNu));
        }
        BigDecimal currentPrice = allPrice.multiply(addDiscount);
        Map<String, BigDecimal> map = new HashMap<>(3);
        map.put("originalPrice", allPrice);
        map.put("currentPrice", allPrice.subtract(currentPrice));
        return new Resp.Builder<Map<String, BigDecimal>>().setData(map).ok();
    }
}
