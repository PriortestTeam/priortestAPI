package com.hu.oneclick.server.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.constant.TwoConstant;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.common.security.service.SysPermissionService;
import com.hu.oneclick.dao.ViewDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.base.Result;
import com.hu.oneclick.model.domain.OneFilter;
import com.hu.oneclick.model.domain.SysUser;
import com.hu.oneclick.model.domain.View;
import com.hu.oneclick.server.service.ViewService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Resp<View> queryById(String id) {
        View queryView = viewDao.queryById(id, jwtUserService.getMasterId());
        //防止mybatis 缓存数据变更
        View view = new View();
        BeanUtils.copyProperties(queryView,view);
        view.setOneFilters(TwoConstant.convertToList(view.getFilter(), OneFilter.class));
        view.setFilter("");
        return new Resp.Builder<View>().setData(view).ok();
    }

    @Override
    public Resp<List<View>> list(View view) {
        if (StringUtils.isEmpty(view.getScope())){
            return new Resp.Builder<List<View>>().buildResult("scope 不能为空。");
        }else if (StringUtils.isEmpty(view.getProjectId())){
            return new Resp.Builder<List<View>>().buildResult("项目ID不能为空。");
        }
        sysPermissionService.viewPermission(null,convertPermission(view.getScope()));
        SysUser sysUser = jwtUserService.getUserLoginInfo().getSysUser();
        view.verifyUserType(sysUser.getManager());
        view.setUserId(jwtUserService.getMasterId());

        List<View> queryViews = viewDao.queryAll(view);

        //防止mybatis 缓存数据变更
        List<View> views = coverViews(queryViews);

        views.forEach(e->{
            e.setOneFilters(TwoConstant.convertToList(e.getFilter(), OneFilter.class));
            e.setFilter("");
        });
        return new Resp.Builder<List<View>>().setData(views).total(views.size()).ok();
    }

    /**
     * 深copy
     * @param queryViews
     * @return
     */
    private List<View> coverViews(List<View> queryViews){
        String s = JSONObject.toJSONString(queryViews);
        return JSONObject.parseArray(s, View.class);
    }


    @Override
    public Resp<String> queryDoesExistByTitle(String projectId, String title, String scope) {
        try {
            Result.verifyDoesExist(queryByTitle(projectId,title,scope),title);
            return new Resp.Builder<String>().ok();
        }catch (BizException e){
            return new Resp.Builder<String>().buildResult(e.getCode(),e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> addView(View view) {
        try {
            view.verify();
            sysPermissionService.viewPermission(OneConstant.PERMISSION.ADD,convertPermission(view.getScope()));
            SysUser sysUser = jwtUserService.getUserLoginInfo().getSysUser();
            String projectId = sysUser.getUserUseOpenProject().getProjectId();
            if (StringUtils.isEmpty(projectId)){
                return new Resp.Builder<String>().buildResult("请选择一个项目");
            }

            Result.verifyDoesExist(queryByTitle(projectId,view.getTitle(),view.getScope()),view.getTitle());
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
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> updateView(View view) {
        try {
            sysPermissionService.viewPermission(OneConstant.PERMISSION.EDIT,convertPermission(view.getScope()));
            view.verifyOneFilter();
            SysUser sysUser = jwtUserService.getUserLoginInfo().getSysUser();
            String projectId = sysUser.getUserUseOpenProject().getProjectId();
            if (StringUtils.isEmpty(projectId)){
                return new Resp.Builder<String>().buildResult("请选择一个项目");
            }
            //修改视图名称要进行验证
            if (view.getTitle() != null){
                Result.verifyDoesExist(queryByTitle(projectId,view.getTitle(),view.getScope()),view.getTitle());
            }

            view.setModifyUser(sysUser.getUserName());
            view.setModifyDate(new Date());
            return Result.updateResult(viewDao.update(view));
        }catch (BizException e){
            logger.error("class: ViewServiceImpl#updateView,error []" + e.getMessage());
            return new Resp.Builder<String>().buildResult(e.getCode(),e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> deleteView(String id) {
        try {
            View view = viewDao.queryById(id,jwtUserService.getMasterId());
            if(view == null){
                return Result.deleteResult(0);
            }
            sysPermissionService.viewPermission(OneConstant.PERMISSION.DELETE,convertPermission(view.getScope()));
            return Result.deleteResult(viewDao.deleteById(jwtUserService.getMasterId(),id));
        }catch (BizException e){
            logger.error("class: ViewServiceImpl#deleteView,error []" + e.getMessage());
            return new Resp.Builder<String>().buildResult(e.getCode(),e.getMessage());
        }
    }

    /**
     * 查询项目是否存在
     * @param title
     * @return
     */
    private Integer queryByTitle(String projectId, String title, String scope){
        if (StringUtils.isEmpty(title)){
            return null;
        }
        if(viewDao.queryTitleIsExist(jwtUserService.getMasterId(),title,projectId,scope) > 0){
            return 1;
        }
        return null;
    }

    /**
     * 根据用户选定的scope 转换成权限标识符
     * @return
     */
    private String convertPermission(String scope){
        return TwoConstant.convertPermission(scope);
    }
}
