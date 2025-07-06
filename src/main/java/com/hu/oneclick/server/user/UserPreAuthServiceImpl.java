package com.hu.oneclick.server.user;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.common.util.PasswordCheckerUtil;
import com.hu.oneclick.common.util.SnowFlakeUtil;
import com.hu.oneclick.controller.req.RegisterBody;
import com.hu.oneclick.model.entity.SysUserOrder;
import com.hu.oneclick.server.service.SystemConfigService;
import com.hu.oneclick.server.service.UserOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import java.math.BigDecimal;
import com.hu.oneclick.dao.*;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.dto.ActivateAccountDto;
import com.hu.oneclick.model.entity.*;
import com.hu.oneclick.server.service.MailService;
import com.hu.oneclick.server.service.ProjectService;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import cn.hutool.core.bean.BeanUtil;

/**
 * 用户登录前操作服务实现类
 * 
 * @author oneclick
 */
@Service
public class UserPreAuthServiceImpl implements UserPreAuthService {

    private final static Logger logger = LoggerFactory.getLogger(UserPreAuthServiceImpl.class);

    @Autowired
    private SysUserDao sysUserDao;

    @Autowired
    private RedissonClient redisClient;

    @Autowired
    private MailService mailService;

    @Autowired
    private RoomDao roomDao;

    @Value("${onclick.template.url}")
    private String templateUrl;

    @Value("${onclick.default.photo}")
    private String defaultPhoto;

    private final ProjectService projectService;
    private final SysUserProjectDao sysUserProjectDao;
    private final RoleFunctionDao roleFunctionDao;
    private final SysRoleDao sysRoleDao;
    private final SysUserBusinessDao sysUserBusinessDao;

    @Value("${onclick.time.firstTime}")
    private long firstTime;
    private final JwtUserServiceImpl jwtUserServiceImpl;

    @Autowired
    private UserOrderService userOrderService;

    @Autowired
    private SystemConfigService systemConfigService;


    public UserPreAuthServiceImpl(SysUserDao sysUserDao, JwtUserServiceImpl jwtUserServiceImpl,
                                 RedissonClient redisClient, MailService mailService,
                                 ProjectService projectService, SysUserProjectDao sysUserProjectDao,
                                 RoleFunctionDao roleFunctionDao, SysRoleDao sysRoleDao,
                                 SysUserBusinessDao sysUserBusinessDao) {
        this.sysUserDao = sysUserDao;
        this.jwtUserServiceImpl = jwtUserServiceImpl;
        this.redisClient = redisClient;
        this.mailService = mailService;
        this.projectService = projectService;
        this.sysUserProjectDao = sysUserProjectDao;
        this.roleFunctionDao = roleFunctionDao;
        this.sysRoleDao = sysRoleDao;
        this.sysUserBusinessDao = sysUserBusinessDao;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> register(final RegisterBody registerBody) {
        try {
            final SysUser registerUser = new SysUser();
            BeanUtils.copyProperties(registerBody, registerUser);

            String email = registerUser.getEmail();
            System.out.println(">>> 开始注册用户，邮箱: " + email);

            if (StringUtils.isEmpty(email)) {
                throw new BizException(SysConstantEnum.NOT_DETECTED_EMAIL.getCode(), SysConstantEnum.NOT_DETECTED_EMAIL.getValue());
            }

            SysUser user = new SysUser();
            BeanUtils.copyProperties(registerUser, user);
            //检查数据库是否已存在用户
            List<SysUser> sysUsers = sysUserDao.queryByLikeEmail(email);
            if (!CollUtil.isEmpty(sysUsers)) {
                return new Resp.Builder<String>().buildResult(SysConstantEnum.NO_DUPLICATE_REGISTER.getCode(), SysConstantEnum.NO_DUPLICATE_REGISTER.getValue());
            }
            for (SysUser sysUser : sysUsers) {
                if (!OneConstant.ACTIVE_STATUS.ACTIVE_GENERATION.equals(sysUser.getActiveState())) {
                    return new Resp.Builder<String>().buildResult(SysConstantEnum.NO_DUPLICATE_REGISTER.getCode(), SysConstantEnum.NO_DUPLICATE_REGISTER.getValue());
                } else if (OneConstant.ACTIVE_STATUS.ACTIVE_GENERATION.equals(sysUser.getActiveState())) {
                    //邮箱链接失效
                    String linkStr = RandomUtil.randomString(80);
                    redisClient.getBucket(linkStr).set("true", 30, TimeUnit.MINUTES);

                    System.out.println(">>> 准备发送激活邮件到: " + email);
                    System.out.println(">>> 激活链接参数: " + linkStr);
                    mailService.sendSimpleMail(email, "PriorTest 激活账号", templateUrl+"activate?email=" + email +
                        "&params=" + linkStr);
                    System.out.println(">>> 激活邮件发送完成");
                    return new Resp.Builder<String>().buildResult(SysConstantEnum.REREGISTER_SUCCESS.getCode(), SysConstantEnum.REREGISTER_SUCCESS.getValue());
                }
            }
            // 先查询该用户是否已在room表，如果在，更新，无新增
            Room room = roomDao.queryByCompanyNameAndUserEmail(registerUser.getCompany(), email);
            if (null == room) {
                room = new Room();
                room.setId(SnowFlakeUtil.getFlowIdInstance().nextId());
                room.setCompanyName(registerUser.getCompany());
                room.setCreateName(registerUser.getUserName());
                room.setCreateUserEmail(email);
                room.setDeleteFlag(false);
                room.setModifyName(registerUser.getUserName());
                room.setType(OneConstant.ACTIVE_STATUS.TRIAL);
                room.setExpiredDate(Date.from(LocalDateTime.now().plusDays(OneConstant.TRIAL_DAYS).atZone(ZoneId.systemDefault()).toInstant()));
                roomDao.insertRoom(room);
            } else {
                BeanUtil.copyProperties(registerUser, room);
                room.setCreateUserEmail(email);
                roomDao.updateRoom(room);
            }
            user.setRoomId(room.getId());
            //设置默认头像
            user.setPhoto(defaultPhoto);
            user.setSysRoleId(RoleConstant.ADMIN_PLAT);
            user.setActiveState(OneConstant.ACTIVE_STATUS.ACTIVE_GENERATION);

            if (sysUserDao.insert(user) > 0) {
                String linkStr = RandomUtil.randomString(80);
                redisClient.getBucket(linkStr).set("true", 30, TimeUnit.MINUTES);

                System.out.println(">>> 准备发送激活邮件到: " + email);
                System.out.println(">>> 激活链接参数: " +linkStr);

                mailService.sendSimpleMail(email, "PriorTest 激活账号", templateUrl + "activate?email=" + email +
                    "&params=" + linkStr);
                 System.out.println(">>> 激活 发邮件完毕: ");

                return new Resp.Builder<String>().buildResult(SysConstantEnum.REGISTER_SUCCESS.getCode(), SysConstantEnum.REGISTER_SUCCESS.getValue());
            }
            throw new BizException(SysConstantEnum.REGISTER_FAILED.getCode(), SysConstantEnum.REGISTER_FAILED.getValue());
        } catch (BizException e) {
            logger.error("class: UserServiceImpl#register,error []" + e.getMessage());
            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
        }
    }

    @Override
    public Resp<String> forgetThePassword(String email) {
        List<SysUser> sysUsers = sysUserDao.queryByLikeEmail(email);
        if (sysUsers.isEmpty()) {
            return new Resp.Builder<String>().buildResult(SysConstantEnum.NOUSER_ERROR.getCode(), SysConstantEnum.NOUSER_ERROR.getValue());
        }

        System.out.println(">>> 开始处理忘记密码，邮箱: " + email);
        SysUser sysUser = sysUsers.get(0);
        Integer activeState = sysUser.getActiveState();
        if (OneConstant.ACTIVE_STATUS.TRIAL_EXPIRED.equals(activeState)) {
            return new Resp.Builder<String>().buildResult("400", "账户试用已过期");

        } else if (OneConstant.ACTIVE_STATUS.ACTIVE_FAILED.equals(activeState) || OneConstant.ACTIVE_STATUS.ACTIVE_GENERATION.equals(activeState)) {
            return new Resp.Builder<String>().buildResult("400", "请先去激活账户");
        }
        String linkStr = RandomUtil.randomString(80);
        redisClient.getBucket(linkStr).set("true", 30, TimeUnit.MINUTES);
        System.out.println(">>> 准备发送忘记密码邮件");
        System.out.println(">>> 准备发送忘记密码激活链接参数 " +linkStr);
        mailService.sendSimpleMail(email, "PriorTest 忘记密码", templateUrl + "findpwd?email=" + email + "&params=" + linkStr);
        System.out.println(">>> 重置密码邮件发送完成");
        return new Resp.Builder<String>().buildResult(SysConstantEnum.SUCCESS.getCode(), SysConstantEnum.SUCCESS.getValue());
    }

    @Override
    public Resp<String> forgetThePasswordIn(ActivateAccountDto activateAccountDto) {
        return activateAccount(activateAccountDto, OneConstant.PASSWORD.FORGETPASSWORD);
    }

    @Override
    public Resp<String> activateAccount(ActivateAccountDto activateAccountDto, String activation) {
        try {
            System.out.println(">>> UserPreAuthServiceImpl.activateAccount 被调用，激活类型: " + activation);

            String email = activateAccountDto.getEmail();
            String params = activateAccountDto.getParams();
            String password = activateAccountDto.getPassword();

            System.out.println(">>> 激活参数 - email: " + email + ", params: " + params);

            if (StringUtils.isEmpty(email)) {
                return new Resp.Builder<String>().buildResult(SysConstantEnum.NOT_DETECTED_EMAIL.getCode(), SysConstantEnum.NOT_DETECTED_EMAIL.getValue());
            }

            // 验证Redis中的参数
            if (!redisClient.getBucket(params).isExists()) {
                System.out.println(">>> 激活链接已过期或无效");
                return new Resp.Builder<String>().buildResult("400", "激活链接已过期或无效");
            }

            // 查询用户
            List<SysUser> sysUsers = sysUserDao.queryByLikeEmail(email);
            if (CollUtil.isEmpty(sysUsers)) {
                return new Resp.Builder<String>().buildResult(SysConstantEnum.NOUSER_ERROR.getCode(), SysConstantEnum.NOUSER_ERROR.getValue());
            }

            SysUser sysUser = sysUsers.get(0);
            System.out.println(">>> 找到用户: " + sysUser.getUserName() + ", 当前状态: " + sysUser.getActiveState());

            // 根据激活类型处理
            if (OneConstant.PASSWORD.ACTIVATION.equals(activation)) {
                // 账户激活
                if (OneConstant.ACTIVE_STATUS.ACTIVE_GENERATION.equals(sysUser.getActiveState())) {
                    sysUser.setActiveState(OneConstant.ACTIVE_STATUS.ACTIVE);
                    sysUser.setPassword(encodePassword(password));
                    sysUserDao.updateByEmail(sysUser);

                    // 删除Redis中的激活参数
                    redisClient.getBucket(params).delete();

                    System.out.println(">>> 账户激活成功");
                    return new Resp.Builder<String>().buildResult(SysConstantEnum.SUCCESS.getCode(), "账户激活成功");
                } else {
                    return new Resp.Builder<String>().buildResult("400", "账户状态异常，无法激活");
                }
            } else if (OneConstant.PASSWORD.FORGETPASSWORD.equals(activation)) {
                // 忘记密码重置
                if (OneConstant.ACTIVE_STATUS.ACTIVE.equals(sysUser.getActiveState())) {
                    sysUser.setPassword(encodePassword(password));
                    sysUserDao.updateByEmail(sysUser);

                    // 删除Redis中的重置参数
                    redisClient.getBucket(params).delete();

                    System.out.println(">>> 密码重置成功");
                    return new Resp.Builder<String>().buildResult(SysConstantEnum.SUCCESS.getCode(), "密码重置成功");
                } else {
                    return new Resp.Builder<String>().buildResult("400", "账户状态异常，无法重置密码");
                }
            }

            return new Resp.Builder<String>().buildResult("400", "未知的激活类型");

        } catch (Exception e) {
            logger.error("class: UserPreAuthServiceImpl#activateAccount,error []" + e.getMessage(), e);
            return new Resp.Builder<String>().buildResult("500", "系统错误: " + e.getMessage());
        }
    }

    /**
     * 密码加密
     */
    private String encodePassword(String password) {
        // 这里需要使用与原系统相同的密码加密方式
        // 通常使用BCrypt或其他加密算法
        return password; // 临时实现，需要根据实际加密方式修改
    }

    private void initOrder(String userId) {
        SysUserOrder sysUserOrder = new SysUserOrder();
        long orderId = SnowFlakeUtil.getFlowIdInstance().nextId();
        sysUserOrder.setOrderId(orderId);
        String apiCall = systemConfigService.getDateForKeyAndGroup("apiCall", OneConstant.SystemConfigGroup.INITORDER);
        sysUserOrder.setApiCall(apiCall);

        String dataStrorage = systemConfigService.getDateForKeyAndGroup("dataStrorage", OneConstant.SystemConfigGroup.INITORDER);
        sysUserOrder.setDataStrorage(dataStrorage);

        String subScription = systemConfigService.getDateForKeyAndGroup("subScription", OneConstant.SystemConfigGroup.INITORDER);
        sysUserOrder.setSubScription(subScription);

        String serviceDuration = systemConfigService.getDateForKeyAndGroup("serviceDuration", OneConstant.SystemConfigGroup.INITORDER);
        sysUserOrder.setServiceDuration(serviceDuration);

        String originalPrice = systemConfigService.getDateForKeyAndGroup("originalPrice", OneConstant.SystemConfigGroup.INITORDER);
        sysUserOrder.setOriginalPrice(new BigDecimal(originalPrice));

        String currentPrice = systemConfigService.getDateForKeyAndGroup("currentPrice", OneConstant.SystemConfigGroup.INITORDER);
        sysUserOrder.setCurrentPrice(new BigDecimal(currentPrice));

        sysUserOrder.setUserId(userId);
        userOrderService.insertOrder(sysUserOrder);
    }
}