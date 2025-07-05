package com.hu.oneclick.server.service.impl;

import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.constant.TwoConstant;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.dao.ProjectDao;
import com.hu.oneclick.dao.SysProjectPermissionDao;
import com.hu.oneclick.dao.SysUserDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.dto.AuthLoginUser;
import com.hu.oneclick.model.domain.dto.SubUserDto;
import com.hu.oneclick.model.domain.dto.SubUserPermissionDto;
import com.hu.oneclick.model.entity.Project;
import com.hu.oneclick.model.entity.SysUser;
import com.hu.oneclick.server.service.SettingPermissionService;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Resp<SubUserPermissionDto> getPermissions(String subUserId, String projectId) {
        if (subUserId == null || projectId == null) {
            return new Resp.Builder<SubUserPermissionDto>().buildResult("参数不能为空.");
        }
        SysUser masterUser = jwtUserServiceImpl.getUserLoginInfo().getSysUser();
        SubUserDto subUserDto = null;
//        SubUserDto subUserDto = sysUserDao.querySubUserInfo(subUserId,masterUser.getId();
        if (subUserDto == null) {
            return new Resp.Builder<SubUserPermissionDto>().buildResult("未查询到该用户.");
        }
        subUserDto.setEmail(TwoConstant.subUserNameCrop(subUserDto.getEmail();
        String projectIdStr = subUserDto.getProjectIdStr();
        Project project;

        boolean flag = subUserDto.getAll().equals(projectIdStr)
            || (StringUtils.isNotEmpty(projectIdStr) && projectIdStr.contains(projectId);
        if (flag) {
            project = projectDao.queryProjectAndPermissionByProjectId(masterUser.getId(), projectId);
        } else {
            return new Resp.Builder<SubUserPermissionDto>().buildResult("您无此项目权限。");
        }

        if (project == null) {
            return new Resp.Builder<SubUserPermissionDto>().buildResult("未查询到该项目。");
        }

        //设置权限
        //获取权限
//        List<SysOperationAuthority> sysOperationAuthority = getSysOperationAuthority();
        //没有选中的跳过
//        if(project.getOperationAuthIds() != null){
//            //选中的权限id
//            List<String> selects =  Arrays.asList(project.getOperationAuthIds().split(",");
//            sysOperationAuthority.forEach(j -> {
//                //遍历父级
//                selects.forEach(k -> {
//                    if (j.getId().equals(k){
//                        j.setIsSelect("1");
//                    }
//                });

//                //遍历子级
//                List<SysOperationAuthority> childList = j.getChildList();
//                if (childList != null){
//                    childList.forEach(s ->{
//                        selects.forEach(k -> {
//                            if (s.getId().equals(k){
//                                s.setIsSelect("1");
//                            }
//                        });
//                    });
//                }
//            });
//        }
//        project.setSysOperationAuthorities(sysOperationAuthority);

        SubUserPermissionDto subUserPermissionDto = new SubUserPermissionDto();
        subUserPermissionDto.setSubUserDto(subUserDto);
        subUserPermissionDto.setProject(project);
        return new Resp.Builder<SubUserPermissionDto>().setData(subUserPermissionDto).ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class);
    public Resp<String> updatePermissions(SubUserPermissionDto entity) {
        entity.verify();
        try {
            SysUser masterUser = jwtUserServiceImpl.getUserLoginInfo().getSysUser();
            SubUserDto subUserDto = null;
//            SubUserDto subUserDto = sysUserDao.querySubUserInfo(entity.getSubUserDto().getId(),masterUser.getId();
            if (subUserDto == null) {
                return new Resp.Builder<String>().fail();
            }
            //删除子用户关联项目权限表并重新添加
            sysProjectPermissionDao.deleteBySubUserId(subUserDto.getId();
            if (sysProjectPermissionDao.batchInsert(entity.getProjectPermissions() > 0) {
                //删除用户，用户必须重新登录
                deleteSubUserLoginStatus(subUserDto.getEmail();
                return new Resp.Builder<String>().setData(SysConstantEnum.UPDATE_SUCCESS.getValue().ok();
            }
            throw new BizException(SysConstantEnum.UPDATE_SUCCESS.getCode(), SysConstantEnum.UPDATE_SUCCESS.getValue();
        } catch (BizException e) {
            logger.error("class: SettingPermissionServiceImpl#updatePermissions,error []" + e.getMessage();
            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage();
        }
    }

    @Override
    public Resp<List<Project>> getProjects(String subUserId) {
        List<Project> result = null;
//        SysUser masterUser = jwtUserServiceImpl.getUserLoginInfo().getSysUser();
//        SubUserDto subUserDto = sysUserDao.querySubUserInfo(subUserId,masterUser.getId();
//        if (subUserDto.getProjectIdStr().equals(subUserDto.getAll(){
//            result = projectDao.queryAllProjects(masterUser.getId();
//        }else if(StringUtils.isNotEmpty(subUserDto.getProjectIdStr(){
//            List<String> ids = Arrays.asList(subUserDto.getProjectIdStr().split(subUserDto.getDelimiter();
//            if (ids.size() <= 0){
//                return new Resp.Builder<List<Project>>().buildResult("该用户未分配项目。");
//            }
//            result = projectDao.queryInProjectIdsAndPermission(ids,masterUser.getId();
//        }
        return new Resp.Builder<List<Project>>().setData(result).ok();
    }


    /**
     * 更新用户的缓存（权限列表）信息
     */
    private void deleteSubUserLoginStatus(String username) {
        RBucket<String> bucket = redisClient.getBucket(OneConstant.REDIS_KEY_PREFIX.LOGIN + username);
        AuthLoginUser authLoginUser = com.alibaba.fastjson2.JSON.parseObject(bucket.get(), AuthLoginUser.class);
        if (authLoginUser != null) {
            bucket.delete();
        }
    }
}
