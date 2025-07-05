package com.hu.oneclick.server.user;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.constant.RoleConstant;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.common.security.JwtAuthenticationToken;
import com.hu.oneclick.common.security.ApiToken;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.common.util.DateUtil;
import com.hu.oneclick.common.util.PasswordCheckerUtil;
import com.hu.oneclick.common.util.SnowFlakeUtil;
import com.hu.oneclick.controller.req.RegisterBody;
import com.hu.oneclick.dao.*;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.base.Result;
import com.hu.oneclick.model.domain.dto.*;
import com.hu.oneclick.model.entity.*;
import com.hu.oneclick.server.service.MailService;
import com.hu.oneclick.server.service.ProjectService;
import com.hu.oneclick.server.service.SystemConfigService;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author qingyang
 */
@Service
public class UserServiceImpl implements UserService {
    private final static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final SysUserDao sysUserDao;

    private final JwtUserServiceImpl jwtUserServiceImpl;

    private final RedissonClient redisClient;

    private final MailService mailService;

    private final SysUserTokenDao sysUserTokenDao;

    private final ProjectService projectService;

    private final SubUserProjectDao subUserProjectDao;

    private final UserOrderService userOrderService;

    private final SystemConfigService systemConfigService;

    private final RoomDao roomDao;

    private final RoleFunctionDao roleFunctionDao;

    private final SysUserBusinessDao sysUserBusinessDao;

    private final SysRoleDao sysRoleDao;

    private final SysUserProjectDao sysUserProjectDao;

    @Value("${onclick.default.photo}")
    private String defaultPhoto;

    @Value("${onclick.time.firstTime}")
    private long firstTime;

    @Value("${onclick.time.secondTime}")
    private long secondTime;

    public UserServiceImpl(SysUserDao sysUserDao,
                           RedissonClient redisClient, JwtUserServiceImpl jwtUserServiceImpl,
                           MailService mailService, SysUserTokenDao sysUserTokenDao, ProjectService projectService,
                           SubUserProjectDao subUserProjectDao, UserOrderService userOrderService,
                           SystemConfigService systemConfigService, RoomDao roomDao, RoleFunctionDao roleFunctionDao,
                           SysUserBusinessDao sysUserBusinessDao, SysRoleDao sysRoleDao, SysUserProjectDao sysUserProjectDao) {
        this.sysUserDao = sysUserDao;
        this.redisClient = redisClient;
        this.jwtUserServiceImpl = jwtUserServiceImpl;
        this.mailService = mailService;
        this.sysUserTokenDao = sysUserTokenDao;
        this.projectService = projectService;
        this.subUserProjectDao = subUserProjectDao;
        this.userOrderService = userOrderService;
        this.systemConfigService = systemConfigService;
        this.roomDao = roomDao;
        this.roleFunctionDao = roleFunctionDao;
        this.sysUserBusinessDao = sysUserBusinessDao;
        this.sysRoleDao = sysRoleDao;
        this.sysUserProjectDao = sysUserProjectDao;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> register(final RegisterBody registerBody) {
        try {
            final SysUser registerUser = new SysUser();
            BeanUtils.copyProperties(registerBody, registerUser);

            String email = registerUser.getEmail();
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
                    // mailService.sendSimpleMail(email, "OneClick激活账号", "http://124.71.142.223/#/activate?email=" + email +
                    // "&params=" + linkStr);
                    mailService.sendSimpleMail(email, "OneClick激活账号", "http://43.139.159.146/#/activate?email=" + email +
                        "&params=" + linkStr);

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

                mailService.sendSimpleMail(email, "OneClick激活账号", "http://43.139.159.146/#/activate?email=" + email +
                    "&params=" + linkStr);
                return new Resp.Builder<String>().buildResult(SysConstantEnum.REGISTER_SUCCESS.getCode(), SysConstantEnum.REGISTER_SUCCESS.getValue());
            }
            throw new BizException(SysConstantEnum.REGISTER_FAILED.getCode(), SysConstantEnum.REGISTER_FAILED.getValue());
        } catch (BizException e) {
            logger.error("class: UserServiceImpl#register,error []" + e.getMessage());
            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> modifyPassword(Map<String, String> args) {
        try {
            SysUser sysUser = jwtUserServiceImpl.getUserLoginInfo().getSysUser();
            String oldPassword = args.get("oldPassword");
            String newPassword = args.get("newPassword");
            verify(newPassword, null);

            if (!jwtUserServiceImpl.verifyPassword(oldPassword, sysUser)) {
                return new Resp.Builder<String>().buildResult("旧密码输入错误");
            }

            sysUser.setPassword(encodePassword(newPassword));
            updatePassword(sysUser);
            jwtUserServiceImpl.saveUserLoginInfo2(sysUser);
            return new Resp.Builder<String>().setData("修改成功").ok();
        } catch (BizException e) {
            logger.error("class: UserServiceImpl#modifyPassword,error []" + e.getMessage());
            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> resetPassword(Map<String, String> args) {
        try {
            String newPassword = args.get("newPassword");
            String verificationCode = args.get("verificationCode");
            String email = args.get("email");
            verify(newPassword, verificationCode);
            SysUser sysUser = sysUserDao.queryByEmail(email);
            if (sysUser == null) {
                throw new BizException(SysConstantEnum.NOT_DETECTED_EMAIL.getCode(), SysConstantEnum.NOT_DETECTED_EMAIL.getValue());
            }
            //验证邮箱
            verifyEmailCode(OneConstant.REDIS_KEY_PREFIX.RESET_PASSWORD + email, verificationCode);

            sysUser.setPassword(encodePassword(newPassword));
            updatePassword(sysUser);
            return new Resp.Builder<String>().setData("修改成功").ok();
        } catch (BizException e) {
            logger.error("class: UserServiceImpl#resetPassword,error []" + e.getMessage());
            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
        }
    }

    @Override
    public Resp<String> queryEmailDoesItExist(String email) {
        SysUser user = sysUserDao.queryByEmail(email);
        if (user == null) {
            return new Resp.Builder<String>().buildResult(SysConstantEnum.NOT_DETECTED_EMAIL.getCode(),
                SysConstantEnum.NOT_DETECTED_EMAIL.getValue());
        }
        return new Resp.Builder<String>().ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> updateUserInfo(SysUser sysUser) {
        try {
            if (sysUserDao.update(sysUser) <= 0) {
                return new Resp.Builder<String>().setData("修改失败。").fail();
            }
            jwtUserServiceImpl.saveUserLoginInfo2(sysUserDao.queryById(sysUser.getId()));
            return new Resp.Builder<String>().setData("修改成功").ok();
        } catch (Exception e) {
            logger.error("class: UserServiceImpl#updateUserInfo,error []" + e.getMessage());
            return new Resp.Builder<String>().buildResult(SysConstantEnum.SYS_ERROR.getCode(), SysConstantEnum.SYS_ERROR.getValue(), e.getMessage());
        }
    }

    @Override
    public Resp<SysUser> queryUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return new Resp.Builder<SysUser>().buildResult("401", "用户未认证");
        }

        String username = authentication.getName();
        if (username == null || username.trim().isEmpty()) {
            return new Resp.Builder<SysUser>().buildResult("400", "无法获取用户信息");
        }

        try {
            // 通过邮箱查询用户信息（因为authentication.getName()返回的是邮箱）
            SysUser sysUser = sysUserDao.queryByEmail(username);
            if (sysUser == null) {
                return new Resp.Builder<SysUser>().buildResult("404", "用户不存在");
            }

            return new Resp.Builder<SysUser>().setData(sysUser).ok();

        } catch (Exception e) {
            logger.error("查询用户信息失败", e);
            return new Resp.Builder<SysUser>().buildResult("500", "查询用户信息失败: " + e.getMessage());
        }
    }

    @Override
    public Resp<List<SysProjectPermissionDto>> queryUserPermissions() {
        AuthLoginUser userLoginInfo = jwtUserServiceImpl.getUserLoginInfo();
        return new Resp.Builder<List<SysProjectPermissionDto>>().setData(userLoginInfo.getPermissions()).ok();
    }

    @Override
    public Resp<List<SubUserDto>> queryByNameSubUsers(String subUserName) {
        List<SubUserDto> subUserDtos = CollUtil.newArrayList();
//        List<SubUserDto> subUserDtos = sysUserDao.queryByNameSubUsers(jwtUserServiceImpl.getMasterId(), subUserName);
        return new Resp.Builder<List<SubUserDto>>().setData(subUserDtos).totalSize(subUserDtos.size()).ok();
    }

    @Override
    public Resp<String> deleteUserById(String id) {
        //删除平台用户并删除子用户

        return Result.deleteResult(sysUserDao.deleteById(id));
    }

    /**
     * 验证密码是否符合规则
     *
     * @param password
     */
    private void verify(String password, String emailCode) {
        PasswordCheckerUtil passwordCheckerUtil = new PasswordCheckerUtil();
        if (StringUtils.isEmpty(password)) {
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(), "密码" + SysConstantEnum.PARAM_EMPTY.getValue());
        } else if (!passwordCheckerUtil.check(password)) {
            throw new BizException(SysConstantEnum.PASSWORD_RULES.getCode(), SysConstantEnum.PASSWORD_RULES.getValue());
        }
        if (emailCode != null) {
            if (StringUtils.isEmpty(emailCode)) {
                throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(), "邮箱验证码" + SysConstantEnum.PARAM_EMPTY.getValue());
            }
        }
    }

    /**
     * 验证邮箱验证码
     *
     * @param key
     * @param verifyCode
     */
    private void verifyEmailCode(String key, String verifyCode) {
        RBucket<String> bucket = redisClient.getBucket(key);
        String redisCode = bucket.get();
        if (redisCode == null || "".equals(redisCode) || !redisCode.equals(verifyCode)) {
            throw new BizException(SysConstantEnum.VERIFY_CODE_ERROR.getCode(), SysConstantEnum.VERIFY_CODE_ERROR.getValue());
        }
        bucket.delete();
    }

    /**
     * 更新密码
     *
     * @param user
     */
    private void updatePassword(SysUser user) {
        int update = sysUserDao.updatePassword(user);
        if (update <= 0) {
            throw new BizException(SysConstantEnum.FAILED.getCode(), SysConstantEnum.FAILED.getValue());
        }
    }

    /**
     * 密码加密
     *
     * @param password
     * @return
     */
    private String encodePassword(String password) {
        return jwtUserServiceImpl.encryptPassword(password);
    }

    @Override
    public Resp<String> activateAccount(ActivateAccountDto activateAccountDto, String activation) {
        if (StringUtils.isEmpty(activateAccountDto.getEmail())) {
            return new Resp.Builder<String>().buildResult(SysConstantEnum.NOT_DETECTED_EMAIL.getCode(), SysConstantEnum.NOT_DETECTED_EMAIL.getValue());
        }
        //检查数据库是否已存在用户
        List<SysUser> sysUsers = sysUserDao.queryByLikeEmail(activateAccountDto.getEmail());
        if (sysUsers.isEmpty()) {
            return new Resp.Builder<String>().buildResult(SysConstantEnum.NOUSER_ERROR.getCode(), SysConstantEnum.NOUSER_ERROR.getValue());
        }
        SysUser sysUser = sysUsers.get(0);
        //申请延期不提示再次输入密码
        if (!activation.equals(OneConstant.PASSWORD.APPLY_FOR_AN_EXTENSION)) {
            if (!activateAccountDto.getPassword().equals(activateAccountDto.getRePassword())) {
                return new Resp.Builder<String>().buildResult(SysConstantEnum.REPASSWORD_ERROR.getCode(), SysConstantEnum.REPASSWORD_ERROR.getValue());
            }
        }

        PasswordCheckerUtil passwordChecker = new PasswordCheckerUtil();
        if (!passwordChecker.check(activateAccountDto.getPassword())) {
            throw new BizException(SysConstantEnum.PASSWORD_RULES.getCode(), SysConstantEnum.PASSWORD_RULES.getValue());
        }

        //激活账号
        if (activation.equals(OneConstant.PASSWORD.ACTIVATION)) {
            sysUser.setActiveState(OneConstant.ACTIVE_STATUS.TRIAL);
            Date activitiDate = new Date(System.currentTimeMillis());
            sysUser.setActivitiDate(activitiDate);
            sysUser.setActivitiNumber(1);
            long time = activitiDate.getTime() + firstTime * 24 * 60 * 60 * 1000;
            sysUser.setExpireDate(new Date(time));
            String userId = sysUser.getId();

            // TODO 如果OpenProjectByDefaultId为空，代表这个是注册的激活
//            SubUserProject subUserProject = subUserProjectDao.queryByUserId(userId);
//            if (subUserProject == null || StringUtils.isEmpty(subUserProject.getOpenProjectByDefaultId())) {

            QueryWrapper<SysUserProject> query = Wrappers.query();
            query.eq("user_id", userId);
            List<SysUserProject> userProjects = sysUserProjectDao.selectList(query);
            if (userProjects.isEmpty()) {
//                UserUseOpenProject userUseOpenProject = new UserUseOpenProject();
//                userUseOpenProject.setProjectId(project.getId());
//                userUseOpenProject.setUserId(userId);
//                userUseOpenProject.setTitle("");
                //设置用户关联的项目
//                subUserProject = new SubUserProject();
//                subUserProject.setUserId(sysUser.getId());
//                subUserProject.setProjectId(project.getId());
//                subUserProject.setOpenProjectByDefaultId(project.getId());
//                subUserProjectDao.insert(subUserProject);

                Project project = new Project();
                project.setUserId(userId);
                project.setTitle("初始化项目");
                project.setStatus("开发中");
                project.setRoomId(sysUser.getRoomId());
                project.setUpdateTime(new Date());
                project.setReportToName(sysUser.getUserName());
                projectService.initProject(project, null);

                SysUserProject sysUserProject = new SysUserProject();
                sysUserProject.setUserId(new BigInteger(userId));
                sysUserProject.setProjectId(new BigInteger(project.getId()));
                sysUserProject.setIsDefault(1);
                sysUserProjectDao.insert(sysUserProject);

                this.initOrder(userId);

                // 2022/11/1 WangYiCheng 设置创始人初始项目的默认function
                RoleFunction roleFunction = roleFunctionDao.queryByRoleId(sysUser.getSysRoleId());

                SysRole sysRole = sysRoleDao.queryById(String.valueOf(sysUser.getSysRoleId()));

                SysUserBusiness sysUserBusiness = new SysUserBusiness();
                sysUserBusiness.setType("RoleFunctions");
                sysUserBusiness.setValue(roleFunction.getCheckFunctionId());
                sysUserBusiness.setInvisible(roleFunction.getInvisibleFunctionId());
                sysUserBusiness.setDeleteFlag("0");
                sysUserBusiness.setUserId(Long.valueOf(sysUser.getId()));
                sysUserBusiness.setUserName(sysUser.getUserName());
                sysUserBusiness.setRoleId(Long.valueOf(sysUser.getSysRoleId()));
                sysUserBusiness.setRoleName(sysRole.getRoleName());

                sysUserBusiness.setProjectId(Long.valueOf(project.getId()));
                sysUserBusiness.setProjectName(project.getTitle());
                sysUserBusinessDao.insertSelective(sysUserBusiness);
            }
        }

        //申请延期
        if (activation.equals(OneConstant.PASSWORD.APPLY_FOR_AN_EXTENSION)) {
            int activitiNumber = sysUser.getActivitiNumber() == null ? 0 : sysUser.getActivitiNumber();
            if (activitiNumber >= 1 && activitiNumber <= 3) {
                final DateTime newExpireDate =
                    cn.hutool.core.date.DateUtil.offsetDay(sysUser.getExpireDate(), 30);
                sysUser.setActivitiNumber(activitiNumber + 1);
                sysUser.setExpireDate(newExpireDate);
            } else {
                throw new BizException(SysConstantEnum.HAS_BEEN_ACTIVATED_ONCE.getCode(), SysConstantEnum.HAS_BEEN_ACTIVATED_ONCE.getValue());
            }
        }
        sysUser.setPassword(encodePassword(activateAccountDto.getPassword()));
        if (sysUserDao.update(sysUser) == 0) {
            throw new BizException(SysConstantEnum.UPDATE_FAILED.getCode(), SysConstantEnum.UPDATE_FAILED.getValue());
        }

        return new Resp.Builder<String>().buildResult(SysConstantEnum.SUCCESS.getCode(), SysConstantEnum.SUCCESS.getValue());
    }

    /**
     * 初始化订单
     *
     * @param userId
     * @Param: [sysUser]
     * @return: void
     * @Author: MaSiyi
     * @Date: 2022/1/11
     */
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

    @Override
    public Resp<String> forgetThePassword(String email) {
        List<SysUser> sysUsers = sysUserDao.queryByLikeEmail(email);
        if (sysUsers.isEmpty()) {
            return new Resp.Builder<String>().buildResult(SysConstantEnum.NOUSER_ERROR.getCode(), SysConstantEnum.NOUSER_ERROR.getValue());
        }
        SysUser sysUser = sysUsers.get(0);
        Integer activeState = sysUser.getActiveState();
        if (OneConstant.ACTIVE_STATUS.TRIAL_EXPIRED.equals(activeState)) {
            return new Resp.Builder<String>().buildResult("400", "账户试用已过期");

        } else if (OneConstant.ACTIVE_STATUS.ACTIVE_FAILED.equals(activeState) || OneConstant.ACTIVE_STATUS.ACTIVE_GENERATION.equals(activeState)) {
            return new Resp.Builder<String>().buildResult("400", "请先去激活账户");
        }
        String linkStr = RandomUtil.randomString(80);
        redisClient.getBucket(linkStr).set("true", 30, TimeUnit.MINUTES);
        // mailService.sendSimpleMail(email, "OneClick忘记密码", "http://124.71.142.223/#/findpwd?email=" + email + "&params=" + linkStr);
        mailService.sendSimpleMail(email, "OneClick忘记密码", "http://127.0.0.1:9529/#/findpwd?email=" + email + "&params=" + linkStr);
        return new Resp.Builder<String>().buildResult(SysConstantEnum.SUCCESS.getCode(), SysConstantEnum.SUCCESS.getValue());
    }

    @Override
    public Resp<String> forgetThePasswordIn(ActivateAccountDto activateAccountDto) {
        return activateAccount(activateAccountDto, OneConstant.PASSWORD.FORGETPASSWORD);
    }

    @Override
    public Resp<String> applyForAnExtension(String email) {
        // 这里需要检测用户一些信息
        final SysUser sysUser = sysUserDao.queryByEmail(email);
        if (Objects.isNull(sysUser)) {
            return new Resp.Builder<String>().buildResult(SysConstantEnum.EMAIL_NOT_EXIST.getCode(),
                SysConstantEnum.EMAIL_NOT_EXIST.getValue(), HttpStatus.BAD_REQUEST.value());
        }
        if (!"Trialer".equals(sysUser.getUserClass())) {
            return new Resp.Builder<String>().buildResult(SysConstantEnum.NOT_TRIALER_USER.getCode(),
                SysConstantEnum.NOT_TRIALER_USER.getValue(), HttpStatus.BAD_REQUEST.value());
        }
        final int activeNumber = Objects.nonNull(sysUser.getActivitiNumber()) ? sysUser.getActivitiNumber() : 0;
        if (activeNumber >= 3) {
            return new Resp.Builder<String>().buildResult(SysConstantEnum.TRIALER_LIMIT.getCode(),
                SysConstantEnum.TRIALER_LIMIT.getValue(), HttpStatus.BAD_REQUEST.value());
        }

        String linkStr = RandomUtil.randomString(80);
        redisClient.getBucket(linkStr).set("true", 30, TimeUnit.MINUTES);
        mailService.sendSimpleMail(email, "OneClick申请延期", "http://43.139.159.146/#/deferred?email=" + email + "&params=" + linkStr);
        return new Resp.Builder<String>().buildResult(SysConstantEnum.SUCCESS.getCode(), SysConstantEnum.SUCCESS.getValue());
    }

    @Override
    public Resp<String> applyForAnExtensionIn(ActivateAccountDto activateAccountDto) {
        return activateAccount(activateAccountDto, OneConstant.PASSWORD.APPLY_FOR_AN_EXTENSION);
    }

    /**
     * 管理员生成token
     *
     * @param sysUserTokenDto
     * @Param: []
     * @return: com.hu.oneclick.model.base.Resp<java.lang.String>
     * @Author: MaSiyi
     * @Date: 2021/11/10
     */
    @Override
    public Resp<SysUserToken> makeToken(SysUserTokenDto sysUserTokenDto) {
        AuthLoginUser userLoginInfo = jwtUserServiceImpl.getUserLoginInfo();

        String token = RandomUtil.randomString(50);

        SysUserToken sysUserToken = new SysUserToken();
        sysUserToken.setUserId(userLoginInfo.getSysUser().getId());
        String tokenName = sysUserTokenDto.getTokenName();
        sysUserToken.setTokenName(tokenName);
        sysUserToken.setTokenValue(token);
        Date expirationTime = sysUserTokenDto.getExpirationTime();
        Date nowDate = new Date();
        long datePoor3 = DateUtil.getDatePoor3(nowDate, expirationTime);
        sysUserToken.setExpirationTime(expirationTime);
        sysUserToken.setCreateTime(nowDate);
        sysUserToken.setIsDel(false);
        sysUserToken.setStatus(false);
        sysUserToken.setApiTimes(0L);
        sysUserToken.setCreateId(userLoginInfo.getSysUser().getId());


        RBucket<Object> bucket = redisClient.getBucket(OneConstant.REDIS_KEY_PREFIX.LOGIN + tokenName);
        if (bucket.isExists()) {
            bucket.delete();
        }
        bucket.set(JSON.toJSONString(userLoginInfo));
        bucket.expire(datePoor3, TimeUnit.MINUTES);


        sysUserTokenDao.insert(sysUserToken);
        return new Resp.Builder<SysUserToken>().setData(sysUserToken).ok();
    }

    /**
     * 获取生成的token列表
     *
     * @Param: []
     * @return: com.hu.oneclick.model.base.Resp<java.lang.String>
     * @Author: MaSiyi
     * @Date: 2021/11/10
     */
    @Override
    public Resp<List<SysUserToken>> listTokens() {
        AuthLoginUser userLoginInfo = jwtUserServiceImpl.getUserLoginInfo();
        List<SysUserToken> sysUserTokens = sysUserTokenDao.selectByUserId(userLoginInfo.getSysUser().getId());
        return new Resp.Builder<List<SysUserToken>>().setData(sysUserTokens).ok();
    }

    /**
     * 删除token
     *
     * @param tokenId
     * @Param: []
     * @return: com.hu.oneclick.model.base.Resp<java.lang.String>
     * @Author: MaSiyi
     * @Date: 2021/11/10
     */
    @Override
    public Resp<String> deleteToken(Integer tokenId) {
        int primaryKey = sysUserTokenDao.deleteByPrimaryKey(tokenId);
        return new Resp.Builder<String>().setData(primaryKey == 1 ? "删除成功" : "删除失败").ok();
    }

    @Override
    public Boolean getUserAccountInfo(String emailId, String token) {
        try {
            System.out.println(">>> API Token验证 - sysUserTokenDao.selectByUserIdAndToken 开始验证");
            System.out.println(">>> emailId: " + emailId);
            System.out.println(">>> token: " + token.substring(0, Math.min(10, token.length())) + "...");

            List<SysUserToken> tokens = sysUserTokenDao.selectByUserIdAndToken(emailId, token);
            System.out.println(">>> API Token验证 - 查询结果数量: " + (tokens != null ? tokens.size() : 0));

            if (tokens == null || tokens.isEmpty()) {
                System.out.println(">>> API Token验证失败: 未找到匹配的token记录");
                return false;
            }

            SysUserToken userToken = tokens.get(0);
            System.out.println(">>> API Token验证 - 找到token记录");

            // 检查token是否过期
            if (userToken.getExpirationTime() != null && userToken.getExpirationTime().before(new Date())) {
                System.out.println(">>> API Token验证失败: token已过期");
                return false;
            }

            // 检查token状态
            if (userToken.getStatus() == null || !userToken.getStatus()) {
                System.out.println(">>> API Token验证失败: token状态无效");
                return false;
            }

            // 检查是否被删除
            if (userToken.getIsDel() != null && userToken.getIsDel()) {
                System.out.println(">>> API Token验证失败: token已被删除");
                return false;
            }

            System.out.println(">>> API Token验证成功: token有效");
            return true;

        } catch (Exception e) {
            System.out.println(">>> API Token验证过程中发生异常: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 专门用于JWT Token认证后获取用户完整信息
     */
    public AuthLoginUser queryUserInfoForJwtToken(String username) {
        try {
            System.out.println(">>> JWT Token - 获取用户信息开始");
            System.out.println(">>> JWT Token - 用户名: " + username);

            SysUser sysUser = sysUserDao.selectByUserName(username);
            if (sysUser == null) {
                System.out.println(">>> JWT Token - 用户不存在: " +username);
                return null;
            }

            AuthLoginUser authLoginUser = new AuthLoginUser();
            authLoginUser.setSysUser(sysUser);

            System.out.println(">>> JWT Token - 获取用户信息成功");
            return authLoginUser;

        } catch (Exception e) {
            System.out.println(">>> JWT Token - 获取用户信息异常: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<SysUser> queryByUserIdAndParentId(String masterId) {
        SysUser sysUser = new SysUser();
        sysUser.setId(masterId);//        sysUser.setParentId(Long.valueOf(masterId));
        return sysUserDao.queryAllIdOrParentId(sysUser);
    }

    /**
     * 返回用户的激活次数
     *
     * @param email
     * @Param: [email]
     * @return: com.hu.oneclick.model.base.Resp<java.lang.String>
     * @Author: MaSiyi
     * @Date: 2021/12/18
     */
    @Override
    public Resp<String> getUserActivNumber(String email) {
        SysUser sysUser = sysUserDao.queryByEmail(email);
        return new Resp.Builder<String>().setData(String.valueOf(sysUser.getActivitiNumber())).ok();
    }

    /**
     * @param projectId 项目id
     * @return Resp<List < Map < String, Object>>>
     * @description 通过项目ID获取用户信息
     * @author Vince
     * @createTime 2022/12/24 19:56
     */
    @Override
    public Resp<List<Map<String, Object>>> listUserByProjectId(Long projectId) {
        List<Map<String, Object>> list = sysUserDao.listUserByProjectId(projectId);
        return new Resp.Builder<List<Map<String, Object>>>().setData(list).ok();
    }

    @Override
    public Resp<String> verifyLinkString(String linkString) {
        // 实现token验证逻辑
        try {
            // 这里应该实现具体的token验证逻辑
            // 返回验证结果，如果验证成功返回相应的字符串，失败返回null
            if (linkString == null || linkString.trim().isEmpty()) {
                return null;
            }

            // 简单的验证逻辑，实际项目中应该根据业务需求实现
            return new Resp.Builder<String>().setData(linkString).ok();
        } catch (Exception e) {
            logger.error("验证链接字符串失败", e);
            return null;
        }
    }

    @Override
    public Resp<AuthLoginUser> queryUserInfoForJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return new Resp.Builder<AuthLoginUser>().buildResult("401", "用户未认证");
        }

        String username = authentication.getName();
        if (username == null || username.trim().isEmpty()) {
            return new Resp.Builder<AuthLoginUser>().buildResult("400", "无法获取用户信息");
        }

        try {
            // 通过邮箱查询用户信息
            SysUser sysUser = sysUserDao.queryByEmail(username);
            if (sysUser == null) {
                return new Resp.Builder<AuthLoginUser>().buildResult("404", "用户不存在");
            }

            // 创建AuthLoginUser对象
            AuthLoginUser authLoginUser = new AuthLoginUser();
            authLoginUser.setSysUser(sysUser);
            authLoginUser.setUserId(sysUser.getId());
            authLoginUser.setUserName(sysUser.getUserName());
            authLoginUser.setUsername(sysUser.getEmail());

            // 获取用户项目信息
            List<SysUserProject> userProjects = sysUserProjectDao.selectByUserId(sysUser.getId());
            if (userProjects != null && !userProjects.isEmpty()) {
                SysUserProject userProject = userProjects.get(0);
                authLoginUser.setProjectid(userProject.getProjectId());
            }

            return new Resp.Builder<AuthLoginUser>().setData(authLoginUser).ok();

        } catch (Exception e) {
            logger.error("查询用户信息失败", e);
            return new Resp.Builder<AuthLoginUser>().buildResult("500", "查询用户信息失败: " + e.getMessage());
        }
    }
}