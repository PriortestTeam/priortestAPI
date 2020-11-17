package com.hu.oneclick.server.user;

import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.common.util.NumberUtil;
import com.hu.oneclick.dao.SysUserDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.SysUser;
import com.hu.oneclick.model.domain.dto.RegisterUser;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author qingyang
 */
@Service
public class UserServiceImpl implements UserService{

    private final static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final SysUserDao sysUserDao;

    private final JwtUserServiceImpl jwtUserServiceImpl;

    private final RedissonClient redisClient;


    @Value("${onclick.default.photo}")
    private String defaultPhoto;
    public UserServiceImpl(SysUserDao sysUserDao, RedissonClient redisClient, JwtUserServiceImpl jwtUserServiceImpl) {
        this.sysUserDao = sysUserDao;
        this.redisClient = redisClient;
        this.jwtUserServiceImpl = jwtUserServiceImpl;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> register(RegisterUser registerUser) {
        try {
            registerUser.verify();
            //注册，校验code码
            String email = registerUser.getEmail();
            String redisKey = "register_send_email" + email;
            RBucket<String> bucket = redisClient.getBucket(redisKey);
            String code = bucket.get();
            if (code == null || "".equals(code) || !registerUser.getEmailCode().equals(code)){
                return new Resp.Builder<String>().buildResult(SysConstantEnum.VERIFY_CODE_ERROR.getCode(), SysConstantEnum.VERIFY_CODE_ERROR.getValue());
            }
            SysUser user = new SysUser();
            BeanUtils.copyProperties(registerUser,user);
            //检查数据库是否已存在用户
            if (sysUserDao.queryByEmail(email) != null){
                return new Resp.Builder<String>().buildResult(SysConstantEnum.NO_DUPLICATE_REGISTER.getCode(), SysConstantEnum.NO_DUPLICATE_REGISTER.getValue());
            }

            //设置密码
            user.setPassword(jwtUserServiceImpl.encryptPassword(user.getPassword()));
            //设置默认头像
            user.setPhoto(defaultPhoto);
            user.setRegisterDate(new Date());
            user.setType(OneConstant.USER_TYPE.admin);
            user.setActiveState(OneConstant.ACTIVE_STATUS.trial);
            user.setCreateTime(new Date());
            if (sysUserDao.insert(user) > 0){
                bucket.delete();
                return new Resp.Builder<String>().buildResult(SysConstantEnum.REGISTER_SUCCESS.getCode(), SysConstantEnum.REGISTER_SUCCESS.getValue());
            }
            throw new BizException(SysConstantEnum.REGISTER_FAILED.getCode(), SysConstantEnum.REGISTER_FAILED.getValue());
        }catch (BizException e){
            logger.error("class: UserServiceImpl#register,error []" + e.getMessage());
            return new Resp.Builder<String>().buildResult(e.getCode(),e.getMessage());
        }
    }

    @Override
    public Resp<String> sendEmailCode(String email) {
        try {
            String redisKey = "register_send_email" + email;
            RBucket<String> bucket = redisClient.getBucket(redisKey);
            String s = bucket.get();
            if (s != null && !"".equals(s)){
                return new Resp.Builder<String>().buildResult(SysConstantEnum.PLEASE_TRY_AGAIN_LATER.getCode(),
                        SysConstantEnum.PLEASE_TRY_AGAIN_LATER.getValue());
            }
            String verifyCode = NumberUtil.getVerifyCode();
            bucket.set(verifyCode);
            //设置超时1分钟
            bucket.expire(1,TimeUnit.MINUTES);
            return new Resp.Builder<String>().ok();
        }catch (Exception e){
            logger.error("class: UserServiceImpl#sendEmailCode,error []" + e.getMessage());
            return new Resp.Builder<String>().buildResult(SysConstantEnum.SYS_ERROR.getCode(), SysConstantEnum.SYS_ERROR.getValue(),e.getMessage());
        }

    }
}
