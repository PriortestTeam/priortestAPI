package com.hu.oneclick.server.user;

import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.enums.OrderEnum;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.common.util.DateUtil;
import com.hu.oneclick.common.util.SnowFlakeUtil;
import com.hu.oneclick.dao.SysUserOrderDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.SysUser;
import com.hu.oneclick.model.domain.SysUserOrder;
import com.hu.oneclick.model.domain.SysUserOrderRecord;
import com.hu.oneclick.server.service.SysOrderDiscountService;
import com.hu.oneclick.server.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
    private UserService userService;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private SysUserOrderRecordService sysUserOrderRecordService;


    @Override
    public Resp<String> insertOrder(SysUserOrder sysUserOrder) {
        //初始转态为未支付
        sysUserOrder.setStatus(false);
        long orderId = SnowFlakeUtil.getFlowIdInstance().nextId();
        sysUserOrder.setOrderId(orderId);
        OrderEnum orderEnum = OrderEnum.toType(sysUserOrder.getServicePlanDuration());
        //付款时间
        Calendar instance = Calendar.getInstance();
        switch (orderEnum) {
            case MONTHLU:
                for (int i = 0; i < 12; i++) {
                    SysUserOrderRecord sysUserOrderRecord = new SysUserOrderRecord();
                    SysUserOrderRecord addSysUserOrderRecord = addOrderRecord(sysUserOrderRecord, sysUserOrder);
                    addSysUserOrderRecord.setOriginal_price(sysUserOrder.getOriginalPrice()
                            .divide(new BigDecimal(12), 2, RoundingMode.HALF_UP));
                    addSysUserOrderRecord.setDiscount_price(sysUserOrder.getCurrentPrice()
                            .divide(new BigDecimal(12), 2, RoundingMode.HALF_UP));
                    addSysUserOrderRecord.setService_plan_duration(orderEnum.getValue());
                    instance.add(Calendar.MONTH, +1);
                    addSysUserOrderRecord.setPayment_time(instance.getTime());
                    sysUserOrderRecordService.insert(addSysUserOrderRecord);
                }
                break;
            case QUARTOLY:
                for (int i = 0; i < 4; i++) {
                    SysUserOrderRecord sysUserOrderRecord = new SysUserOrderRecord();
                    SysUserOrderRecord addSysUserOrderRecord = addOrderRecord(sysUserOrderRecord, sysUserOrder);
                    addSysUserOrderRecord.setOriginal_price(sysUserOrder.getOriginalPrice()
                            .divide(new BigDecimal(4), 2, RoundingMode.HALF_UP));
                    addSysUserOrderRecord.setDiscount_price(sysUserOrder.getCurrentPrice()
                            .divide(new BigDecimal(4), 2, RoundingMode.HALF_UP));
                    addSysUserOrderRecord.setService_plan_duration(orderEnum.getValue());
                    instance.add(Calendar.MONTH, +3);
                    addSysUserOrderRecord.setPayment_time(instance.getTime());
                    sysUserOrderRecordService.insert(addSysUserOrderRecord);
                }
                break;
            case HALFYEAR:
                for (int i = 0; i < 2; i++) {
                    SysUserOrderRecord sysUserOrderRecord = new SysUserOrderRecord();
                    SysUserOrderRecord addSysUserOrderRecord = addOrderRecord(sysUserOrderRecord, sysUserOrder);
                    addSysUserOrderRecord.setOriginal_price(sysUserOrder.getOriginalPrice()
                            .divide(new BigDecimal(2), 2, RoundingMode.HALF_UP));
                    addSysUserOrderRecord.setDiscount_price(sysUserOrder.getCurrentPrice()
                            .divide(new BigDecimal(2), 2, RoundingMode.HALF_UP));
                    addSysUserOrderRecord.setService_plan_duration(orderEnum.getValue());
                    instance.add(Calendar.MONTH, +6);
                    addSysUserOrderRecord.setPayment_time(instance.getTime());
                    sysUserOrderRecordService.insert(addSysUserOrderRecord);
                }
                break;
            case YEARLY:
                SysUserOrderRecord sysUserOrderRecord = new SysUserOrderRecord();
                SysUserOrderRecord addSysUserOrderRecord = addOrderRecord(sysUserOrderRecord, sysUserOrder);
                addSysUserOrderRecord.setOriginal_price(sysUserOrder.getOriginalPrice());
                addSysUserOrderRecord.setDiscount_price(sysUserOrder.getCurrentPrice());
                addSysUserOrderRecord.setService_plan_duration(orderEnum.getValue());
                instance.add(Calendar.MONDAY, +12);
                addSysUserOrderRecord.setPayment_time(instance.getTime());
                sysUserOrderRecordService.insert(addSysUserOrderRecord);
                break;
            default:

        }
        if (sysUserOrderDao.insertSelective(sysUserOrder) > 0) {
            return new Resp.Builder<String>().buildResult(SysConstantEnum.ADD_SUCCESS.getCode(), SysConstantEnum.ADD_SUCCESS.getValue());
        }
        return new Resp.Builder<String>().buildResult(SysConstantEnum.ADD_FAILED.getCode(), SysConstantEnum.ADD_FAILED.getValue());

    }

    /**
     * 添加订单记录表
     *
     * @Param: [sysUserOrderRecord, sysUserOrder]
     * @return: com.hu.oneclick.model.domain.SysUserOrderRecord
     * @Author: MaSiyi
     * @Date: 2021/10/20
     */
    private SysUserOrderRecord addOrderRecord(SysUserOrderRecord sysUserOrderRecord, SysUserOrder sysUserOrder) {

        sysUserOrderRecord.setOrder_id(sysUserOrder.getOrderId());
        sysUserOrderRecord.setStatus(false);
        sysUserOrderRecord.setCreate_time(new Date());
        sysUserOrderRecord.setPayment_type(sysUserOrder.getPaymentType());
        Integer dataStrorage = sysUserOrder.getDataStrorage();
        sysUserOrderRecord.setData_strorage(dataStrorage);
        sysUserOrderRecord.setData_price(new BigDecimal(systemConfigService.getDateForKeyAndGroup(
                String.valueOf(dataStrorage), OneConstant.SystemConfigGroup.DATASTRORAGE)));
        String apiCall = sysUserOrder.getApiCall();
        sysUserOrderRecord.setApi_call(apiCall);
        sysUserOrderRecord.setApi_call_price(new BigDecimal(systemConfigService.getDateForKeyAndGroup(
                apiCall, OneConstant.SystemConfigGroup.APICALL)));
        sysUserOrderRecord.setSub_scription(sysUserOrder.getSubScription());
        sysUserOrderRecord.setDiscount(new BigDecimal("0"));
        sysUserOrderRecord.setExpenditure(new BigDecimal("0"));
        sysUserOrderRecord.setInvoice(false);
        return sysUserOrderRecord;
    }

    @Override
    public Resp<String> insertUserDetail(SysUser sysUser) {

        sysUser.setActiveState(OneConstant.ACTIVE_STATUS.PAYING_USERS);
        sysUser.setActivitiDate(new Date(System.currentTimeMillis()));
        String userId = sysUser.getId();
        //过期时间
        long expireDate = userService.getExpireDate(userId).getTime();
        SysUserOrder orderOfUserId = sysUserOrderDao.getOrderOfUserId(userId);
        //订阅时长
        String subScription = orderOfUserId.getSubScription();
        String dataTime = systemConfigService.getData(subScription);
        long addTime = Integer.parseInt(dataTime) * 24 * 60 * 60 * 1000L;

        //过期时间
        sysUser.setExpireDate(new Date(expireDate + addTime));
        userService.updateUserInfo(sysUser);
        //已支付
        orderOfUserId.setStatus(true);
        sysUserOrderDao.updateByUuidSelective(orderOfUserId);
        return new Resp.Builder<String>().buildResult(SysConstantEnum.SUCCESS.getCode(), SysConstantEnum.SUCCESS.getValue());

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
