package com.hu.oneclick.common.security.service;

import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.model.domain.dto.AuthLoginUser;
import com.hu.oneclick.model.domain.dto.SysProjectPermissionDto;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author qingyang
 */
@Service("ps")
public class SysPermissionService {

    private final JwtUserServiceImpl jwtUserServiceImpl;

    public SysPermissionService(JwtUserServiceImpl jwtUserServiceImpl) {
        this.jwtUserServiceImpl = jwtUserServiceImpl;
    }

    /**
     * 判断用户是否具有后台管理员的权限
     */
    public boolean backstageManagement(){
        AuthLoginUser userLoginInfo = jwtUserServiceImpl.getUserLoginInfo();
        Integer manager = userLoginInfo.getSysUser().getManager();
        return manager.equals(OneConstant.PLATEFORM_USER_TYPE.MANAGER);
    }

    /**
     * 判断用户是否具有管理子用户权限
     */
    public boolean manageSubUsers(){
        AuthLoginUser userLoginInfo = jwtUserServiceImpl.getUserLoginInfo();
        Integer manager = userLoginInfo.getSysUser().getManager();
        Integer userType = userLoginInfo.getSysUser().getType();
        if (manager.equals(OneConstant.PLATEFORM_USER_TYPE.MANAGER) || manager.equals(OneConstant.PLATEFORM_USER_TYPE.ORDINARY)){
            return true;
        }
        return manager.equals(OneConstant.PLATEFORM_USER_TYPE.SUB) && userType.equals(OneConstant.USER_TYPE.ADMIN);
    }


    /**
     * 判断用户是否有权限操作该接口信息
     * @return
     */
    public void hasPermission(String parent, String sub, String projectId){
        AuthLoginUser userLoginInfo = jwtUserServiceImpl.getUserLoginInfo();
        Integer manage = userLoginInfo.getSysUser().getManager();
        //后台管理员和平台注册用户具有所有权限
        if (manage.equals(OneConstant.PLATEFORM_USER_TYPE.ORDINARY) || manage.equals(OneConstant.PLATEFORM_USER_TYPE.MANAGER)){
            return;
        }
        List<SysProjectPermissionDto> permissions = userLoginInfo.getPermissions();
        if (permissions == null || permissions.size() <= 0){
            throw new BizException(SysConstantEnum.NOT_PERMISSION.getCode(),SysConstantEnum.NOT_PERMISSION.getValue());
        }
        //判断用户权限，存在返回true
        for (SysProjectPermissionDto subPermission : permissions) {
            if (subPermission.getMarkName().equals(sub) && subPermission.getProjectId().equals(projectId)){
                for (SysProjectPermissionDto parentPermission : permissions) {
                    if (subPermission.getParentId().equals(parentPermission.getOperationAuthId())
                            && parentPermission.getMarkName().equals(parent)){
                        return;
                    }
                }
            }
        }
        throw new BizException(SysConstantEnum.NOT_PERMISSION.getCode(),SysConstantEnum.NOT_PERMISSION.getValue());
    }
}
