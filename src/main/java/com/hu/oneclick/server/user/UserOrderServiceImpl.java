package com.hu.oneclick.server.user;

import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.enums.OrderEnum;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.util.DateUtil;
import com.hu.oneclick.common.util.SnowFlakeUtil;
import com.hu.oneclick.dao.SysUserOrderDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.SysUser;
import com.hu.oneclick.model.domain.SysUserOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

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


    @Override
    public Resp<String> insertOrder(SysUserOrder sysUserOrder) {
        //初始转态为未支付
        sysUserOrder.setStatus(false);
        sysUserOrder.setUuid(SnowFlakeUtil.getFlowIdInstance().nextId());
        if (sysUserOrderDao.insertSelective(sysUserOrder) > 0) {
            return new Resp.Builder<String>().buildResult(SysConstantEnum.ADD_SUCCESS.getCode(), SysConstantEnum.ADD_SUCCESS.getValue());
        }
        return new Resp.Builder<String>().buildResult(SysConstantEnum.ADD_FAILED.getCode(), SysConstantEnum.ADD_FAILED.getValue());

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
        Integer subScription = orderOfUserId.getSubScription();
        long addTime;
        switch (OrderEnum.toType(subScription)) {
            case MONTHLU:
                addTime = OrderEnum.MONTHLU.getKey() * 24 * 60 * 60 * 1000L;
                break;
            case QUARTOLY:
                addTime = OrderEnum.QUARTOLY.getKey() * 24 * 60 * 60 * 1000L;
                break;
            case HALFYEAR:
                addTime = OrderEnum.HALFYEAR.getKey() * 24 * 60 * 60 * 1000L;
                break;
            case YEARLY:
                addTime = OrderEnum.YEARLY.getKey() * 24 * 60 * 60 * 1000L;
                break;
            default:
                addTime = 0;
        }
        //过期时间
        sysUser.setExpireDate(new Date(expireDate + addTime));
        userService.updateUserInfo(sysUser);
        //已支付
        orderOfUserId.setStatus(true);
        sysUserOrderDao.updateByUuidSelective(orderOfUserId);
        return new Resp.Builder<String>().buildResult(SysConstantEnum.SUCCESS.getCode(), SysConstantEnum.SUCCESS.getValue());

    }
}
