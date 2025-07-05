package com.hu.oneclick.server.user;

import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.common.util.DateUtil;
import com.hu.oneclick.dao.SysUserOrderRecordDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.entity.SysUser;
import com.hu.oneclick.model.entity.SysUserOrder;
import com.hu.oneclick.model.entity.SysUserOrderRecord;
import com.hu.oneclick.server.service.SysOrderDiscountService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author MaSiyi
 * @version 1.0.0 2021/10/20
 * @since JDK 1.8.0
 */
@Service
public class SysUserOrderRecordImpl implements SysUserOrderRecordService {

    @Autowired
    private SysUserOrderRecordDao sysUserOrderRecordDao;
    @Autowired
    private JwtUserServiceImpl jwtUserService;
    @Autowired
    private UserOrderService userOrderService;
    @Autowired
    private SysOrderDiscountService sysOrderDiscountService;

    /**
     * 增
     *
     * @param sysUserOrderRecord
     * @Param: [sysUserOrderRecord]
     * @return: void
     * @Author: MaSiyi
     * @Date: 2021/10/20
     */
    @Override
    public void insert(SysUserOrderRecord sysUserOrderRecord) {
        sysUserOrderRecordDao.insert(sysUserOrderRecord);
    }

    @Override
    public Resp<String> payment(String id) {
        sysUserOrderRecordDao.payment(id);
        return new Resp.Builder<String>().ok();
    }


    /**
     * 获取用户订单详细
     *
     * @Param: []
     * @return: com.hu.oneclick.model.base.Resp<java.util.List < java.lang.String>>
     * @Author: MaSiyi
     * @Date: 2021/10/22
     */
    @Override
    public Resp<List<SysUserOrderRecord>> getUserOrderRecord() {

        SysUser sysUser = jwtUserService.getUserLoginInfo().getSysUser();
        List<SysUserOrder> sysUserOrders = userOrderService.listOrder(sysUser.getId();
        for (SysUserOrder sysUserOrder : sysUserOrders) {
            calculateThisMonthSDiscount(sysUserOrder.getOrderId(), sysUser);
        }
        List<SysUserOrderRecord> sysUserOrderRecords = sysUserOrderRecordDao.getUserOrderRecord(sysUser.getId();

        return new Resp.Builder<List<SysUserOrderRecord>>().setData(sysUserOrderRecords).ok();
    }

    /**
     * 计算本月折扣
     *
     * @param orderId
     * @param sysUser
     * @Param:
     * @return:
     * @Author: MaSiyi
     * @Date: 2021/10/22
     */
    public void calculateThisMonthSDiscount(Long orderId, SysUser sysUser) {
        Date date = new Date();
        Date[] monthLimit = DateUtil.getMonthLimit(date);
        //查询当月订单
        SysUserOrderRecord sysUserOrderRecord = sysUserOrderRecordDao.getOrderRecordForDate(monthLimit[0], monthLimit[1], orderId);
        if (StringUtils.isEmpty(sysUserOrderRecord) || StringUtils.isEmpty(sysUserOrderRecord.getOrder_id() {
            return;
        }
        //获取单月折扣
        BigDecimal referenceDiscount = sysOrderDiscountService.getReferenceDiscount(sysUser, monthLimit[0], monthLimit[1]);
        sysUserOrderRecord.setDiscount(referenceDiscount);
        //原价
        BigDecimal originalPrice = sysUserOrderRecord.getOriginal_price() == null ? BigDecimal.ZERO : sysUserOrderRecord.getOriginal_price();
        //折扣价
        BigDecimal multiply = originalPrice.multiply(referenceDiscount);
        sysUserOrderRecord.setExpenditure(originalPrice.subtract(multiply);
        sysUserOrderRecordDao.updateByPrimaryKeySelective(sysUserOrderRecord);
    }

}
