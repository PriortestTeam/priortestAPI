package com.hu.oneclick.server.user;

import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.common.util.NumberUtil;
import com.hu.oneclick.common.util.PasswordCheckerUtil;
import com.hu.oneclick.dao.MasterIdentifierDao;
import com.hu.oneclick.dao.SysUserDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.MasterIdentifier;
import com.hu.oneclick.model.domain.SysUser;
import com.hu.oneclick.model.domain.dto.ActivateAccountDto;
import com.hu.oneclick.model.domain.dto.AuthLoginUser;
import com.hu.oneclick.model.domain.dto.SubUserDto;
import com.hu.oneclick.model.domain.dto.SysProjectPermissionDto;
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


    @Value("${onclick.default.photo}")
    private String defaultPhoto;

    public UserServiceImpl(SysUserDao sysUserDao, MasterIdentifierDao masterIdentifierDao, RedissonClient redisClient, JwtUserServiceImpl jwtUserServiceImpl) {
        this.sysUserDao = sysUserDao;
        this.masterIdentifierDao = masterIdentifierDao;
        this.redisClient = redisClient;
        this.jwtUserServiceImpl = jwtUserServiceImpl;
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
            if (sysUserDao.queryByEmail(email) != null) {
                return new Resp.Builder<String>().buildResult(SysConstantEnum.NO_DUPLICATE_REGISTER.getCode(), SysConstantEnum.NO_DUPLICATE_REGISTER.getValue());
            }
            //设置默认头像
            user.setPhoto(defaultPhoto);
            user.setType(OneConstant.USER_TYPE.ADMIN);
            user.setActiveState(OneConstant.ACTIVE_STATUS.ACTIVE_GENERATION);

            //设置主账号识别号，用于子用户登录
            MasterIdentifier masterIdentifier = masterIdentifierDao.queryOne();
            if (masterIdentifier == null) {
                throw new BizException(SysConstantEnum.SYS_ERROR.getCode(), SysConstantEnum.SYS_ERROR.getValue());
            }
            user.setIdentifier(masterIdentifier.getId());
            if (sysUserDao.insert(user) > 0 && masterIdentifierDao.update(masterIdentifier.getId()) > 0) {
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
    public Resp<String> sendEmailCode(String email, String prefix) {
        try {
            if (prefix.equals(OneConstant.REDIS_KEY_PREFIX.RESET_PASSWORD)) {
                SysUser user = sysUserDao.queryByEmail(email);
                if (user == null) {
                    return new Resp.Builder<String>().buildResult(SysConstantEnum.NOT_DETECTED_EMAIL.getCode(),
                            SysConstantEnum.NOT_DETECTED_EMAIL.getValue());
                }
            }
            String redisKey = prefix + email;
            RBucket<String> bucket = redisClient.getBucket(redisKey);
            String s = bucket.get();
            if (s != null && !"".equals(s)) {
                return new Resp.Builder<String>().buildResult(SysConstantEnum.PLEASE_TRY_AGAIN_LATER.getCode(),
                        SysConstantEnum.PLEASE_TRY_AGAIN_LATER.getValue());
            }
            String verifyCode = NumberUtil.getVerifyCode();
            bucket.set(verifyCode);
            //设置超时1分钟
            bucket.expire(1, TimeUnit.MINUTES);
            sendEmail();
            return new Resp.Builder<String>().ok();
        } catch (Exception e) {
            logger.error("class: UserServiceImpl#sendEmailCode,error []" + e.getMessage());
            return new Resp.Builder<String>().buildResult(SysConstantEnum.SYS_ERROR.getCode(), SysConstantEnum.SYS_ERROR.getValue(), e.getMessage());
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

    private void sendEmail() {

    }

    @Override
    public Resp<String> activateAccount(ActivateAccountDto activateAccountDto) {
        if (StringUtils.isEmpty(activateAccountDto.getEmail())) {
            return new Resp.Builder<String>().buildResult(SysConstantEnum.NOT_DETECTED_EMAIL.getCode(), SysConstantEnum.NOT_DETECTED_EMAIL.getValue());

        }
        //检查数据库是否已存在用户
        SysUser sysUser = sysUserDao.queryByEmail(activateAccountDto.getEmail());
        if ( sysUser == null) {
            return new Resp.Builder<String>().buildResult(SysConstantEnum.NOUSER_ERROR.getCode(), SysConstantEnum.NOUSER_ERROR.getValue());
        }
        if (!activateAccountDto.getPassword().equals(activateAccountDto.getRePassword())) {
            return new Resp.Builder<String>().buildResult(SysConstantEnum.REPASSWORD_ERROR.getCode(), SysConstantEnum.REPASSWORD_ERROR.getValue());
        }
        PasswordCheckerUtil passwordChecker = new PasswordCheckerUtil();
        if (!passwordChecker.check(activateAccountDto.getPassword())) {
            throw new BizException(SysConstantEnum.PASSWORD_RULES.getCode(), SysConstantEnum.PASSWORD_RULES.getValue());
        }
        sysUser.setActiveState(OneConstant.ACTIVE_STATUS.TRIAL);
        sysUser.setActivitiDate(new Date(System.currentTimeMillis()));
        sysUser.setPassword(encodePassword(activateAccountDto.getPassword()));
        if (sysUserDao.update(sysUser) == 0) {
            throw new BizException(SysConstantEnum.UPDATE_FAILED.getCode(), SysConstantEnum.UPDATE_FAILED.getValue());
        }

        return new Resp.Builder<String>().buildResult(SysConstantEnum.ACTIVATION_SUCCESS.getCode(), SysConstantEnum.ACTIVATION_SUCCESS.getValue());
    }
}
