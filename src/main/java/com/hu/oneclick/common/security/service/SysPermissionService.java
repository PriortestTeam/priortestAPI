package com.hu.oneclick.common.security.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.model.domain.SysUser;
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
     * v2 去掉用子账户 功能限制
     */
    public boolean manageSubUsers(){
        return true;
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
        //父级权限
        verifyParentPermission(permissions,parent,projectId);
        //子级权限
        verifySubPermission(permissions,sub,projectId);
    }

    /**
     * 验证是否有父级权限
     */
    private void verifyParentPermission(List<SysProjectPermissionDto> permissions,String parent, String projectId){
        for (SysProjectPermissionDto permission : permissions) {
            if (permission.getProjectId().equals(projectId) &&
                    permission.getMarkName().equals(parent) && "0".equals(permission.getParentId())){
                return;
            }
        }
        throw new BizException(SysConstantEnum.NOT_PERMISSION.getCode(),SysConstantEnum.NOT_PERMISSION.getValue() + parent + "。");
    }

    /**
     * 验证是否有子级权限
     */
    private void verifySubPermission(List<SysProjectPermissionDto> permissions,String sub, String projectId){
        if (sub == null){
            return;
        }
        for (SysProjectPermissionDto permission : permissions) {
            if (permission.getMarkName().equals(sub) && permission.getProjectId().equals(projectId)){
                return;
            }
        }
        throw new BizException(SysConstantEnum.NOT_PERMISSION.getCode(),SysConstantEnum.NOT_PERMISSION.getValue() + sub + "。");
    }







    /**
     * 验证项目权限
     * @param area
     */
    public void projectPermission(String area){
        SysUser sysUser = jwtUserServiceImpl.getUserLoginInfo().getSysUser();
        String projectId = sysUser.getUserUseOpenProject().getProjectId();
        if(projectId == null){
            throw new BizException(SysConstantEnum.NOT_PROJECT.getCode(),SysConstantEnum.NOT_PROJECT.getValue());
        }
        hasPermission(OneConstant.PERMISSION.PROJECT,
                area,projectId);
    }

    /**
     * 验证view 的权限
     * @param
     * @param
     */
    public void viewPermission(String projectSub, String viewParent){
        SysUser sysUser = jwtUserServiceImpl.getUserLoginInfo().getSysUser();
        String projectId = sysUser.getUserUseOpenProject().getProjectId();
        if(projectId == null){
            throw new BizException(SysConstantEnum.NOT_PROJECT.getCode(),SysConstantEnum.NOT_PROJECT.getValue());
        }
        //先验证项目权限
        hasPermission(OneConstant.PERMISSION.PROJECT,
                projectSub,projectId);
        //验证view 权限
        hasPermission(viewParent,
                OneConstant.PERMISSION.VIEW,projectId);
    }

    /**
     * 故事控制器
     */
    public void featurePermission(String projectSub, String featureParent) {
        SysUser sysUser = jwtUserServiceImpl.getUserLoginInfo().getSysUser();
        String projectId = sysUser.getUserUseOpenProject().getProjectId();
        if(projectId == null){
            throw new BizException(SysConstantEnum.NOT_PROJECT.getCode(),SysConstantEnum.NOT_PROJECT.getValue());
        }
        //先验证项目权限
        hasPermission(OneConstant.PERMISSION.PROJECT,
                projectSub,projectId);
        //验证view 权限
        hasPermission(featureParent,
                OneConstant.PERMISSION.ONE_FEATURE,projectId);

    }
}
