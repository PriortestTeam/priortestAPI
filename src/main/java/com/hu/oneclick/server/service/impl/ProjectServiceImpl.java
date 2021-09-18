package com.hu.oneclick.server.service.impl;

import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.common.security.service.SysPermissionService;
import com.hu.oneclick.dao.ProjectDao;
import com.hu.oneclick.dao.ViewDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.base.Result;
import com.hu.oneclick.model.domain.Project;
import com.hu.oneclick.model.domain.SysUser;
import com.hu.oneclick.model.domain.UserUseOpenProject;
import com.hu.oneclick.model.domain.dto.ProjectDto;
import com.hu.oneclick.model.domain.dto.SignOffDto;
import com.hu.oneclick.server.service.ProjectService;
import com.hu.oneclick.server.service.QueryFilterService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFPicture;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author qingyang
 */
@Service
public class ProjectServiceImpl implements ProjectService {

    private final static Logger logger = LoggerFactory.getLogger(ProjectServiceImpl.class);

    private final SysPermissionService sysPermissionService;

    private final JwtUserServiceImpl jwtUserService;

    private final ProjectDao projectDao;

    private final QueryFilterService queryFilterService;

    public ProjectServiceImpl(SysPermissionService sysPermissionService, JwtUserServiceImpl jwtUserService, ProjectDao projectDao, RedissonClient redisClient, QueryFilterService queryFilterService, ViewDao viewDao) {
        this.sysPermissionService = sysPermissionService;
        this.jwtUserService = jwtUserService;
        this.projectDao = projectDao;
        this.queryFilterService = queryFilterService;
    }

    @Override
    public Resp<Project> queryById(String id) {
        return new Resp.Builder<Project>().setData(projectDao.queryById(id)).ok();
    }

    @Override
    public Resp<String> queryDoesExistByTitle(String title) {
        try {
            Result.verifyDoesExist(queryByTitle(title), title);
            return new Resp.Builder<String>().ok();
        } catch (BizException e) {
            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
        }
    }

    @Override
    public Resp<List<Project>> queryForProjects(ProjectDto project) {
        String masterId = jwtUserService.getMasterId();
        project.setUserId(masterId);

        project.setFilter(queryFilterService.mysqlFilterProcess(project.getViewTreeDto(), masterId));

        List<Project> projects = projectDao.queryAll(project);
        return new Resp.Builder<List<Project>>().setData(projects).total(projects).ok();
    }

    @Override
    public Resp<List<Project>> queryForProjects() {
        List<Project> projects = projectDao.queryAllProjects(jwtUserService.getMasterId());
        return new Resp.Builder<List<Project>>().setData(projects).totalSize(projects.size()).ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> addProject(Project project) {
        try {
            sysPermissionService.hasPermission(OneConstant.PERMISSION.PROJECT,
                    OneConstant.PERMISSION.ADD, project.getId());
            Result.verifyDoesExist(queryByTitle(project.getTitle()), project.getTitle());
            project.setUserId(jwtUserService.getMasterId());
            return Result.addResult(projectDao.insert(project));
        } catch (BizException e) {
            logger.error("class: ProjectServiceImpl#addProject,error []" + e.getMessage());
            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> updateProject(Project project) {
        try {
            sysPermissionService.hasPermission(OneConstant.PERMISSION.PROJECT,
                    OneConstant.PERMISSION.EDIT, project.getId());
            Result.verifyDoesExist(queryByTitle(project.getTitle()), project.getTitle());
            return Result.updateResult(projectDao.update(project));
        } catch (BizException e) {
            logger.error("class: ProjectServiceImpl#updateProject,error []" + e.getMessage());
            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> deleteProject(String projectId) {
        try {
            sysPermissionService.hasPermission(OneConstant.PERMISSION.PROJECT,
                    OneConstant.PERMISSION.DELETE, projectId);
            return Result.deleteResult(projectDao.deleteById(projectId));
        } catch (BizException e) {
            logger.error("class: ProjectServiceImpl#deleteProject,error []" + e.getMessage());
            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> checkProject(String projectId) {
        int flag = 0;
        try {
            SysUser sysUser = jwtUserService.getUserLoginInfo().getSysUser();

            Project project = projectDao.queryById(projectId);

            if (project != null) {
                UserUseOpenProject userUseOpenProject = new UserUseOpenProject();
                userUseOpenProject.setProjectId(projectId);
                userUseOpenProject.setUserId(sysUser.getId());
                userUseOpenProject.setTitle(project.getTitle());
                if (sysUser.getUserUseOpenProject() != null) {
                    projectDao.deleteUseOpenProject(sysUser.getUserUseOpenProject().getId());
                }
                if (projectDao.insertUseOpenProject(userUseOpenProject) > 0) {
                    sysUser.setUserUseOpenProject(userUseOpenProject);
                    jwtUserService.saveUserLoginInfo2(sysUser);
                    flag = 1;
                }
            }
            return Result.updateResult(flag);
        } catch (BizException e) {
            logger.error("class: ProjectServiceImpl#checkProject,error []" + e.getMessage());
            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> getCloseProject(String id, String closeDesc) {
        try {
            Project project = new Project();
            project.setUserId(jwtUserService.getMasterId());
            project.setId(id);
            project.setStatus(1);
            project.setCloseDate(new Date());
            project.setCloseDesc(closeDesc);
            return Result.updateResult(projectDao.update(project));
        } catch (BizException e) {
            logger.error("class: ProjectServiceImpl#getCloseProject,error []" + e.getMessage());
            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
        }
    }


    /**
     * 查询项目是否存在
     *
     * @param title
     * @return
     */
    private Integer queryByTitle(String title) {
        if (StringUtils.isEmpty(title)) {
            return null;
        }
        if (projectDao.queryByTitle(jwtUserService.getMasterId(), title) > 0) {
            return 1;
        }
        return null;
    }

    @Override
    public Resp<List<String>> getProject() {
        List<String> project = projectDao.getProject();
        return new Resp.Builder<List<String>>().setData(project).ok();
    }

    /**
     * 检测生成pdf表
     *
     * @param signOffDto
     * @return
     */
    @Override
    public Resp<String> generate(SignOffDto signOffDto) {
        if (StringUtils.isEmpty(signOffDto.getProjectId())) {
            return new Resp.Builder<String>().setData("请选择一个项目").fail();
        }

        String[] split = signOffDto.getFileUrl().split("。");
        creatExcel(split[0], split[1]);
        return null;
    }

    @Override
    public Resp<String> upload(MultipartFile file, HttpServletRequest req) {
        SimpleDateFormat sdf = new SimpleDateFormat("/yyyy/MM/dd/");
        String format = sdf.format(new Date());
        String realPath = req.getServletContext().getRealPath("/") + format;
        File folder = new File(realPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String oldName = file.getOriginalFilename();
        String imageName = UUID.randomUUID().toString();
        String newName = imageName + oldName.substring(oldName.lastIndexOf("."));
        String uri = folder.getPath() + File.separator + newName;
        try {
            FileUtils.copyInputStreamToFile(file.getInputStream(), new File(uri));
        } catch (IOException e) {
            e.printStackTrace();
            return new Resp.Builder<String>().setData(e.getMessage()).fail();
        }

        return new Resp.Builder<String>().setData(uri + "。" + imageName).ok();
    }

    private void creatExcel(String realPath, String imageName) {

        //创建Excel文件(Workbook)
        HSSFWorkbook workbook = new HSSFWorkbook();
        // 创建工作表(Sheet)
        HSSFSheet sheet = workbook.createSheet("Test");
        FileInputStream stream = null;
        byte[] bytes = null;
        try {
            stream = new FileInputStream(realPath);
            bytes = new byte[(int) stream.getChannel().size()];
            //读取图片到二进制数组
            stream.read(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int pictureIdx = workbook.addPicture(bytes, HSSFWorkbook.PICTURE_TYPE_JPEG);
        HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
        HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0, 0, 0, (short) 0, 0, (short) 5, 5);
        patriarch.createPicture(anchor, pictureIdx);
        //保存Excel文件
        try {
            FileOutputStream out = new FileOutputStream(realPath.substring(0, realPath.lastIndexOf(File.separatorChar) + 1) + imageName + ".xls");
            workbook.write(out);

            out.close();//关闭文件流
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
