package com.hu.oneclick.server.user;
import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.common.util.SnowFlakeUtil;
import com.hu.oneclick.dao.SysUserDao;
import com.hu.oneclick.dao.SysUserOrderDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.entity.SysUser;
import com.hu.oneclick.model.entity.SysUserOrder;
import com.hu.oneclick.model.entity.SysUserOrderRecord;
import com.hu.oneclick.server.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
/**
 * @author MaSiyi
 * @version 1.0.0 2021/10/9
 * @since JDK 1.8.0
 */
@Service
public class UserOrderServiceImpl implements UserOrderService {
    @Autowired
    private SysUserOrderDao sysUserOrderDao;
    @Autowired
    private SysUserDao sysUserDao;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private SysUserOrderRecordService sysUserOrderRecordService;
    @Autowired
    private JwtUserServiceImpl jwtUserService;
    @Override
    public Resp<String> insertOrder(SysUserOrder sysUserOrder) {
        String userId = sysUserOrder.getUserId();
        if (StringUtils.isEmpty(userId) {
            userId = jwtUserService.getUserLoginInfo().getSysUser().getId();
        }
        sysUserOrder.setUserId(userId);
        //初始转态为未支付
        sysUserOrder.setStatus(false);
        long orderId = SnowFlakeUtil.getFlowIdInstance().nextId();
        sysUserOrder.setOrderId(orderId);
        //订阅时长
        String serviceDuration = sysUserOrder.getServiceDuration();
        int duration = 1;
        switch (serviceDuration) {
            case "Monthly":
                duration = 1;
                break;
            case "Quarterly":
                duration = 3;
                break;
            case "HalfYear":
                duration = 6;
                break;
            case "Yearly":
                duration = 12;
                break;
            default:
        }
        //月付，季付
        String subScription = sysUserOrder.getSubScription();
        int scription = duration;
        BigDecimal durationBigDecimal = new BigDecimal(duration);
        //产生订单的数量
        int orderCount;
        switch (subScription) {
            case "Monthly":
                scription = 1;
                break;
            case "Quarterly":
                scription = 3;
                break;
            case "HalfYear":
                scription = 6;
                break;
            case "Yearly":
                scription = 12;
                break;
            default:
        }
        BigDecimal scriptionBigDecimal = new BigDecimal(scription);
        BigDecimal divide = new BigDecimal(1);
        if (!"Perpetual".equals(serviceDuration) {
            divide = durationBigDecimal.divide(scriptionBigDecimal);
        }
        orderCount = divide.intValue();
        //付款时间
        Calendar instance = Calendar.getInstance();
        for (int i = 0; i < orderCount; i++) {
            instance.add(Calendar.MONDAY, +scription);
            SysUserOrderRecord sysUserOrderRecord = new SysUserOrderRecord();
            addOrderRecord(sysUserOrderRecord, sysUserOrder);
            //原价
            BigDecimal originalPrice = sysUserOrder.getOriginalPrice();
            sysUserOrderRecord.setOriginal_price(originalPrice
                    .divide(new BigDecimal(orderCount), 2, RoundingMode.HALF_UP);
            //折扣价
            BigDecimal discountPrice = sysUserOrder.getCurrentPrice()
                    .divide(new BigDecimal(orderCount), 2, RoundingMode.HALF_UP);
            sysUserOrderRecord.setService_plan_duration(serviceDuration);
            //最后一次付款价格
            int newCount = orderCount - 1;
            if (i == newCount) {
                BigDecimal currentPrice = sysUserOrder.getCurrentPrice();
                discountPrice = currentPrice.subtract(discountPrice.multiply(BigDecimal.valueOf(newCount);
                sysUserOrderRecord.setDiscount_price(discountPrice);
            } else {
                sysUserOrderRecord.setDiscount_price(discountPrice);
            }
            sysUserOrderRecord.setPayment_time(instance.getTime();
            sysUserOrderRecordService.insert(sysUserOrderRecord);
        }
        if (sysUserOrderDao.insertSelective(sysUserOrder) > 0) {
            return new Resp.Builder<String>().buildResult(SysConstantEnum.ADD_SUCCESS.getCode(), SysConstantEnum.ADD_SUCCESS.getValue();
        }
        return new Resp.Builder<String>().buildResult(SysConstantEnum.ADD_FAILED.getCode(), SysConstantEnum.ADD_FAILED.getValue();
    }
    /**
     * 添加订单记录表
     *
     * @Param: [sysUserOrderRecord, sysUserOrder]
     * @return: com.hu.oneclick.model.entity.SysUserOrderRecord
     * @Author: MaSiyi
     * @Date: 2021/10/20
     */
    private void addOrderRecord(SysUserOrderRecord sysUserOrderRecord, SysUserOrder sysUserOrder) {
        //todo 而在提交的订单的时候，来判断用户身份的改变
        sysUserOrderRecord.setOrder_id(sysUserOrder.getOrderId();
        sysUserOrderRecord.setStatus(false);
        sysUserOrderRecord.setCreate_time(new Date();
        sysUserOrderRecord.setPayment_type(sysUserOrder.getPaymentType();
        String dataStrorage = sysUserOrder.getDataStrorage();
        sysUserOrderRecord.setData_strorage(dataStrorage);
        sysUserOrderRecord.setData_price(new BigDecimal(systemConfigService.getDateForKeyAndGroup(
                String.valueOf(dataStrorage), OneConstant.SystemConfigGroup.SYSTEMCONFIG);
        String apiCall = sysUserOrder.getApiCall();
        sysUserOrderRecord.setApi_call(apiCall);
        sysUserOrderRecord.setApi_call_price(new BigDecimal(systemConfigService.getDateForKeyAndGroup(
                apiCall, OneConstant.SystemConfigGroup.SYSTEMCONFIG);
        sysUserOrderRecord.setSub_scription(sysUserOrder.getSubScription();
        sysUserOrderRecord.setDiscount(new BigDecimal("0");
        sysUserOrderRecord.setExpenditure(new BigDecimal("0");
        sysUserOrderRecord.setInvoice(false);
    }
    @Override
    public Resp<String> insertUserDetail(SysUser sysUser) {
        sysUser.setActiveState(OneConstant.ACTIVE_STATUS.PAYING_USERS);
        sysUser.setActivitiDate(new Date(System.currentTimeMillis();
        String userId = sysUser.getId();
        //过期时间
        long expireDate = sysUserDao.getExpireDate(userId).getTime();
        SysUserOrder orderOfUserId = sysUserOrderDao.getOrderOfUserId(userId);
        //订阅时长
        String subScription = orderOfUserId.getSubScription();
        String dataTime = systemConfigService.getData(subScription);
        long addTime = Integer.parseInt(dataTime) * 24 * 60 * 60 * 1000L;
        //过期时间
        sysUser.setExpireDate(new Date(expireDate + addTime);
        sysUserDao.update(sysUser);
        jwtUserService.saveUserLoginInfo2(sysUserDao.queryById(sysUser.getId();
        //已支付
        orderOfUserId.setStatus(true);
        sysUserOrderDao.updateByUuidSelective(orderOfUserId);
        return new Resp.Builder<String>().buildResult(SysConstantEnum.SUCCESS.getCode(), SysConstantEnum.SUCCESS.getValue();
    }
    /**
     * 查询付款方式
     *
     * @Param: []
     * @return: com.hu.oneclick.model.base.Resp<java.lang.String>
     * @Author: MaSiyi
     * @Date: 2021/10/21
     */
    @Override
    public Resp<List<String>> getPaymentMethod() {
        List<String> keyForGroup = systemConfigService.getKeyForGroup(OneConstant.SystemConfigGroup.PAYMENTTYPE);
        return new Resp.Builder<List<String>>().setData(keyForGroup).ok();
    }
    @Override
    public List<SysUserOrder> listOrder(String userId) {
        return sysUserOrderDao.listOrder(userId);
    }
}
}
}
