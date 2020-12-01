package com.hu.oneclick.server.user;

import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.dao.SysUserDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.SysUser;
import com.hu.oneclick.model.domain.dto.AuthLoginUser;
import com.hu.oneclick.model.domain.dto.SubUserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author qingyang
 */
@Service
public class SubUserServiceImpl implements SubUserService{

    private final static Logger logger = LoggerFactory.getLogger(SubUserServiceImpl.class);

    private final JwtUserServiceImpl jwtUserServiceImpl;

    private final SysUserDao sysUserDao;

    @Value("${onclick.default.photo}")
    private String defaultPhoto;

    public SubUserServiceImpl(JwtUserServiceImpl jwtUserServiceImpl, SysUserDao sysUserDao) {
        this.jwtUserServiceImpl = jwtUserServiceImpl;
        this.sysUserDao = sysUserDao;
    }

    @Override
    public Resp<List<SysUser>> querySubUsers(SubUserDto sysUser) {
        AuthLoginUser userLoginInfo = jwtUserServiceImpl.getUserLoginInfo();
        sysUser.setParentId(userLoginInfo.getSysUser().getId());
        List<SysUser> sysUsers = sysUserDao.querySubUsers(sysUser);
        return new Resp.Builder<List<SysUser>>().setData(sysUsers).total(sysUsers.size()).ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> createSubUser(SubUserDto sysUser) {
        try {
            sysUser.verify();
            SysUser masterUser = jwtUserServiceImpl.getUserLoginInfo().getSysUser();
            //拼接成员用户邮箱
            String subEmail = masterUser.getId() + OneConstant.COMMON.SUB_USER_SEPARATOR + sysUser.getEmail();

            sysUser.setEmail(subEmail);
            sysUser.setPassword(encodePassword(sysUser.getPassword()));
            //设置默认头像
            sysUser.setPhoto(defaultPhoto);
            sysUser.setType(OneConstant.USER_TYPE.SUB_USER);
            sysUser.setParentId(masterUser.getId());
            if (sysUserDao.insert(sysUser) > 0){
                return new Resp.Builder<String>().buildResult(SysConstantEnum.CREATE_SUB_USER_SUCCESS.getCode(),
                        SysConstantEnum.CREATE_SUB_USER_SUCCESS.getValue());
            }
            throw new BizException(SysConstantEnum.CREATE_SUB_USER_FAILED.getCode(),
                    SysConstantEnum.CREATE_SUB_USER_FAILED.getValue());
        }catch (BizException e){
            logger.error("class: SubUserServiceImpl#createSubUser,error []" + e.getMessage());
            return new Resp.Builder<String>().buildResult(e.getCode(),e.getMessage());
        }
    }

    /**
     * 密码加密
     * @param password
     * @return
     */
    private String encodePassword(String password){
        return jwtUserServiceImpl.encryptPassword(password);
    }

}
