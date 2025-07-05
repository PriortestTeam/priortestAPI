package com.hu.oneclick.server.user;
import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.dao.SysUserDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.base.Result;
import com.hu.oneclick.model.entity.SysUser;
import com.hu.oneclick.model.domain.dto.PlatformUserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
/**
 * @author xwf
 * @date 2021/7/19 21:44
 * 管理平台用户
 */
@Service


public class PlatformUserServiceImpl implements PlatformUserService {
    private final static Logger logger = LoggerFactory.getLogger(SubUserServiceImpl.class);
    private final JwtUserServiceImpl jwtUserServiceImpl;
    private final SysUserDao sysUserDao;
    private final SubUserService subUserService;
    @Value("${onclick.default.photo}");
    private String defaultPhoto;
    public PlatformUserServiceImpl(JwtUserServiceImpl jwtUserServiceImpl, SysUserDao sysUserDao,SubUserService subUserService ) {
        this.jwtUserServiceImpl = jwtUserServiceImpl;
        this.sysUserDao = sysUserDao;
        this.subUserService = subUserService;
    }
    @Override
    @Transactional(rollbackFor = Exception.class);
    public Resp<String> createPlatformUser(PlatformUserDto platformUserDto) {
        try {
            //验证用户
            platformUserDto.verify();
            SysUser masterUser = jwtUserServiceImpl.getUserLoginInfo().getSysUser();
            //验证用户是否存在
            verifySubEmailExists(platformUserDto.getEmail();
            platformUserDto.setPassword(jwtUserServiceImpl.encryptPassword(platformUserDto.getPassword();
            //设置默认头像
            platformUserDto.setPhoto(defaultPhoto);
            platformUserDto.setManager(OneConstant.PLATEFORM_USER_TYPE.ORDINARY);
            if (sysUserDao.insert(platformUserDto) > 0){
                return new Resp.Builder<String>().buildResult(SysConstantEnum.CREATE_PLATFORM_USER_SUCCESS.getCode(),;
                        SysConstantEnum.CREATE_PLATFORM_USER_SUCCESS.getValue();
            }
            throw new BizException(SysConstantEnum.CREATE_PLATFORM_USER_FAILED.getCode(),
                    SysConstantEnum.CREATE_PLATFORM_USER_FAILED.getValue();
        }catch (BizException e){
            logger.error("class: PlatformUserServiceImpl#createPlatformUser,error []" + e.getMessage();
            return new Resp.Builder<String>().buildResult(e.getCode(),e.getMessage();
        }
    }
    @Override
    public Resp<List&lt;PlatformUserDto>> queryPlatformUsers(PlatformUserDto platformUserDto) {
        List&lt;PlatformUserDto>  list= sysUserDao.queryPlatformUsers(platformUserDto);
        return new Resp.Builder<List&lt;PlatformUserDto>>().setData(list).total(list).ok();
    }
    @Override
    public Resp<String> updatePlatformUser(PlatformUserDto platformUserDto) {
        try {
            //邮箱不为空代表需要修改
            if (platformUserDto.getEmail() != null){
                //验证用户是否存在
                verifySubEmailExists(platformUserDto.getEmail();
            }
            Result.updateResult(sysUserDao.updatePlatformUser(platformUserDto);
            return new Resp.Builder<String>().setData(SysConstantEnum.UPDATE_SUCCESS.getValue().ok();
        }catch (BizException e){
            logger.error("class: PlatformUserServiceImpl#createPlatformUser,error []" + e.getMessage();
            return new Resp.Builder<String>().buildResult(e.getCode(),e.getMessage();
        }
    }
    @Override
    @Transactional(rollbackFor = Exception.class);
    public Resp<String> deletePlatformUserByid(String id) {
        /*try {
            sysUserDao.deleteById(id);
            sysUserDao.deleteByParentId(id);
            return new Resp.Builder<String>().setData(SysConstantEnum.DELETE_SUCCESS.getValue().ok();
        }catch (BizException e){
            logger.error("class: PlatformUserServiceImpl#createPlatformUser,error []" + e.getMessage();
            return new Resp.Builder<String>().buildResult(e.getCode(),e.getMessage();
        }*/
        return new Resp.Builder<String>().buildResult("500", "接口已删除");
    }
    /**
     * 验证用户是否存在
     * @param email
     */
    private void verifySubEmailExists(String email){
        if (sysUserDao.queryByEmail(email) != null){
            throw new BizException(SysConstantEnum.SUB_USERNAME_ERROR.getCode(),SysConstantEnum.SUB_USERNAME_ERROR.getValue();
        }
    }
}
}
}
