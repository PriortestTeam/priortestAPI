package com.hu.oneclick.server.user;

import cn.hutool.core.util.RandomUtil;
import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.constant.RoleConstant;
import com.hu.oneclick.common.constant.TwoConstant;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.dao.ProjectDao;
import com.hu.oneclick.dao.SubUserProjectDao;
import com.hu.oneclick.dao.SysUserDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.base.Result;
import com.hu.oneclick.model.domain.Project;
import com.hu.oneclick.model.domain.SubUserProject;
import com.hu.oneclick.model.domain.SysUser;
import com.hu.oneclick.model.domain.UserUseOpenProject;
import com.hu.oneclick.model.domain.dto.SubUserDto;
import com.hu.oneclick.server.service.MailService;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author qingyang
 */
@Service
public class SubUserServiceImpl implements SubUserService {

    private final static Logger logger = LoggerFactory.getLogger(SubUserServiceImpl.class);

    private final JwtUserServiceImpl jwtUserServiceImpl;

    private final SysUserDao sysUserDao;

    private final SubUserProjectDao subUserProjectDao;

    private final ProjectDao projectDao;

    private final MailService mailService;

    private final RedissonClient redisClient;

    @Value("${onclick.default.photo}")
    private String defaultPhoto;

    public SubUserServiceImpl(JwtUserServiceImpl jwtUserServiceImpl, SysUserDao sysUserDao, SubUserProjectDao subUserProjectDao, ProjectDao projectDao, MailService mailService, RedissonClient redisClient) {
        this.jwtUserServiceImpl = jwtUserServiceImpl;
        this.sysUserDao = sysUserDao;
        this.subUserProjectDao = subUserProjectDao;
        this.projectDao = projectDao;
        this.mailService = mailService;
        this.redisClient = redisClient;
    }

    @Override
    public Resp<List<SubUserDto>> querySubUsers(SubUserDto dto) {
        /*sysUser.setParentId(Long.valueOf(jwtUserServiceImpl.getMasterId()));
        List<SubUserDto> sysUsers = sysUserDao.querySubUsers(sysUser);
        List<Project> projects = projectDao.queryAllProjects(jwtUserServiceImpl.getMasterId());
        if (projects != null && projects.size() > 0){
            sysUsers.forEach(e -> accept(e, projects));
        }
        return new Resp.Builder<List<SubUserDto>>().setData(sysUsers).total(sysUsers).ok();*/
        SysUser sysUser = jwtUserServiceImpl.getUserLoginInfo().getSysUser();
        List<SubUserDto> sysUsers = sysUserDao.querySubUsersByRoomId(sysUser.getRoomId());
        for (SubUserDto user : sysUsers) {
            String[] projectIds = user.getProjectIdStr().split(",");
            List<String> titles = subUserProjectDao.selectTitlesByUserId(sysUser.getId(),projectIds);
            String projectsSts = StringUtils.join(titles, ",");
            user.setProjectsSts(projectsSts);
        }

        return new Resp.Builder<List<SubUserDto>>().setData(sysUsers).total(sysUsers).ok();
    }

    private void accept(SubUserDto subUserDto, List<Project> projects) {
        //用户名裁剪
        subUserDto.setEmail(TwoConstant.subUserNameCrop(subUserDto.getEmail()));
        //整合关联的项目
        queryLikeProjectNames(subUserDto, projects);
    }

    /**
     * 整合关联的项目
     *
     * @param subUserDto
     */
    private void queryLikeProjectNames(SubUserDto subUserDto, List<Project> projects) {
        List<String> lists = new ArrayList<>(projects.size());
        String projectIdStr = subUserDto.getProjectIdStr();
        if (StringUtils.isEmpty(projectIdStr)) {
            return;
        } else if (subUserDto.getAll().equals(projectIdStr)) {
            projects.forEach(e -> lists.add(e.getTitle()));
        } else {
            //将查询条件转换成list
            projects.forEach(e -> {
                if (subUserDto.getProjectIdStr().contains(e.getId())) {
                    lists.add(e.getTitle());
                }
            });
        }
        //将项目名称列表转换成 字符串
        subUserDto.setProjectsSts(StringUtils.join(lists, "; "));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> createSubUser(SubUserDto sysUser) {
        try {
//            sysUser.verify();
            if (StringUtils.isEmpty(sysUser.getEmail())) {
                throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(), "邮箱" + SysConstantEnum.PARAM_EMPTY.getValue());
            } else if (StringUtils.isEmpty(sysUser.getUserName())) {
                throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(), "用户名" + SysConstantEnum.PARAM_EMPTY.getValue());
            }
            SysUser masterUser = jwtUserServiceImpl.getUserLoginInfo().getSysUser();

            //拼接成员用户邮箱
            String oldEmail = sysUser.getEmail();
            List<SysUser> sysUsers = sysUserDao.queryByLikeEmail(oldEmail);
            if (!sysUsers.isEmpty()) {
                throw new BizException(SysConstantEnum.DATE_EXIST.getCode(), "邮箱" + SysConstantEnum.DATE_EXIST.getValue());
            }
//            String subEmail = OneConstant.COMMON.SUB_USER_SEPARATOR + oldEmail;

            //验证用户是否存在
            verifySubEmailExists(oldEmail);

            sysUser.setEmail(oldEmail);
//            sysUser.setPassword(encodePassword(sysUser.getPassword()));
            //设置默认头像
            sysUser.setPhoto(defaultPhoto);
//            sysUser.setSysRoleId(RoleConstant.ADMIN_PLAT);
            sysUser.setSysRoleId(sysUser.getSysRoleId());
            sysUser.setManager(OneConstant.PLATEFORM_USER_TYPE.SUB_USER);
//            sysUser.setParentId(Long.valueOf(masterUser.getId()));
            sysUser.setRoomId(masterUser.getRoomId());

            //设置用户关联的项目
            SubUserProject subUserProject = new SubUserProject();
            subUserProject.setUserId(sysUser.getId());
            subUserProject.setProjectId(sysUser.getProjectIdStr());
            subUserProject.setOpenProjectByDefaultId(sysUser.getOpenProjectByDefaultId());


            // 设置用户下次登录默认打开的项目
            UserUseOpenProject userUseOpenProject = new UserUseOpenProject();
            userUseOpenProject.setProjectId(sysUser.getOpenProjectByDefaultId());
            userUseOpenProject.setUserId(sysUser.getId());
            projectDao.insertUseOpenProject(userUseOpenProject);

            if (sysUserDao.insert(sysUser) > 0
                    && subUserProjectDao.insert(subUserProject) > 0) {
                String linkStr = RandomUtil.randomString(80);
                redisClient.getBucket(linkStr).set("true", 30, TimeUnit.MINUTES);

                // TODO 测试时把地址改成本地了
//                mailService.sendSimpleMail(oldEmail, "OneClick激活账号", "http://124.71.142.223/#/activate?email=" + oldEmail +
//                        "&params=" + linkStr);
                mailService.sendSimpleMail(oldEmail, "OneClick激活账号", "http://127.0.0.1:9529/#/activate?email=" + oldEmail +
                        "&params=" + linkStr);
                return new Resp.Builder<String>().buildResult(SysConstantEnum.CREATE_SUB_USER_SUCCESS.getCode(),
                        SysConstantEnum.CREATE_SUB_USER_SUCCESS.getValue());
            }
            throw new BizException(SysConstantEnum.CREATE_SUB_USER_FAILED.getCode(),
                    SysConstantEnum.CREATE_SUB_USER_FAILED.getValue());
        } catch (BizException e) {
            logger.error("class: SubUserServiceImpl#createSubUser,error []" + e.getMessage());
            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
        }
    }

//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public Resp<String> updateSubUser(SubUserDto sysUser) {
        /*try {
            String masterId = jwtUserServiceImpl.getMasterId();
            sysUser.setParentId(Long.valueOf(masterId));

            //邮箱不为空代表需要修改
            if (sysUser.getEmail() != null){
                SysUser user = sysUserDao.queryById(masterId);
                if (user != null){
                    //验证用户是否存在
                    String email = OneConstant.COMMON.SUB_USER_SEPARATOR + sysUser.getEmail();
                    sysUser.setEmail(email);
                    verifySubEmailExists(email);
                }else {
                    throw new BizException(SysConstantEnum.MASTER_ACCOUNT_ERROR.getCode(),SysConstantEnum.MASTER_ACCOUNT_ERROR.getValue());
                }
            }

            Result.updateResult(sysUserDao.updateSubUser(sysUser));

            //设置用户关联的项目
            SubUserProject subUserProject = new SubUserProject();
            subUserProject.setUserId(sysUser.getId());
            subUserProject.setProjectId(sysUser.getProjectIdStr());

            if (StringUtils.isNotEmpty(subUserProject.getProjectId()) && StringUtils.isNotEmpty(subUserProject.getUserId())){
                Result.updateResult(subUserProjectDao.update(subUserProject));
            }
            return new Resp.Builder<String>().setData(SysConstantEnum.UPDATE_SUCCESS.getValue()).ok();
        }catch (BizException e){
            logger.error("class: SubUserServiceImpl#updateSubUser,error []" + e.getMessage());
            return new Resp.Builder<String>().buildResult(e.getCode(),e.getMessage());
        }*/
//        return  new Resp.Builder<String>().buildResult("500", "接口已删除");
//    }

    @Override
    public Resp<String> updateSubUser(SubUserDto subUserDto) {

        // 设置用户
        SysUser sysUser = new SysUser();
        sysUser.setId(subUserDto.getId());
        sysUser.setUserName(subUserDto.getUserName());
        sysUser.setSysRoleId(subUserDto.getSysRoleId());
        sysUserDao.updateSubUser(sysUser);

        //设置用户关联的项目
        SubUserProject subUserProject = new SubUserProject();
        subUserProject.setUserId(subUserDto.getId());
        subUserProject.setProjectId(subUserDto.getProjectIdStr());
        subUserProject.setOpenProjectByDefaultId(subUserDto.getOpenProjectByDefaultId());
        subUserProjectDao.update(subUserProject);

        // 设置用户下次登录默认打开的项目
        UserUseOpenProject userUseOpenProject = new UserUseOpenProject();
        userUseOpenProject.setProjectId(subUserDto.getOpenProjectByDefaultId());
        userUseOpenProject.setUserId(subUserDto.getId());
        projectDao.updateOpenProject(userUseOpenProject);

        return new Resp.Builder<String>().setData(SysConstantEnum.UPDATE_SUCCESS.getValue()).ok();
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> updateSubUserPassword(SubUserDto sysUser) {
        /*sysUser.verifyPassword();
        sysUser.setParentId(Long.valueOf(jwtUserServiceImpl.getMasterId()));
        sysUser.setPassword(encodePassword(sysUser.getPassword()));
        return Result.updateResult(sysUserDao.updateSubUserPassword(sysUser));*/
        String msg = "接口已删除";
        return new Resp.Builder<String>().buildResult("500", "接口已删除");
    }

//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public Resp<String> deleteSubUser(String id) {
//        //return Result.deleteResult(sysUserDao.deleteSubUser(id,jwtUserServiceImpl.getMasterId()));
//        return new Resp.Builder<String>().buildResult("500", "接口已删除");
//    }

    @Override
    public Resp<String> deleteSubUser(String id) {
        //删除用户
        if (sysUserDao.deleteSubUser(id) > 0) {
            // 删除关联的项目
            subUserProjectDao.deleteByUserId(id);
            projectDao.deleteOpenProjectByUserId(id);
            return new Resp.Builder<String>().setData(SysConstantEnum.DELETE_SUCCESS.getValue()).ok();
        }
        return  new Resp.Builder<String>().buildResult("500", "删除失败");
    }

    /**
     * 验证用户是否存在
     *
     * @param email
     */
    private void verifySubEmailExists(String email) {
        if (sysUserDao.queryByEmail(email) != null) {
            throw new BizException(SysConstantEnum.SUB_USERNAME_ERROR.getCode(), SysConstantEnum.SUB_USERNAME_ERROR.getValue());
        }
    }

    /**
     * 密码加密
     *
     * @param password
     * @return
     */
    private String encodePassword(String password) {
        return jwtUserServiceImpl.encryptPassword(password);
    }

    /**
     * 返回用户的项目列表
     *
     * @param userId
     * @Param: [userId]
     * @return: com.hu.oneclick.model.base.Resp<java.lang.String>
     * @Author: MaSiyi
     * @Date: 2022/2/8
     */
    @Override
    public Resp<SubUserProject> getSubUserProject(String userId) {
        SubUserProject subUserProject = subUserProjectDao.queryByUserId(userId);
        return new Resp.Builder<SubUserProject>().setData(subUserProject).ok();
    }
}
