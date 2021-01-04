package com.hu.oneclick.server.service.impl;

import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.constant.TwoConstant;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.common.security.service.SysPermissionService;
import com.hu.oneclick.dao.ViewDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.base.Result;
import com.hu.oneclick.model.domain.SysUser;
import com.hu.oneclick.model.domain.View;
import com.hu.oneclick.server.service.ViewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author qingyang
 */
@Service
public class ViewServiceImpl implements ViewService {

    private final static Logger logger = LoggerFactory.getLogger(ViewServiceImpl.class);

    private final ViewDao viewDao;

    private final JwtUserServiceImpl jwtUserService;

    private final SysPermissionService sysPermissionService;

    public ViewServiceImpl(ViewDao v, JwtUserServiceImpl jwtUserService, SysPermissionService sysPermissionService) {
        this.viewDao = v;
        this.jwtUserService = jwtUserService;
        this.sysPermissionService = sysPermissionService;
    }

    @Override
    public Resp<List<View>> list(View view) {
        if (view.getScope() == null){
            return new Resp.Builder<List<View>>().buildResult("scope 不能为空");
        }
        sysPermissionService.viewPermission(OneConstant.PERMISSION.ADD,convertPermission(view.getScope()));
        SysUser sysUser = jwtUserService.getUserLoginInfo().getSysUser();
        view.verifyUserType(sysUser.getManager());
        List<View> views = viewDao.queryAll(view);
        return new Resp.Builder<List<View>>().setData(views).total(views.size()).ok();
    }

    @Override
    public Resp<String> addView(View view) {
        try {
            sysPermissionService.viewPermission(OneConstant.PERMISSION.ADD,convertPermission(view.getScope()));
            view.verify();
            SysUser sysUser = jwtUserService.getUserLoginInfo().getSysUser();
            String projectId = sysUser.getUserUseOpenProject().getProjectId();
            view.setUserId(jwtUserService.getMasterId());
            view.setProjectId(projectId);
            view.setOwner(sysUser.getUserName());
            return Result.addResult(viewDao.insert(view));
        }catch (BizException e){
            logger.error("class: ViewServiceImpl#addView,error []" + e.getMessage());
            return new Resp.Builder<String>().buildResult(e.getCode(),e.getMessage());
        }
    }

    @Override
    public Resp<String> updateView(View view) {
        try {
            sysPermissionService.viewPermission(OneConstant.PERMISSION.EDIT,convertPermission(view.getScope()));
            view.verifyOneFilter();
            SysUser sysUser = jwtUserService.getUserLoginInfo().getSysUser();
            view.setModifyUser(sysUser.getUserName());
            view.setModifyDate(new Date());
            return Result.updateResult(viewDao.update(view));
        }catch (BizException e){
            logger.error("class: ViewServiceImpl#updateView,error []" + e.getMessage());
            return new Resp.Builder<String>().buildResult(e.getCode(),e.getMessage());
        }
    }

    @Override
    public Resp<String> deleteView(String id) {
        try {
            View view = viewDao.queryById(id);
            if(view == null){
                return Result.deleteResult(0);
            }
            sysPermissionService.viewPermission(OneConstant.PERMISSION.DELETE,convertPermission(view.getScope()));
            return Result.updateResult(viewDao.deleteById(jwtUserService.getMasterId(),id));
        }catch (BizException e){
            logger.error("class: ViewServiceImpl#deleteView,error []" + e.getMessage());
            return new Resp.Builder<String>().buildResult(e.getCode(),e.getMessage());
        }
    }


    /**
     * 根据用户选定的scope 转换成权限标识符
     * @return
     */
    private String convertPermission(String scope){
        return TwoConstant.convertPermission(scope);
    }
}
