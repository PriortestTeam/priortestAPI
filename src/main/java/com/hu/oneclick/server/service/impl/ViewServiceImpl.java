package com.hu.oneclick.server.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.constant.TwoConstant;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.common.security.service.SysPermissionService;
import com.hu.oneclick.dao.ViewDao;
import com.hu.oneclick.dao.ViewDownChildParamsDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.base.Result;
import com.hu.oneclick.model.domain.OneFilter;
import com.hu.oneclick.model.domain.SysUser;
import com.hu.oneclick.model.domain.View;
import com.hu.oneclick.model.domain.ViewDownChildParams;
import com.hu.oneclick.model.domain.dto.ViewScopeChildParams;
import com.hu.oneclick.model.domain.dto.ViewTreeDto;
import com.hu.oneclick.server.service.ViewService;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    private final ViewDownChildParamsDao viewDownChildParamsDao;

    private final RedissonClient redissonClient;

    public ViewServiceImpl(ViewDao v, JwtUserServiceImpl jwtUserService, SysPermissionService sysPermissionService, ViewDownChildParamsDao viewDownChildParamsDao, RedissonClient redissonClient) {
        this.viewDao = v;
        this.jwtUserService = jwtUserService;
        this.sysPermissionService = sysPermissionService;
        this.viewDownChildParamsDao = viewDownChildParamsDao;
        this.redissonClient = redissonClient;
    }

    @Override
    public Resp<View> queryById(String id) {
        View queryView = viewDao.queryById(id, jwtUserService.getMasterId());
        //防止mybatis 缓存数据变更
        View view = new View();
        BeanUtils.copyProperties(queryView,view);
        view.setOneFilters(TwoConstant.convertToList(view.getFilter(), OneFilter.class));
        view.setFilter("");
        view.setParentTitle(queryParentTitle(view.getParentId()));
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
            e.setParentTitle(queryParentTitle(e.getParentId()));
            e.setOneFilters(TwoConstant.convertToList(e.getFilter(), OneFilter.class));
            e.setFilter("");
        });
        return new Resp.Builder<List<View>>().setData(views).total(queryViews).ok();
    }

    /**
     * 查询父title
     * @param parentId
     * @return
     */
    private String queryParentTitle(String parentId){
        if (!"0".equals(parentId) && StringUtils.isNotEmpty(parentId)){
            return viewDao.queryTitleByParentId(parentId);
        }
        return null;
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
            String masterId = jwtUserService.getMasterId();
            if (StringUtils.isEmpty(projectId)){
                return new Resp.Builder<String>().buildResult("请选择一个项目");
            }
            //检查是否关联父级，如果关联父级，则查询父级 是否还可进行关联，最大级别3
            if (StringUtils.isNotEmpty(view.getParentId())){
                View parentView = viewDao.queryById(view.getParentId(), masterId);
                int level = parentView.getLevel() + 1;
                if (level > OneConstant.COMMON.VIEW_PARENT_CHILDREN_LEVEL){
                    return new Resp.Builder<String>().buildResult("您已超出最大层级，不可添加。");
                }
            }

            Result.verifyDoesExist(queryByTitle(projectId,view.getTitle(),view.getScope()),view.getTitle());
            view.setUserId(masterId);
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





    @Override
    public Resp<List<ViewScopeChildParams>> getViewScopeChildParams(String scope) {
        String key = OneConstant.REDIS_KEY_PREFIX.viewScopeDown + "-"+ scope;
        RBucket<String> bucket = redissonClient.getBucket(key);
        //缓存存在返回
        String s = bucket.get();
        if(!StringUtils.isEmpty(s)){
            List<ViewScopeChildParams> childParams = JSONArray.parseArray(s,ViewScopeChildParams.class);
            return new Resp.Builder<List<ViewScopeChildParams>>().setData(childParams).totalSize((long)childParams.size()).ok();
        }
        ViewDownChildParams viewDownChildParams = viewDownChildParamsDao.queryByScope(scope);
        if (viewDownChildParams == null){
            return new Resp.Builder<List<ViewScopeChildParams>>().buildResult("scope 无效");
        }
        String defaultValues = viewDownChildParams.getDefaultValues();
        List<ViewScopeChildParams> childParams = JSONArray.parseArray(defaultValues,ViewScopeChildParams.class);
        bucket.set(defaultValues);
        return new Resp.Builder<List<ViewScopeChildParams>>().setData(childParams).totalSize((long)childParams.size()).ok();
    }

    @Override
    public Resp<List<View>> queryViewParents(String scope, String viewTitle) {
        if (StringUtils.isEmpty(scope)){
           return new Resp.Builder<List<View>>().buildResult("scope" + SysConstantEnum.PARAM_EMPTY.getValue());
        }
        String masterId = jwtUserService.getMasterId();
        String projectId = jwtUserService.getUserLoginInfo().getSysUser().getUserUseOpenProject().getProjectId();
        List<View> result = viewDao.queryViewParents(masterId,scope,viewTitle,projectId);
        return new Resp.Builder<List<View>>().setData(result).total(result.size()).ok();
    }

    /**
     * 查询树结构view
     * @param scope
     * @return
     */
    @Override
    public Resp<List<ViewTreeDto>> queryViewTrees(String scope) {
        if (StringUtils.isEmpty(scope)){
            return new Resp.Builder<List<ViewTreeDto>>().buildResult("scope" + SysConstantEnum.PARAM_EMPTY.getValue());
        }
        String masterId = jwtUserService.getMasterId();
        String projectId = jwtUserService.getUserLoginInfo().getSysUser().getUserUseOpenProject().getProjectId();
        List<ViewTreeDto> treeAll = viewDao.queryViewByScopeAll(masterId,projectId,scope);
        //递归
        List<ViewTreeDto> result = viewTreeRecursion(treeAll);
        return new Resp.Builder<List<ViewTreeDto>>().setData(result).ok();
    }

    /**
     * queryViewTrees 递归
     */
    private List<ViewTreeDto> viewTreeRecursion(List<ViewTreeDto> treeAll){
        if(treeAll == null || treeAll.size() <= 0){
            return new ArrayList<>();
        }
        List<ViewTreeDto> result = new ArrayList<>();
        //循环找父级
        treeAll.forEach(e->{
            if (verifyParentId(e.getParentId())){
                e.setChildViews(childViewTreeRecursion(treeAll ,e.getId()));
                result.add(e);
            }
        });
        return result;
    }

    /**
     * 递归自己
     * @param treeAll,parentId
     * @return
     */
    private List<ViewTreeDto> childViewTreeRecursion(List<ViewTreeDto> treeAll,String id){
        List<ViewTreeDto> result = new ArrayList<>();
        treeAll.forEach(e->{
            //取反
            if (!verifyParentId(e.getParentId())
                    && e.getParentId().equals(id)){
                e.setChildViews(childViewTreeRecursion(treeAll,e.getId()));
                result.add(e);
            }
        });
        return result;
    }
    private boolean verifyParentId(String parentId){
        return StringUtils.isEmpty(parentId) || "0".equals(parentId);
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
