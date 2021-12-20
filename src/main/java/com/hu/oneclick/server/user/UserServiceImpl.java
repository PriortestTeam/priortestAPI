package com.hu.oneclick.server.user;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.common.util.DateUtil;
import com.hu.oneclick.common.util.PasswordCheckerUtil;
import com.hu.oneclick.dao.MasterIdentifierDao;
import com.hu.oneclick.dao.SysUserDao;
import com.hu.oneclick.dao.SysUserTokenDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.base.Result;
import com.hu.oneclick.model.domain.MasterIdentifier;
import com.hu.oneclick.model.domain.Project;
import com.hu.oneclick.model.domain.SysUser;
import com.hu.oneclick.model.domain.SysUserToken;
import com.hu.oneclick.model.domain.UserUseOpenProject;
import com.hu.oneclick.model.domain.dto.ActivateAccountDto;
import com.hu.oneclick.model.domain.dto.AuthLoginUser;
import com.hu.oneclick.model.domain.dto.SubUserDto;
import com.hu.oneclick.model.domain.dto.SysProjectPermissionDto;
import com.hu.oneclick.model.domain.dto.SysUserTokenDto;
import com.hu.oneclick.server.service.MailService;
import com.hu.oneclick.server.service.ProjectService;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author qingyang
 */
@Service
public class UserServiceImpl implements UserService {

    private final static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final SysUserDao sysUserDao;

    private final MasterIdentifierDao masterIdentifierDao;

    private final JwtUserServiceImpl jwtUserServiceImpl;

    private final RedissonClient redisClient;

    private final MailService mailService;

    private final SysUserTokenDao sysUserTokenDao;

    private final ProjectService projectService;

    @Value("${onclick.default.photo}")
    private String defaultPhoto;

    @Value("${onclick.time.firstTime}")
    private long firstTime;

    @Value("${onclick.time.secondTime}")
    private long secondTime;


    public UserServiceImpl(SysUserDao sysUserDao, MasterIdentifierDao masterIdentifierDao, RedissonClient redisClient, JwtUserServiceImpl jwtUserServiceImpl, MailService mailService, SysUserTokenDao sysUserTokenDao, ProjectService projectService) {
        this.sysUserDao = sysUserDao;
        this.masterIdentifierDao = masterIdentifierDao;
        this.redisClient = redisClient;
        this.jwtUserServiceImpl = jwtUserServiceImpl;
        this.mailService = mailService;
        this.sysUserTokenDao = sysUserTokenDao;
        this.projectService = projectService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> register(SysUser registerUser) {
        try {

            String email = registerUser.getEmail();
            if (StringUtils.isEmpty(email)) {
                throw new BizException(SysConstantEnum.NOT_DETECTED_EMAIL.getCode(), SysConstantEnum.NOT_DETECTED_EMAIL.getValue());
            }

            SysUser user = new SysUser();
            BeanUtils.copyProperties(registerUser, user);
            //检查数据库是否已存在用户
            List<SysUser> sysUsers = sysUserDao.queryByLikeEmail(email);
            if (sysUsers.size() > 1) {
                return new Resp.Builder<String>().buildResult(SysConstantEnum.NO_DUPLICATE_REGISTER.getCode(), SysConstantEnum.NO_DUPLICATE_REGISTER.getValue());
            }
            for (SysUser sysUser : sysUsers) {
                if (!OneConstant.ACTIVE_STATUS.ACTIVE_GENERATION.equals(sysUser.getActiveState())) {
                    return new Resp.Builder<String>().buildResult(SysConstantEnum.NO_DUPLICATE_REGISTER.getCode(), SysConstantEnum.NO_DUPLICATE_REGISTER.getValue());
                } else if ( OneConstant.ACTIVE_STATUS.ACTIVE_GENERATION.equals(sysUser.getActiveState())) {
                    //邮箱链接失效
                    String linkStr = RandomUtil.randomString(80);
                    redisClient.getBucket(linkStr).set("true", 30, TimeUnit.MINUTES);
                    mailService.sendSimpleMail(email, "OneClick激活账号", "http://124.71.142.223/#/activate?email=" + email +
                            "&params=" + linkStr);
                    return new Resp.Builder<String>().buildResult(SysConstantEnum.REREGISTER_SUCCESS.getCode(), SysConstantEnum.REREGISTER_SUCCESS.getValue());
                }
            }

            //设置默认头像
            user.setPhoto(defaultPhoto);
            user.setType(OneConstant.USER_TYPE.ADMIN);
            user.setActiveState(OneConstant.ACTIVE_STATUS.ACTIVE_GENERATION);

            //设置主账号识别号，用于子用户登录
            MasterIdentifier masterIdentifier = masterIdentifierDao.queryOne();
            masterIdentifier = Optional.ofNullable(masterIdentifier).orElse(new MasterIdentifier());
            if (StringUtils.isEmpty(masterIdentifier.getId())) {
                masterIdentifier.setId(RandomUtil.randomNumbers(8));
                masterIdentifier.setFlag(0);
                masterIdentifierDao.insert(masterIdentifier);
            }
            user.setIdentifier(masterIdentifier.getId());
            if (sysUserDao.insert(user) > 0 && masterIdentifierDao.update(masterIdentifier.getId()) > 0) {
                String linkStr = RandomUtil.randomString(80);
                redisClient.getBucket(linkStr).set("true", 30, TimeUnit.MINUTES);

                mailService.sendSimpleMail(email, "OneClick激活账号", "http://124.71.142.223/#/activate?email=" + email +
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
        List<SubUserDto> subUserDtos = sysUserDao.queryByNameSubUsers(jwtUserServiceImpl.getMasterId(), subUserName);
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
            Project project = new Project();
            UserUseOpenProject userUseOpenProject = new UserUseOpenProject();
            userUseOpenProject.setProjectId(project.getId());
            userUseOpenProject.setUserId(sysUser.getId());
            userUseOpenProject.setTitle("初始化项目");
            project.setUserId(userUseOpenProject.getUserId());
            project.setTitle(userUseOpenProject.getTitle());
            project.setStatus("开发中");
            project.setDelFlag(0);
            project.setUpdateTime(new Date());
            project.setReportToName(sysUser.getUserName());
            projectService.initProject(project, userUseOpenProject);
        }
        //申请延期
        if (activation.equals(OneConstant.PASSWORD.APPLY_FOR_AN_EXTENSION)) {
            int activitiNumber = sysUser.getActivitiNumber() == null ? 0 : sysUser.getActivitiNumber();
            if (activitiNumber >= 1 && activitiNumber <= 3) {
                Date activitiDate = sysUser.getActivitiDate();
                long beginTime = sysUser.getExpireDate().getTime();
                long endTime = new Date(System.currentTimeMillis()).getTime();
                if (beginTime < endTime) {
                    sysUser.setActivitiDate(activitiDate);
                    sysUser.setActivitiNumber(activitiNumber + 1);
                    long time = activitiDate.getTime() + secondTime * 24 * 60 * 60 * 1000;
                    sysUser.setExpireDate(new Date(time));
                } else {
                    throw new BizException(SysConstantEnum.HAS_BEEN_ACTIVATED_ONCE.getCode(), SysConstantEnum.HAS_BEEN_ACTIVATED_ONCE.getValue());
                }
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
        mailService.sendSimpleMail(email, "OneClick忘记密码", "http://124.71.142.223/#/findpwd?email=" + email + "&params=" + linkStr);
        return new Resp.Builder<String>().buildResult(SysConstantEnum.SUCCESS.getCode(), SysConstantEnum.SUCCESS.getValue());
    }

    @Override
    public Resp<String> forgetThePasswordIn(ActivateAccountDto activateAccountDto) {
        return activateAccount(activateAccountDto, OneConstant.PASSWORD.FORGETPASSWORD);
    }

    @Override
    public Resp<String> applyForAnExtension(String email) {
        String linkStr = RandomUtil.randomString(80);
        redisClient.getBucket(linkStr).set("true", 30, TimeUnit.MINUTES);
        mailService.sendSimpleMail(email, "OneClick申请延期", "http://124.71.142.223/#/deferred?email=" + email + "&params=" + linkStr);
        return new Resp.Builder<String>().buildResult(SysConstantEnum.SUCCESS.getCode(), SysConstantEnum.SUCCESS.getValue());
    }

    @Override
    public Resp<String> applyForAnExtensionIn(ActivateAccountDto activateAccountDto) {
        return activateAccount(activateAccountDto, OneConstant.PASSWORD.APPLY_FOR_AN_EXTENSION);
    }

    @Override
    public Date getExpireDate(String id) {
        return sysUserDao.getExpireDate(id);
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
        sysUserToken.setUser_id(userLoginInfo.getSysUser().getId());
        String tokenName = sysUserTokenDto.getTokenName();
        sysUserToken.setToken_name(tokenName);
        sysUserToken.setToken_value(token);
        Date expirationTime = sysUserTokenDto.getExpirationTime();
        Date nowDate = new Date();
        long datePoor3 = DateUtil.getDatePoor3(nowDate, expirationTime);
        sysUserToken.setExpiration_time(expirationTime);
        sysUserToken.setCreate_time(nowDate);
        sysUserToken.setIs_del(false);
        sysUserToken.setStatus(false);
        sysUserToken.setApi_times(0L);
        sysUserToken.setCreate_id(userLoginInfo.getSysUser().getId());


        RBucket<Object> bucket = redisClient.getBucket(OneConstant.REDIS_KEY_PREFIX.LOGIN + tokenName);
        if (bucket.isExists()) {
            bucket.delete();
        }
        bucket.set(JSONObject.toJSONString(userLoginInfo));
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
    public Boolean getUserAccountInfo(String emailId) {
        SysUser sysUser = sysUserDao.queryByEmail(emailId);
        if (org.springframework.util.StringUtils.isEmpty(sysUser)) {
            return false;
        }
        String identifier = sysUser.getIdentifier();

        //如果为空，则是子账号
        if (StringUtils.isEmpty(identifier)) {
            //查询是否有权限
            SysUser parentUser = sysUserDao.queryById(sysUser.getParentId());
            if (sysUserTokenDao.selectByUserId(parentUser.getId()).isEmpty()) {
                return false;
            }
        }
        //主账号
        List<SysUserToken> sysUserTokens = sysUserTokenDao.selectByUserId(sysUser.getId());
        for (SysUserToken sysUserToken : sysUserTokens) {
            if (sysUserToken.getApi_times() > 0) {
                sysUserTokenDao.decreaseApiTimes(sysUserToken.getId());
            }
        }
        return !sysUserTokens.isEmpty();
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
     *
     * @param masterId
     * @Param: [masterId]
     * @return: java.util.List<com.hu.oneclick.model.domain.SysUser>
     * @Author: MaSiyi
     * @Date: 2021/12/15
     */
    @Override
    public List<SysUser> queryByUserIdAndParentId(String masterId) {
        SysUser sysUser = new SysUser();
        sysUser.setId(masterId);
        sysUser.setParentId(masterId);


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
}
