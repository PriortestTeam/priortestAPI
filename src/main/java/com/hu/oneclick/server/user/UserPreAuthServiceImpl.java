
package com.hu.oneclick.server.user;

import com.hu.oneclick.common.enums.SysConstantEnum;
import cn.hutool.core.util.RandomUtil;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
    
    @Value("${onclick.time.firstTime}")
    private Integer firstTime;
    
    @Override
    public Resp<String> register(RegisterBody registerBody) {
        logger.info(">>> 开始注册用户，邮箱: {}", registerBody.getEmail());
        
        if (StringUtils.isEmpty(registerBody.getEmail())) {
            return new Resp.Builder<String>().buildResult(SysConstantEnum.NOT_DETECTED_EMAIL.getCode(), SysConstantEnum.NOT_DETECTED_EMAIL.getValue());
        }
        
        List<SysUser> sysUsers = sysUserDao.queryByLikeEmail(registerBody.getEmail());
        if (!sysUsers.isEmpty()) {
            return new Resp.Builder<String>().buildResult(SysConstantEnum.ALREADY_HAS_EMAIL.getCode(), SysConstantEnum.ALREADY_HAS_EMAIL.getValue());
        }
        
        // 查询公司房间是否存在  
        Room room = roomDao.queryByCompanyName(registerBody.getCompany());
        if (room == null) {
            // 创建新房间
            room = new Room();
            room.setCreateName(registerBody.getUserName());
            room.setCreateUserEmail(registerBody.getEmail());
            room.setCompanyName(registerBody.getCompany());
            
            // 设置过期时间为30天后
            long expiredTime = System.currentTimeMillis() + (long) firstTime * 24 * 60 * 60 * 1000;
            room.setExpiredDate(new Date(expiredTime));
            
            room.setType(1);
            room.setModifyName(registerBody.getUserName());
            roomDao.insert(room);
        }
        
        // 创建用户
        SysUser sysUser = new SysUser();
        sysUser.setEmail(registerBody.getEmail());
        sysUser.setUserName(registerBody.getUserName());
        sysUser.setContactNo(registerBody.getContactNo());
        sysUser.setCompany(registerBody.getCompany());
        sysUser.setProfession(registerBody.getProfession());
        sysUser.setIndustry(registerBody.getIndustry());
        sysUser.setActiveState(5); // 未激活状态
        sysUser.setSysRoleId(4); // 默认角色
        sysUser.setRoomId(room.getId());
        sysUser.setPhoto(defaultPhoto);
        
        int result = sysUserDao.insert(sysUser);
        if (result > 0) {
            logger.info(">>> 准备发送激活邮件到: {}", registerBody.getEmail());
            
            // 生成激活链接
            String linkStr = RandomUtil.randomString(80);
            logger.info(">>> 激活链接参数: {}", linkStr);
            
            // 将激活码存储到Redis，30分钟过期
            redisClient.getBucket(linkStr).set("true", 30, TimeUnit.MINUTES);
            
            // 发送激活邮件
            String activationUrl = templateUrl + "?params=" + linkStr + "&email=" + registerBody.getEmail();
            try {
                mailService.sendEmail(registerBody.getEmail(), "账户激活", activationUrl);
                logger.info(">>> 激活 发邮件完毕: ");
                return new Resp.Builder<String>().buildResult(SysConstantEnum.SUCCESS.getCode(), SysConstantEnum.SUCCESS.getValue());
            } catch (Exception e) {
                logger.error(">>> 发送激活邮件失败: ", e);
                return new Resp.Builder<String>().buildResult(SysConstantEnum.FAILED.getCode(), "邮件发送失败");
            }
        } else {
            return new Resp.Builder<String>().buildResult(SysConstantEnum.FAILED.getCode(), "数据库操作失败");
        }
    }
}
