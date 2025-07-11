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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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

     @Value("${onclick.template.url}")
        private String templateUrl;

    private final UserPreAuthService userPreAuthService;

    private final PasswordEncoder passwordEncoder;


    public UserServiceImpl(SysUserDao sysUserDao,
                           RedissonClient redisClient, JwtUserServiceImpl jwtUserServiceImpl,
                           MailService mailService, SysUserTokenDao sysUserTokenDao, ProjectService projectService,
                           SubUserProjectDao subUserProjectDao, UserOrderService userOrderService,
                           SystemConfigService systemConfigService, RoomDao roomDao, RoleFunctionDao roleFunctionDao,
                           SysUserBusinessDao sysUserBusinessDao, SysRoleDao sysRoleDao, SysUserProjectDao sysUserProjectDao,
                           UserPreAuthService userPreAuthService, PasswordEncoder passwordEncoder) {
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
        this.userPreAuthService = userPreAuthService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Resp<String> register(RegisterBody registerBody) {
        // 委托给UserPreAuthService处理
        return userPreAuthService.register(registerBody);
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
        AuthLoginUser userLoginInfo = jwtUserServiceImpl.getUserLoginInfo();
        SysUser sysUser = userLoginInfo.getSysUser();
        sysUser.setPassword("");
        return new Resp.Builder<SysUser>().setData(sysUser).ok();
    }

    @Override
    public Resp<List<SysProjectPermissionDto>> queryUserPermissions() {
        AuthLoginUser userLoginInfo = jwtUserServiceImpl.getUserLoginInfo();
        return new Resp.Builder<List<SysProjectPermissionDto>>().setData(userLoginInfo.getPermissions()).ok();
    }

    @Override
    public Resp<List<SubUserDto>> queryByNameSubUsers(String subUserName) {
        List<SubUserDto> subUserDtos = CollUtil.newArrayList();
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
    private int updatePassword(SysUser user) {
        int update = sysUserDao.updatePassword(user);
        if (update <= 0) {
            throw new BizException(SysConstantEnum.FAILED.getCode(), SysConstantEnum.FAILED.getValue());
        }
        return update;
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
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> activateAccount(ActivateAccountDto activateAccountDto, String activation) {
        System.out.println(">>> UserServiceImpl.activateAccount() 开始执行");
        System.out.println(">>> activation类型: " + activation);
        try {
            Resp<String> result = activateAccountInternal(activateAccountDto, activation);
            System.out.println(">>> UserServiceImpl.activateAccount() 内部方法执行完成，返回: " + result);
            return result;
        } catch (Exception e) {
            System.out.println(">>> UserServiceImpl.activateAccount() 发生异常: " + e.getMessage());
            logger.error("activateAccount error", e);
            return new Resp.Builder<String>().buildResult(SysConstantEnum.FAILED.getCode(), "操作失败", HttpStatus.BAD_REQUEST.value());
        }
    }

    // 保留原来的activateAccount方法作为内部方法，供反射调用
    private Resp<String> activateAccountInternal(ActivateAccountDto activateAccountDto, String activation) {
        System.out.println(">>> UserServiceImpl.activateAccountInternal() 开始执行");
        System.out.println(">>> 邮箱: " + activateAccountDto.getEmail());
        System.out.println(">>> activation类型: " + activation);

            // 根据activation类型选择合适的查询方法
            if (OneConstant.PASSWORD.FORGETPASSWORD.equals(activation)) {
                // 忘记密码功能使用优化的查询方法
                SysUser sysUser = sysUserDao.queryUserBasicInfoByEmail(activateAccountDto.getEmail());
                System.out.println(">>> 使用优化查询，查询到用户: " + (sysUser != null ? "是" : "否"));

                if (sysUser == null) {
                    return new Resp.Builder<String>().buildResult(SysConstantEnum.NOUSER_ERROR.getCode(), SysConstantEnum.NOUSER_ERROR.getValue());
                }
                System.out.println(">>> 开始执行verify方法，用户ID: " + sysUser.getId());
                return verify(sysUser, activateAccountDto, activation);
            } else {
                // 其他功能继续使用原有方法
                List<SysUser> sysUsers = sysUserDao.queryByLikeEmail(activateAccountDto.getEmail());
                System.out.println(">>> 查询到用户数量: " + sysUsers.size());

                if (sysUsers.isEmpty()) {
                    return new Resp.Builder<String>().buildResult(SysConstantEnum.NOUSER_ERROR.getCode(), SysConstantEnum.NOUSER_ERROR.getValue());
                }
                SysUser sysUser = sysUsers.get(0);
                System.out.println(">>> 开始执行verify方法，用户ID: " + sysUser.getId());
                return verify(sysUser, activateAccountDto, activation);
            }
    }

    private Resp<String> verify(SysUser sysUser, ActivateAccountDto activateAccountDto, String activation) {
        System.out.println(">>> UserServiceImpl.verify() 开始执行");
        System.out.println(">>> activation类型: " + activation);

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

            QueryWrapper<SysUserProject> query = Wrappers.query();
            query.eq("user_id", userId);
            List<SysUserProject> userProjects = sysUserProjectDao.selectList(query);
            if (userProjects.isEmpty()) {            

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

        //忘记密码
        if (activation.equals(OneConstant.PASSWORD.FORGETPASSWORD)) {
            String encodedPassword = passwordEncoder.encode(activateAccountDto.getPassword());
            // 使用优化的密码更新方法，只更新密码和更新时间
            int updateResult = sysUserDao.updateUserPasswordOnly(sysUser.getId(), encodedPassword);
            if (updateResult > 0) {
                return new Resp.Builder<String>().buildResult(SysConstantEnum.SUCCESS.getCode(), SysConstantEnum.SUCCESS.getValue());
            } else {
                return new Resp.Builder<String>().buildResult(SysConstantEnum.FAILED.getCode(), "密码更新失败");
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
    @Override
    public void initOrder(String userId) {
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
        // 委托给UserPreAuthService处理
        return userPreAuthService.forgetThePassword(email);
    }

    @Override
    public Resp<String> forgetThePasswordIn(ActivateAccountDto activateAccountDto) {
        return activateAccount(activateAccountDto, OneConstant.PASSWORD.FORGETPASSWORD);
    }

    @Override
    public Resp<String> applyForAnExtension(String email) {
        // 委托给UserPreAuthService处理
        return userPreAuthService.applyForAnExtension(email);
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

    /**
     * 获取用户账号信息
     *
     * @param emailId
     * @Param: [emailId]
     * @return: void
     * @Author: MaSiyi
     * @Date: 2021/11/10
     */
    @Override
    public Boolean getUserAccountInfo(String emailId, String token) {
        List<SysUser> sysUsers = sysUserDao.queryByLikeEmail(emailId);
        SysUser sysUser;
        if (sysUsers.isEmpty()) {
            return false;
        }
        sysUser = sysUsers.get(0);

        {
            //主账号
            List<SysUserToken> sysUserTokens = sysUserTokenDao.selectByUserIdAndToken(sysUser.getId(), token);
            if (sysUserTokens.isEmpty()) {
                return false;
            }
            for (SysUserToken sysUserToken : sysUserTokens) {
                if (sysUserToken.getApiTimes() > 0) {
                    sysUserTokenDao.decreaseApiTimes(sysUserToken.getId());
                }
            }
        }
        return true;
    }

    @Override
    public Resp<String> verifyLinkString(String linkStr) {
        RBucket<String> bucket = redisClient.getBucket(linkStr);
        String redisCode = bucket.get();
        if (redisCode == null || "".equals(redisCode) || !"true".equals(redisCode)) {
            throw new BizException(SysConstantEnum.LINKSTRERROR.getCode(), SysConstantEnum.LINKSTRERROR.getValue());
        }
        bucket.delete();
        return new Resp.Builder<String>().ok();
    }

    /**
     * 查询用户和子用户

     * @param masterId
     * @Param: [masterId]
     * @return: java.util.List<com.hu.oneclick.model.entity.SysUser>
     * @Author: MaSiyi
     * @Date: 2021/12/15
     */
    @Override
    public List<SysUser> queryByUserIdAndParentId(String masterId) {
        SysUser sysUser = new SysUser();
        sysUser.setId(masterId);
//        sysUser.setParentId(Long.valueOf(masterId));
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
}