
package com.hu.oneclick.server.user;

import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.constant.RoleConstant;
import com.hu.oneclick.common.util.SnowFlakeUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.bean.BeanUtil;
import com.hu.oneclick.controller.req.RegisterBody;
import com.hu.oneclick.dao.RoomDao;
import com.hu.oneclick.dao.SysUserDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.entity.Room;
import com.hu.oneclick.model.entity.SysUser;
import com.hu.oneclick.server.service.MailService;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
                    redisClient.getBucket(linkStr).set("true", 15, TimeUnit.MINUTES);
            
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
                redisClient.getBucket(linkStr).set("true", 15, TimeUnit.MINUTES);

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
        try {
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
            redisClient.getBucket(linkStr).set("true", 15, TimeUnit.MINUTES);
            System.out.println(">>> 准备发送忘记密码邮件");
            System.out.println(">>> 准备发送忘记密码激活链接参数 " + linkStr);
            mailService.sendSimpleMail(email, "PriorTest 忘记密码", templateUrl + "findpwd?email=" + email + "&params=" + linkStr);
            System.out.println(">>> 重置密码邮件发送完成");
            return new Resp.Builder<String>().buildResult(SysConstantEnum.SUCCESS.getCode(), SysConstantEnum.SUCCESS.getValue());
        } catch (Exception e) {
            logger.error("class: UserPreAuthServiceImpl#forgetThePassword,error []" + e.getMessage());
            return new Resp.Builder<String>().buildResult("500", "忘记密码处理失败");
        }
    }
}
