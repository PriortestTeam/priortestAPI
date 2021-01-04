package com.hu.oneclick.server.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.dao.ProjectDao;
import com.hu.oneclick.dao.SysProjectPermissionDao;
import com.hu.oneclick.dao.SysUserDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.Project;
import com.hu.oneclick.model.domain.SysUser;
import com.hu.oneclick.model.domain.dto.AuthLoginUser;
import com.hu.oneclick.model.domain.dto.SubUserDto;
import com.hu.oneclick.model.domain.dto.SubUserPermissionDto;
import com.hu.oneclick.server.service.SettingPermissionService;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * @author qingyang
 */
@Service
public class SettingPermissionServiceImpl implements SettingPermissionService {

    private final static Logger logger = LoggerFactory.getLogger(SettingPermissionServiceImpl.class);

    private final JwtUserServiceImpl jwtUserServiceImpl;

    private final SysUserDao sysUserDao;

    private final ProjectDao projectDao;

    private final SysProjectPermissionDao sysProjectPermissionDao;

    private final RedissonClient redisClient;

    public SettingPermissionServiceImpl(JwtUserServiceImpl jwtUserServiceImpl, SysUserDao sysUserDao, ProjectDao projectDao, SysProjectPermissionDao sysProjectPermissionDao, RedissonClient redisClient) {
        this.jwtUserServiceImpl = jwtUserServiceImpl;
        this.sysUserDao = sysUserDao;
        this.projectDao = projectDao;
        this.sysProjectPermissionDao = sysProjectPermissionDao;
        this.redisClient = redisClient;
    }


    @Override
    public Resp<SubUserPermissionDto> getPermissions(String subUserId) {
        SysUser masterUser = jwtUserServiceImpl.getUserLoginInfo().getSysUser();
        SubUserDto subUserDto = sysUserDao.querySubUserInfo(subUserId,masterUser.getId());
        if (subUserDto == null){
            return new Resp.Builder<SubUserPermissionDto>().fail();
        }
        String projectIdStr = subUserDto.getProjectIdStr();
        List<Project> projects = null;
        if (subUserDto.getALL().equals(projectIdStr)){
            //获取全部的项目
            projects = projectDao.queryAllProjectsAndPermission(masterUser.getId());
        }else if(StringUtils.isNotEmpty(projectIdStr)){
            List<String> ids = Arrays.asList(projectIdStr.split(subUserDto.getDELIMITER()));
            projects = projectDao.queryInProjectIdsAndPermission(ids,subUserDto.getId(), masterUser.getId());
        }
        SubUserPermissionDto subUserPermissionDto = new SubUserPermissionDto();
        subUserPermissionDto.setSubUserDto(subUserDto);
        subUserPermissionDto.setProjects(projects);
        return new Resp.Builder<SubUserPermissionDto>().setData(subUserPermissionDto).ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> updatePermissions(SubUserPermissionDto entity) {
        entity.verify();
        try {
            SysUser masterUser = jwtUserServiceImpl.getUserLoginInfo().getSysUser();
            SubUserDto subUserDto = sysUserDao.querySubUserInfo(entity.getSubUserDto().getId(),masterUser.getId());
            if (subUserDto == null){
                return new Resp.Builder<String>().fail();
            }
            //删除子用户关联项目权限表并重新添加
            if (sysProjectPermissionDao.deleteBySubUserId(subUserDto.getId()) > 0
                    && sysProjectPermissionDao.batchInsert(entity.getProjectPermissions()) > 0){
                //删除用户，用户必须重新登录
                deleteSubUserLoginStatus(subUserDto.getEmail());
                return new Resp.Builder<String>().setData(SysConstantEnum.UPDATE_SUCCESS.getValue()).ok();
            }
            throw new BizException(SysConstantEnum.UPDATE_SUCCESS.getCode(),SysConstantEnum.UPDATE_SUCCESS.getValue());
        }catch (BizException e){
            logger.error("class: SettingPermissionServiceImpl#updatePermissions,error []" + e.getMessage());
            return new Resp.Builder<String>().buildResult(e.getCode(),e.getMessage());
        }
    }

    /**
     * 更新用户的缓存（权限列表）信息
     */
    @Transactional(rollbackFor = Exception.class)
    private void deleteSubUserLoginStatus(String username){
        RBucket<String> bucket = redisClient.getBucket(OneConstant.REDIS_KEY_PREFIX.LOGIN  + username);
        AuthLoginUser authLoginUser = JSONObject.parseObject(bucket.get(), AuthLoginUser.class);
        if (authLoginUser != null) {
            bucket.delete();
        }
    }
}
