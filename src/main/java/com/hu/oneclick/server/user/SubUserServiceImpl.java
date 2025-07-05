package com.hu.oneclick.server.user;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.constant.TwoConstant;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.dao.*;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.dto.SubUserDto;
import com.hu.oneclick.model.entity.*;
import com.hu.oneclick.server.service.MailService;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

    private final RoleFunctionDao roleFunctionDao;

    private final SysUserBusinessDao sysUserBusinessDao;

    private final SysRoleDao sysRoleDao;

    private final SysUserProjectDao sysUserProjectDao;

    @Value("${onclick.default.photo}")
    private String defaultPhoto;

    public SubUserServiceImpl(JwtUserServiceImpl jwtUserServiceImpl, SysUserDao sysUserDao, SubUserProjectDao subUserProjectDao, ProjectDao projectDao,
                              MailService mailService, RedissonClient redisClient, RoleFunctionDao roleFunctionDao, SysUserBusinessDao sysUserBusinessDao,
                              SysRoleDao sysRoleDao, SysUserProjectDao sysUserProjectDao) {
        this.jwtUserServiceImpl = jwtUserServiceImpl;
        this.sysUserDao = sysUserDao;
        this.subUserProjectDao = subUserProjectDao;
        this.projectDao = projectDao;
        this.mailService = mailService;
        this.redisClient = redisClient;
        this.roleFunctionDao = roleFunctionDao;
        this.sysUserBusinessDao = sysUserBusinessDao;
        this.sysRoleDao = sysRoleDao;
        this.sysUserProjectDao = sysUserProjectDao;
    }

    @Override
    public Resp<List<Map<String, Object>>> querySubUsers(int pageNum, int pageSize) {
        SysUser sysUser = jwtUserServiceImpl.getUserLoginInfo().getSysUser();
        Long roomId = sysUser.getRoomId();

        List<Map<String, Object>> sysUsers = sysUserDao.queryUsersByRoomId(new BigInteger(roomId.toString()), pageNum, pageSize);

        List<BigInteger> ids = sysUsers.stream().map(obj -> new BigInteger(obj.get("id").toString())).collect(Collectors.toList());
        List<Map<String, Object>> projects = sysUserProjectDao.queryProjectWithUsers(ids);

        List<SysRole> sysRoles = sysRoleDao.queryAll(null);

        for (Map<String, Object> map : sysUsers) {
            BigInteger userid = new BigInteger(map.get("id").toString());

            String roleName = sysRoles.stream().filter(obj -> obj.getId().equals(map.get("sysRoleId").toString()))
                .map(SysRole::getRoleName).findFirst().orElse(null);
            map.put("sysRoleName", roleName);

            Optional<Map<String, Object>> first = projects.stream().filter(m -> new BigInteger(m.get("userId").toString()).equals(userid)
                && Integer.parseInt(m.get("is_default").toString()) == 1).findFirst();
            map.put("openProjectByDefaultId", first.map(obj -> obj.get("projectId").toString()).orElse(null));
            map.put("openProjectByDefaultName", first.map(obj -> obj.get("title").toString()).orElse(null));

            List<Map<String, Object>> linkedProject = projects.stream().filter(m -> new BigInteger(m.get("userId").toString()).equals(userid))
                .map(m -> {
                    Map<String, Object> linkedMap = new HashMap<>();
                    linkedMap.put("projectId", m.get("projectId").toString());
                    linkedMap.put("title", m.get("title").toString());
                    return linkedMap;
                }).collect(Collectors.toList());

            map.put("projectIdStr", linkedProject);
        }

        return new Resp.Builder<List<Map<String, Object>>>().setData(sysUsers).total(sysUsers).ok();
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

            //验证用户是否存在
            verifySubEmailExists(oldEmail);

            sysUser.setEmail(oldEmail);
            //设置默认头像
            sysUser.setPhoto(defaultPhoto);
            sysUser.setSysRoleId(sysUser.getSysRoleId());
            sysUser.setManager(OneConstant.PLATEFORM_USER_TYPE.SUB_USER);
            sysUser.setRoomId(masterUser.getRoomId());

            if (sysUserDao.insert(sysUser) > 0) {
                String[] ids = sysUser.getProjectIdStr().split(",");
                SysUserProject sysUserProject;
                for (String id : ids) {
                    sysUserProject = new SysUserProject();
                    if (id.equals(sysUser.getOpenProjectByDefaultId())) {
                        sysUserProject.setIsDefault(1);
                    }
                    sysUserProject.setUserId(new BigInteger(sysUser.getId()));
                    sysUserProject.setProjectId(new BigInteger(id));
                    sysUserProjectDao.insert(sysUserProject);
                }

                String linkStr = RandomUtil.randomString(80);
                redisClient.getBucket(linkStr).set("true", 30, TimeUnit.MINUTES);

                mailService.sendSimpleMail(oldEmail, "OneClick激活账号", "http://43.139.159.146/#/activate?email=" + oldEmail +
                    "&params=" + linkStr);

                //2022/10/31 WangYiCheng 新增用户，根据角色，设置默认权限
                RoleFunction roleFunction = roleFunctionDao.queryByRoleId(sysUser.getSysRoleId());


                SysUserBusiness sysUserBusiness = new SysUserBusiness();
                sysUserBusiness.setType("RoleFunctions");
                sysUserBusiness.setValue(roleFunction.getCheckFunctionId());
                sysUserBusiness.setInvisible(roleFunction.getInvisibleFunctionId());
                sysUserBusiness.setDeleteFlag("0");
                sysUserBusiness.setUserId(Long.valueOf(sysUser.getId()));
                sysUserBusiness.setUserName(sysUser.getUserName());
                sysUserBusiness.setRoleId(Long.valueOf(sysUser.getSysRoleId()));

                SysRole sysRole = sysRoleDao.queryById(String.valueOf(sysUser.getSysRoleId()));
                sysUserBusiness.setRoleName(sysRole.getRoleName());

                String[] projectIds = sysUser.getProjectIdStr().split(",");

                for (String projectId : projectIds) {
                    Project project = projectDao.queryById(projectId);
                    sysUserBusiness.setProjectId(Long.valueOf(project.getId()));
                    sysUserBusiness.setProjectName(project.getTitle());
                    sysUserBusinessDao.insertSelective(sysUserBusiness);
                }


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

    @Override
    public Resp<String> updateSubUser(SubUserDto subUserDto) {
        SysUser sysUserBefore = sysUserDao.queryById(subUserDto.getId());

        // 设置用户
        SysUser sysUser = new SysUser();
        sysUser.setId(subUserDto.getId());
        sysUser.setUserName(subUserDto.getUserName());
        sysUser.setSysRoleId(subUserDto.getSysRoleId());
        sysUserDao.updateSubUser(sysUser);

        QueryWrapper<SysUserProject> query = Wrappers.query();
        query.eq("user_id", new BigInteger(subUserDto.getId()));
        List<SysUserProject> userProjects = sysUserProjectDao.selectList(query);

        String defaultProject = userProjects.stream().filter(obj -> obj.getIsDefault() == 1).map(obj -> obj.getProjectId().toString())
            .findFirst().orElse(null);

        List<String> projectIdsBefore = userProjects.stream().map(arg -> arg.getProjectId().toString()).collect(Collectors.toList());

        List<String> incomingIds = new ArrayList<>(List.of(subUserDto.getProjectIdStr().split(",")));

        List<String> deletedIds = new ArrayList<>(projectIdsBefore);
        deletedIds.removeAll(incomingIds);

        if (!deletedIds.isEmpty()) {
            QueryWrapper<SysUserProject> query2 = Wrappers.query();
            query2.eq("user_id", new BigInteger(subUserDto.getId()));
            query2.in("project_id", deletedIds.stream().map(BigInteger::new).collect(Collectors.toList()));
            sysUserProjectDao.delete(query2);
        }

        if (defaultProject == null || !defaultProject.equals(subUserDto.getOpenProjectByDefaultId())) {
            if (projectIdsBefore.contains(subUserDto.getOpenProjectByDefaultId())) {
                UpdateWrapper<SysUserProject> update = Wrappers.update();
                update.set("is_default", 1);
                update.eq("user_id", new BigInteger(subUserDto.getId()));
                update.eq("project_id", new BigInteger(subUserDto.getOpenProjectByDefaultId()));
                sysUserProjectDao.update(new SysUserProject(), update);
            } else {
                SysUserProject sysUserProject = new SysUserProject();
                sysUserProject.setUserId(new BigInteger(subUserDto.getId()));
                sysUserProject.setProjectId(new BigInteger(subUserDto.getOpenProjectByDefaultId()));
                sysUserProject.setIsDefault(1);
                sysUserProjectDao.insert(sysUserProject);
            }

            if (defaultProject != null && !deletedIds.contains(defaultProject)) {
                UpdateWrapper<SysUserProject> update = Wrappers.update();
                update.set("is_default", 0);
                update.eq("user_id", new BigInteger(subUserDto.getId()));
                update.eq("project_id", new BigInteger(defaultProject));
                sysUserProjectDao.update(new SysUserProject(), update);
            }
        }

        incomingIds.remove(subUserDto.getOpenProjectByDefaultId());
        if (!incomingIds.isEmpty()) {
            incomingIds.removeAll(projectIdsBefore);
            if (!incomingIds.isEmpty()) {
                SysUserProject sysUserProject;
                for (String projectId : incomingIds) {
                    sysUserProject = new SysUserProject();
                    sysUserProject.setUserId(new BigInteger(subUserDto.getId()));
                    sysUserProject.setProjectId(new BigInteger(projectId));
                    sysUserProjectDao.insert(sysUserProject);
                }
            }
        }

        //business相关
        //如果角色变了，则根据userId删除以前所有business数据，然后插入
        if (subUserDto.getSysRoleId() != sysUserBefore.getSysRoleId()) {
            sysUserBusinessDao.deleteByUserId(subUserDto.getId());

            RoleFunction roleFunction = roleFunctionDao.queryByRoleId(subUserDto.getSysRoleId());

            SysUserBusiness sysUserBusiness = new SysUserBusiness();
            sysUserBusiness.setType("RoleFunctions");
            sysUserBusiness.setValue(roleFunction.getCheckFunctionId());
            sysUserBusiness.setInvisible(roleFunction.getInvisibleFunctionId());
            sysUserBusiness.setDeleteFlag("0");
            sysUserBusiness.setUserId(Long.valueOf(subUserDto.getId()));
            sysUserBusiness.setUserName(subUserDto.getUserName());
            sysUserBusiness.setRoleId(Long.valueOf(subUserDto.getSysRoleId()));
            sysUserBusiness.setRoleName(subUserDto.getRoleName());

            String[] projectIds = subUserDto.getProjectIdStr().split(",");

            for (String projectId : projectIds) {
                Project project = projectDao.queryById(projectId);
                sysUserBusiness.setProjectId(Long.valueOf(project.getId()));
                sysUserBusiness.setProjectName(project.getTitle());
                sysUserBusinessDao.insertSelective(sysUserBusiness);
            }
        } else {
            List<String> addProjectIds = new ArrayList<>();

            List<String> projectIds = Arrays.asList(subUserDto.getProjectIdStr().split(","));
            for (int i = 0; i < projectIdsBefore.size(); i++) {
                //原来的不在现在的，则是要删除的
                if (!projectIds.contains(projectIdsBefore.get(i))) {
                    sysUserBusinessDao.deleteByUserIdAndProjectId(subUserDto.getId(), projectIdsBefore.get(i));
                }
            }

            for (int i = 0; i < projectIds.size(); i++) {
                //现在的不在原来的，则是要增加的
                if (!projectIdsBefore.contains(projectIds.get(i))) {
                    addProjectIds.add(projectIds.get(i));

                    RoleFunction roleFunction = roleFunctionDao.queryByRoleId(subUserDto.getSysRoleId());

                    SysUserBusiness sysUserBusiness = new SysUserBusiness();
                    sysUserBusiness.setType("RoleFunctions");
                    sysUserBusiness.setValue(roleFunction.getCheckFunctionId());
                    sysUserBusiness.setInvisible(roleFunction.getInvisibleFunctionId());
                    sysUserBusiness.setDeleteFlag("0");
                    sysUserBusiness.setUserId(Long.valueOf(subUserDto.getId()));
                    sysUserBusiness.setUserName(subUserDto.getUserName());
                    sysUserBusiness.setRoleId(Long.valueOf(subUserDto.getSysRoleId()));
                    sysUserBusiness.setRoleName(subUserDto.getRoleName());

                    Project project = projectDao.queryById(projectIds.get(i));
                    sysUserBusiness.setProjectId(Long.valueOf(project.getId()));
                    sysUserBusiness.setProjectName(project.getTitle());
                    sysUserBusinessDao.insertSelective(sysUserBusiness);


                }
            }
        }

        //如果角色没变，根据userId、查询以前所有business的projectIds
        //以前有现在也有，则不动。以前有，现在没有则删除。以前没有，现在有，则增加
        return new Resp.Builder<String>().setData(SysConstantEnum.UPDATE_SUCCESS.getValue()).ok();
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> updateSubUserPassword(SubUserDto sysUser) {
        String msg = "接口已删除";
        return new Resp.Builder<String>().buildResult("500", "接口已删除");
    }

    @Override
    public Resp<String> deleteSubUser(String id) {
        //删除用户
        if (sysUserDao.deleteSubUser(id) > 0) {
            // 删除关联的项目
//            subUserProjectDao.deleteByUserId(id);
            QueryWrapper<SysUserProject> query = Wrappers.query();
            query.eq("user_id", new BigInteger(id));
            sysUserProjectDao.delete(query);

//            projectDao.deleteOpenProjectByUserId(id);
            // 删除bussiness
            sysUserBusinessDao.deleteByUserId(id);
            return new Resp.Builder<String>().setData(SysConstantEnum.DELETE_SUCCESS.getValue()).ok();
        }
        return new Resp.Builder<String>().buildResult("500", "删除失败");
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

    @Override
    public Resp<List<Project>> getProjectByUserId() {
        SysUser sysUser = jwtUserServiceImpl.getUserLoginInfo().getSysUser();
        String userId = sysUser.getId();

        QueryWrapper<SysUserProject> query = Wrappers.query();
        query.eq("user_id", new BigInteger(userId));
        List<String> projectIdList = sysUserProjectDao.selectList(query).stream().map(obj -> obj.getProjectId().toString()).collect(Collectors.toList());

        List<Project> projectList = projectDao.queryAllByIds(projectIdList);

        return new Resp.Builder<List<Project>>().setData(projectList).total(projectList).ok();
    }
}
