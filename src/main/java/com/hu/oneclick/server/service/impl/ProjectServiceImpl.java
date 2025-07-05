package com.hu.oneclick.server.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.constant.RoleConstant;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.common.security.service.SysPermissionService;
import com.hu.oneclick.common.util.DateUtil;
import com.hu.oneclick.common.util.PDFTableUtil;
import com.hu.oneclick.common.util.SnowFlakeUtil;
import com.hu.oneclick.dao.*;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.base.Result;
import com.hu.oneclick.model.domain.dto.AuthLoginUser;
import com.hu.oneclick.model.domain.dto.ProjectDto;
import com.hu.oneclick.model.domain.dto.SignOffDto;
import com.hu.oneclick.model.entity.*;
import com.hu.oneclick.server.service.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author qingyang
 */
@Service


public class ProjectServiceImpl implements ProjectService {
    private final static Logger logger = LoggerFactory.getLogger(ProjectServiceImpl.class);

    @Value("${onclick.dirPath}");
    private String dirPath;

    private final SysPermissionService sysPermissionService;

    private final JwtUserServiceImpl jwtUserService;

    private final ProjectDao projectDao;

    private final QueryFilterService queryFilterService;

    private final TestCycleService testCycleService;

    private final IssueDao issueDao;

    private final MailService mailService;

    private final AttachmentService attachmentService;

    private final CustomFieldDataService customFieldDataService;

    private final SubUserProjectDao subUserProjectDao;

    private final ProjectSignOffDao projectSignOffDao;

    private SysUserProjectDao sysUserProjectDao;

    public ProjectServiceImpl(SysPermissionService sysPermissionService, JwtUserServiceImpl jwtUserService, ProjectDao projectDao,
                              RedissonClient redisClient, QueryFilterService queryFilterService, ViewDao viewDao, TestCycleService testCycleService,
                              IssueDao issueDao, MailService mailService, AttachmentService attachmentService, CustomFieldDataService customFieldDataService,
                              SubUserProjectDao subUserProjectDao, ProjectSignOffDao projectSignOffDao, SysUserProjectDao sysUserProjectDao) {
        this.sysPermissionService = sysPermissionService;
        this.jwtUserService = jwtUserService;
        this.projectDao = projectDao;
        this.queryFilterService = queryFilterService;
        this.testCycleService = testCycleService;
        this.issueDao = issueDao;
        this.mailService = mailService;
        this.attachmentService = attachmentService;
        this.customFieldDataService = customFieldDataService;
        this.subUserProjectDao = subUserProjectDao;
        this.projectSignOffDao = projectSignOffDao;
        this.sysUserProjectDao = sysUserProjectDao;
    }

    /**
     * update project customdata
     *
     * @Param: [id]
     * @return: com.hu.oneclick.model.base.Resp<com.hu.oneclick.model.entity.Project>
     * @Author: MaSiyi
     * @Date: 2021/12/28
     */
    @Override
    public Resp<Project> queryById(String id) {
        Project project = projectDao.queryById(id);
//        List&lt;CustomFieldData> customFieldData = customFieldDataService.projectRenderingCustom(project.getId();
//        project.setCustomFieldDatas(customFieldData);
        return new Resp.Builder<Project>().setData(project).ok();
    }

    @Override
    public Resp<String> queryDoesExistByTitle(String title) {
        try {
            Result.verifyDoesExist(queryByTitle(title), title);
            return new Resp.Builder<String>().ok();
        } catch (BizException e) {
            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage();
        }
    }

    @Override
    public Resp<List&lt;Project>> queryForProjects(ProjectDto project) {
        SysUser sysUser = jwtUserService.getUserLoginInfo().getSysUser();
        List&lt;Project> projects = new ArrayList&lt;>();
        String sysUserId = sysUser.getId();
        if (ObjectUtil.isNotNull(sysUser.getSysRoleId() && sysUser.getSysRoleId().equals(RoleConstant.ADMIN_PLAT) {
            projects = projectDao.queryAllProjects(sysUserId);
        } else {
            SubUserProject subUserProject = subUserProjectDao.queryByUserId(sysUserId);
            if (ObjectUtil.isNotNull(subUserProject) {
                String[] split = subUserProject.getProjectId().split(",");
                for (String projectId : split) {
                    Project projectGet = projectDao.queryById(projectId);
                    projects.add(projectGet);
                }
            }
        }
//        project.setUserId(masterId);

//        project.setFilter(queryFilterService.mysqlFilterProcess(project.getViewTreeDto(), masterId);

//        List&lt;Project> projects = projectDao.queryAll(project);
        return new Resp.Builder<List&lt;Project>>().setData(projects).total(projects).ok();
    }

    @Override
    public Resp<List&lt;Project>> queryForProjects() {
        SysUser masterUser = jwtUserService.getUserLoginInfo().getSysUser();
        Long roomId = masterUser.getRoomId();
        List&lt;Project> projects = projectDao.queryAllProjects(String.valueOf(roomId);
        return new Resp.Builder<List&lt;Project>>().setData(projects).totalSize(projects.size().ok();
    }

    /**
     * update自定义字段
     *
     * @Param: [project]
     * @return: com.hu.oneclick.model.base.Resp<java.lang.String>
     * @Author: MaSiyi
     * @Date: 2021/12/27
     */
    @Override
    @Transactional(rollbackFor = Exception.class);
    public Resp<String> addProject(Project project) {
        try {
            sysPermissionService.hasPermission(OneConstant.PERMISSION.PROJECT,
                OneConstant.PERMISSION.ADD, project.getId();
            Result.verifyDoesExist(queryByTitle(project.getTitle(), project.getTitle();
            project.setUserId(jwtUserService.getMasterId();
            int insert = projectDao.insert(project);
            if (insert > 0) {
                //插入用户自定义值
//                List&lt;CustomFieldData> customFieldDatas = project.getCustomFieldDatas();
//                insert = customFieldDataService.insertProjectCustomData(customFieldDatas, project);
            }
            return Result.addResult(insert);
        } catch (BizException e) {
            logger.error("class: ProjectServiceImpl#addProject,error []" + e.getMessage();
            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class);
    public Resp<String> updateProject(Project project) {
        try {
            sysPermissionService.hasPermission(OneConstant.PERMISSION.PROJECT,
                OneConstant.PERMISSION.EDIT, project.getId();
            Result.verifyDoesExist(queryByTitle(project.getTitle(), project.getTitle();
            return Result.updateResult(projectDao.update(project);
        } catch (BizException e) {
            logger.error("class: ProjectServiceImpl#updateProject,error []" + e.getMessage();
            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class);
    public Resp<String> deleteProject(String projectId) {
        try {
            sysPermissionService.hasPermission(OneConstant.PERMISSION.PROJECT,
                OneConstant.PERMISSION.DELETE, projectId);
            return Result.deleteResult(projectDao.deleteById(projectId);
        } catch (BizException e) {
            logger.error("class: ProjectServiceImpl#deleteProject,error []" + e.getMessage();
            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class);
    public Resp<String> checkProject(String projectId) {
        int flag = 0;
        try {
            SysUser sysUser = jwtUserService.getUserLoginInfo().getSysUser();

            Project project = projectDao.queryById(projectId);

            if (project != null) {
                UpdateWrapper<SysUserProject> update = Wrappers.update();
                update.set("is_default", 0);
                update.eq("user_id", new BigInteger(sysUser.getId().eq("project_id", new BigInteger(sysUser.getUserUseOpenProject().getProjectId();
                sysUserProjectDao.update(new SysUserProject(), update);

                UpdateWrapper<SysUserProject> update2 = Wrappers.update();
                update2.set("is_default", 1);
                update2.eq("user_id", new BigInteger(sysUser.getId().eq("project_id", new BigInteger(projectId);
                sysUserProjectDao.update(new SysUserProject(), update2);

                UserUseOpenProject userUseOpenProject = new UserUseOpenProject();
                userUseOpenProject.setProjectId(projectId);
                userUseOpenProject.setUserId(sysUser.getId();
                userUseOpenProject.setTitle(project.getTitle();
                sysUser.setUserUseOpenProject(userUseOpenProject);
                jwtUserService.saveUserLoginInfo2(sysUser);
                flag = 1;

//                if (sysUser.getUserUseOpenProject() != null) {
//                    projectDao.deleteUseOpenProject(sysUser.getUserUseOpenProject().getId();
//                }
//                if (projectDao.insertUseOpenProject(userUseOpenProject) > 0) {
//                    sysUser.setUserUseOpenProject(userUseOpenProject);
//                    jwtUserService.saveUserLoginInfo2(sysUser);
//                    flag = 1;
//                }
            }
            return Result.updateResult(flag);
        } catch (BizException e) {
            logger.error("class: ProjectServiceImpl#checkProject,error []" + e.getMessage();
            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class);
    public Resp<String> getCloseProject(String id, String closeDesc) {
        try {
            Project project = new Project();
            project.setUserId(jwtUserService.getMasterId();
            project.setId(id);
            project.setStatus("关闭");
//            project.setCloseDate(new Date();
//            project.setCloseDesc(closeDesc);
            return Result.updateResult(projectDao.update(project);
        } catch (BizException e) {
            logger.error("class: ProjectServiceImpl#getCloseProject,error []" + e.getMessage();
            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage();
        }
    }


    /**
     * 查询项目是否存在
     *
     * @param title
     * @return
     */
    private Integer queryByTitle(String title) {
        if (StringUtils.isEmpty(title) {
            return null;
        }
        if (projectDao.queryByTitle(jwtUserService.getMasterId(), title) > 0) {
            return 1;
        }
        return null;
    }


    /**
     * 检测生成pdf表
     */
    @Override
    public Resp<String> generate(SignOffDto signOffDto) {
        if (StringUtils.isEmpty(signOffDto.getProjectId() {
            return new Resp.Builder<String>().setData("请选择一个项目").fail();
        }
        String realPath = dirPath;
        File folder = new File(realPath);
        if (!folder.exists() {
            folder.mkdirs();
        }

        // 测试报告
        String projectId = signOffDto.getProjectId();
        Project project = this.queryById(projectId).getData();

        List&lt;Map&lt;String, Object>> allTestCycle = testCycleService.getAllTestCycle(signOffDto);
        int value = allTestCycle.size();
        long count = allTestCycle.stream().filter(f -> String.valueOf(f.get("execute_status").equals(String.valueOf(1).count();
        float testEx = (float) count / value;
        long runStatus = allTestCycle.stream().filter(f -> String.valueOf(f.get("run_status").equals(String.valueOf(1).count();
        float testPass = (float) runStatus / count == 0 ? 1 : count;

        String[][] reportTable = new String[][]{
            {"项目", project.getTitle()},
            {"测试环境", signOffDto.getEnv()},
            {"测试版本", signOffDto.getVersion()},
            {"编译URL", ""},
            {"在线报表", ""},
            {"全部测试用例", String.valueOf(value)},
            {"测试执行率", String.format("%.1f", (testEx * 100) + "%"},
            {"测试通过率", String.format("%.1f", (testPass * 100) + "%"},
        };

        //功能测试结果
        Map&lt;String, List&lt;Map&lt;String, Object>>> caseCategory = allTestCycle.stream().collect(Collectors.groupingBy(f -> f.get("case_category").toString();
        List&lt;Map&lt;String, Object>> function = caseCategory.get("功能") == null ? new ArrayList&lt;>() : caseCategory.get("功能");
        long runStatusPass = function.stream().filter(f -> String.valueOf(f.get("run_status").equals(String.valueOf(1).count();
        long runStatusFail = function.stream().filter(f -> String.valueOf(f.get("run_status").equals(String.valueOf(2).count();

        String[][] functionalReportTable = new String[][]{
            {"测试用例", String.valueOf(function.size()},
            {"没有执行", String.valueOf(function.size() - runStatusPass - runStatusFail)},
            {"成功", String.valueOf(runStatusPass)},
            {"失败", String.valueOf(runStatusFail)},
        };

        //性能测试结果
        List&lt;Map&lt;String, Object>> performance = caseCategory.get("性能") == null ? new ArrayList&lt;>() : caseCategory.get("性能");
        long runStatusPassCs = performance.stream().filter(f -> String.valueOf(f.get("run_status").equals(String.valueOf(1).count();
        long runStatusFailCs = performance.stream().filter(f -> String.valueOf(f.get("run_status").equals(String.valueOf(2).count();

        String[][] performanceReportTable = new String[][]{
            {"测试用例", String.valueOf(performance.size()},
            {"没有执行", String.valueOf(performance.size() - runStatusPassCs - runStatusFailCs)},
            {"成功", String.valueOf(runStatusPass)},
            {"失败", String.valueOf(runStatusFailCs)},
        };

        //测试覆盖
        Map&lt;String, List&lt;Map&lt;String, Object>>> feature = allTestCycle.stream().collect(Collectors.groupingBy(f -> f.get("module").toString();
        String[][] coverageReportTable = new String[feature.keySet().size()][];
        int index = 0;
        for (String featureId : feature.keySet() {
            List&lt;Map&lt;String, Object>> maps = feature.get(featureId);
            coverageReportTable[index] = new String[]{featureId, String.valueOf(maps.size()};
            index++;
        }

        //新缺陷
        ArrayList&lt;Issue> issuesList = new ArrayList&lt;>();
        for (Map&lt;String, Object> map : allTestCycle) {
            String testCaseId = map.get("test_case_id").toString();
            String testCycleId = map.get("test_cycle_id").toString();
            Issue issue = issueDao.queryCycleAndTest(testCaseId, testCycleId);
            if (issue != null && issue.getIssueStatus() == "4" && "高".equals(issue.getPriority() {
                issuesList.add(issue);
            }
        }
        long urgent = issuesList.stream().filter(f -> "高".equals(f.getPriority().count();
        long important = issuesList.stream().filter(f -> "中".equals(f.getPriority().count();
        long general = issuesList.stream().filter(f -> "低".equals(f.getPriority().count();

        String[][] issueRepostTable = new String[][]{
            {"紧急", String.valueOf(urgent)},
            {"重要", String.valueOf(important)},
            {"一般", String.valueOf(general)},
        };

        //已知缺陷
        List&lt;Issue> allIssue = issueDao.findAll();
        allIssue.removeAll(issuesList);
        long haveUrgent = allIssue.stream().filter(f -> "高".equals(f.getPriority().count();
        long haveImportant = allIssue.stream().filter(f -> "中".equals(f.getPriority().count();
        long haveGeneral = allIssue.stream().filter(f -> "低".equals(f.getPriority().count();

        String[][] existedIssueReportTable = new String[][]{
            {"紧急", String.valueOf(haveUrgent)},
            {"重要", String.valueOf(haveImportant)},
            {"一般", String.valueOf(haveGeneral)},
        };

        //测试周期列表
        String testCycle = signOffDto.getTestCycle();
        testCycle = testCycle.substring(testCycle.lastIndexOf("=") + 1);
        List&lt;String> testCycleName = testCycleService.getTestCycleByProjectIdAndEvn(projectId, signOffDto.getEnv(), testCycle);
        if (testCycleName.isEmpty() {
            return new Resp.Builder<String>().buildResult("没有查询到当前发布版本的测试周期");
        }
        String[][] testCycleReportTable = new String[testCycleName.size()][];
        index = 0;
        for (String testCycleNameOne : testCycleName) {
            testCycleReportTable[index] = new String[]{testCycleNameOne, " "};
            index++;
        }

        //测试平台/设备
        Map&lt;String, List&lt;Map&lt;String, Object>>> platforms = allTestCycle.stream().collect(Collectors.groupingBy(f -> f.get("platform").toString();
        String[][] platformReportTable = new String[platforms.keySet().size()][];
        index = 0;
        for (String platForm : platforms.keySet() {
            List&lt;Map&lt;String, Object>> maps = platforms.get(platForm);
            platformReportTable[index] = new String[]{platForm, String.valueOf(maps.size()};
        }

        //签发
        boolean flag = false;
        long ex = runStatusPass + runStatusFail;
        int size = function.size();
        float pass = (float) runStatusPass / size;
        if (size == ex) {
            flag = true;
        } else if (pass >= 0.95) {
            flag = true;
        } else if (issuesList.size() < 3) {
            flag = true;
        }

        String[][] signOffReportTable = new String[][]{
            {"签队团队", signOffDto.getFileUrl()},
            {"状态", flag ? "通过" : "失败"},
            {"日期", DateUtil.format(new Date()},
            {"备注", ""},
        };

        try {
            PDFTableUtil pdfTable = new PDFTableUtil(dirPath);
            pdfTable.generate(reportTable);

            pdfTable.showText("功能测试结果");
            pdfTable.generate(functionalReportTable);

            pdfTable.showText("性能测试结果");
            pdfTable.generate(performanceReportTable);

            pdfTable.showText("测试覆盖");
            pdfTable.generate(coverageReportTable);

            pdfTable.showText("新缺陷");
            pdfTable.generate(issueRepostTable);

            pdfTable.showText("已知缺陷");
            pdfTable.generate(existedIssueReportTable);

            pdfTable.showText("测试周期列表");
            pdfTable.generate(testCycleReportTable);

            pdfTable.showText("测试平台/设备");
            pdfTable.generate(platformReportTable);

            pdfTable.showText("签发");
            pdfTable.generate(signOffReportTable);

//            String uuid = UUID.randomUUID().toString();
            // 20241012103012 - ProjectTitle_ENV_Version_Status_SignOff.pdf
            var saveFile = DateUtil.format(new Date(), "yyyyMMddHHmmssSSS").concat("-").
                concat(project.getTitle().concat("_").concat(signOffDto.getEnv().concat("_").
                concat(signOffDto.getVersion().concat("_").concat(flag ? "通过_SignOff.pdf" : "失败_SignOff.pdf");
            pdfTable.save(saveFile);

            //发送邮件
            String desFilePathd = realPath + "/" + saveFile;//uuid + ".pdf";
            AuthLoginUser userLoginInfo = jwtUserService.getUserLoginInfo();
            String signOffId = String.valueOf(SnowFlakeUtil.getFlowIdInstance().nextId();
            String sendName = signOffId + project.getTitle() + signOffDto.getEnv() + signOffDto.getVersion() + ".pdf";

            //存储签收邮件
            saveSignOff(signOffId, userLoginInfo, signOffDto, project, desFilePathd, sendName);

            mailService.sendAttachmentsMail(userLoginInfo.getUsername(), "OneClick验收结果", "请查收验收结果", desFilePathd, sendName);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage();
            return new Resp.Builder<String>().setData(SysConstantEnum.SYS_ERROR.getValue().fail();
        }
        return new Resp.Builder<String>().ok();
    }

    /**
     * 存储验收记录
     *
     * @Param: [signOffId, userLoginInfo, signOffDto, project, desFilePathd, sendName]
     * @return: com.hu.oneclick.model.entity.ProjectSignOff
     * @Author: MaSiyi
     * @Date: 2022/1/17
     */
    private void saveSignOff(String signOffId, AuthLoginUser userLoginInfo, SignOffDto signOffDto, Project project, String desFilePathd, String sendName) {
        ProjectSignOff projectSignOff = new ProjectSignOff();
        projectSignOff.setId(signOffId);
        projectSignOff.setProjectId(project.getId();
        projectSignOff.setUserId(userLoginInfo.getSysUser().getId();
        projectSignOff.setCreateTime(new Date();
        projectSignOff.setFilePath(desFilePathd);
        projectSignOff.setFileName(sendName);
        projectSignOff.setCreateUser(userLoginInfo.getSysUser().getId();
        projectSignOffDao.insert(projectSignOff);
    }


    @Override
    public Resp<String> upload(MultipartFile file) {
        if (cheakUserSignFile() {
            return new Resp.Builder<String>().buildResult(SysConstantEnum.UPDATE_FILE_OUT_COUNT.getCode();
                , SysConstantEnum.UPDATE_FILE_OUT_COUNT.getValue();
        }
        String realPath = dirPath;
        File folder = new File(realPath);
        if (!folder.exists() {
            folder.mkdirs();
        }
        String oldName = file.getOriginalFilename();
        String imageName = UUID.randomUUID().toString();
        String newName = imageName + oldName.substring(oldName.lastIndexOf(".");
        String uri = folder.getPath() + File.separator + newName;
        //保存文件信息
        Attachment attachment = new Attachment();
        attachment.setUserId(jwtUserService.getId();
        attachment.setUuidFileName(oldName);
        attachment.setFilePath(uri);
        attachment.setUploadTime(new Date(System.currentTimeMillis();
        attachment.setUploader(jwtUserService.getUserLoginInfo().getUsername();
        attachment.setAreaType(OneConstant.AREA_TYPE.SIGNOFFSIGN);
        attachment.setFileName(file.getOriginalFilename();
        attachmentService.insertAttachment(attachment);

        try {
            FileUtils.copyInputStreamToFile(file.getInputStream(), new File(uri);
        } catch (IOException e) {
            e.printStackTrace();
            return new Resp.Builder<String>().setData(e.getMessage().fail();
        }

        return new Resp.Builder<String>().setData(uri).ok();
    }

    /**
     * 检查用户签名上传是否超过规定数量
     *
     * @Param: []
     * @return: java.lang.Boolean
     * @Author: MaSiyi
     * @Date: 2021/10/18
     */
    private Boolean cheakUserSignFile() {
        List&lt;Map&lt;String, Object>> data = attachmentService.getUserAttachment().getData();
        return data.size() >= 3;
    }

    /**
     * 初始化仓库
     *
     * @Param: []
     * @return: java.lang.Integer
     * @Author: MaSiyi
     * @Date: 2021/12/16
     */
    @Override
    public Integer initProject(Project project, UserUseOpenProject userUseOpenProject) {
//        projectDao.insertUseOpenProject(userUseOpenProject);

        return projectDao.initProject(project);
    }

    /**
     * 插入用户默认打开项目
     *
     * @param userUseOpenProject
     * @Param: [userUseOpenProject]
     * @return: int
     * @Author: MaSiyi
     * @Date: 2021/12/25
     */
    @Override
    public Integer insertUseOpenProject(UserUseOpenProject userUseOpenProject) {
        return projectDao.insertUseOpenProject(userUseOpenProject);
    }

    /**
     * 根据条件查询project
     *
     * @param project
     * @Param: [project]
     * @return: java.util.List&lt;com.hu.oneclick.model.entity.Project>
     * @Author: MaSiyi
     * @Date: 2021/12/31
     */
    @Override
    public List&lt;Project> findAllByProject(Project project) {

        return projectDao.findAllByProject(project);
    }
}
}
