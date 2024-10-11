package com.hu.oneclick.server.service.impl;

import cn.hutool.core.util.ObjectUtil;
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
import com.hu.oneclick.model.domain.*;
import com.hu.oneclick.model.domain.dto.AuthLoginUser;
import com.hu.oneclick.model.domain.dto.ProjectDto;
import com.hu.oneclick.model.domain.dto.SignOffDto;
import com.hu.oneclick.server.service.*;
import com.spire.xls.FileFormat;
import com.spire.xls.Workbook;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author qingyang
 */
@Service
public class ProjectServiceImpl implements ProjectService {


    private final static Logger logger = LoggerFactory.getLogger(ProjectServiceImpl.class);

    @Value("${onclick.dirPath}")
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

    public ProjectServiceImpl(SysPermissionService sysPermissionService, JwtUserServiceImpl jwtUserService, ProjectDao projectDao, RedissonClient redisClient, QueryFilterService queryFilterService, ViewDao viewDao, TestCycleService testCycleService, IssueDao issueDao, MailService mailService, AttachmentService attachmentService, CustomFieldDataService customFieldDataService, SubUserProjectDao subUserProjectDao, ProjectSignOffDao projectSignOffDao) {
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
    }

    /**
     * update project customdata
     *
     * @Param: [id]
     * @return: com.hu.oneclick.model.base.Resp<com.hu.oneclick.model.domain.Project>
     * @Author: MaSiyi
     * @Date: 2021/12/28
     */
    @Override
    public Resp<Project> queryById(String id) {
        Project project = projectDao.queryById(id);
//        List<CustomFieldData> customFieldData = customFieldDataService.projectRenderingCustom(project.getId());
//        project.setCustomFieldDatas(customFieldData);
        return new Resp.Builder<Project>().setData(project).ok();
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
        SysUser sysUser = jwtUserService.getUserLoginInfo().getSysUser();
        List<Project> projects = new ArrayList<>();
        String sysUserId = sysUser.getId();
        if (ObjectUtil.isNotNull(sysUser.getSysRoleId()) && sysUser.getSysRoleId().equals(RoleConstant.ADMIN_PLAT)) {
            projects = projectDao.queryAllProjects(sysUserId);
        } else {
            SubUserProject subUserProject = subUserProjectDao.queryByUserId(sysUserId);
            if (ObjectUtil.isNotNull(subUserProject)) {
                String[] split = subUserProject.getProjectId().split(",");
                for (String projectId : split) {
                    Project projectGet = projectDao.queryById(projectId);
                    projects.add(projectGet);
                }
            }
        }
//        project.setUserId(masterId);
//
//        project.setFilter(queryFilterService.mysqlFilterProcess(project.getViewTreeDto(), masterId));

//        List<Project> projects = projectDao.queryAll(project);
        return new Resp.Builder<List<Project>>().setData(projects).total(projects).ok();
    }

    @Override
    public Resp<List<Project>> queryForProjects() {
        SysUser masterUser = jwtUserService.getUserLoginInfo().getSysUser();
        Long roomId = masterUser.getRoomId();
        List<Project> projects = projectDao.queryAllProjects(String.valueOf(roomId));
        return new Resp.Builder<List<Project>>().setData(projects).totalSize(projects.size()).ok();
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
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> addProject(Project project) {
        try {
            sysPermissionService.hasPermission(OneConstant.PERMISSION.PROJECT,
                    OneConstant.PERMISSION.ADD, project.getId());
            Result.verifyDoesExist(queryByTitle(project.getTitle()), project.getTitle());
            project.setUserId(jwtUserService.getMasterId());
            int insert = projectDao.insert(project);
            if (insert > 0) {
                //插入用户自定义值
//                List<CustomFieldData> customFieldDatas = project.getCustomFieldDatas();
//                insert = customFieldDataService.insertProjectCustomData(customFieldDatas, project);
            }
            return Result.addResult(insert);
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
            project.setStatus("关闭");
//            project.setCloseDate(new Date());
//            project.setCloseDesc(closeDesc);
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
        String realPath = dirPath;
        File folder = new File(realPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // 测试报告
        String projectId = signOffDto.getProjectId();
        Project project = this.queryById(projectId).getData();
        List<Map<String, Object>> allTestCycle = testCycleService.getAllTestCycle(signOffDto);
        int value = allTestCycle.size();
        long count = allTestCycle.stream().filter(f -> String.valueOf(f.get("execute_status")).equals(String.valueOf(1))).count();
        float testEx = (float) count / value;
        long runStatus = allTestCycle.stream().filter(f -> String.valueOf(f.get("run_status")).equals(String.valueOf(1))).count();
        float testPass = (float) runStatus / count == 0 ? 1 : count;

        String[][] reportTable = new String[][]{
                {"项目",project.getTitle()},
                {"测试环境",signOffDto.getEnv()},
                {"测试版本",signOffDto.getVersion()},
                {"编译URL",""},
                {"在线报表",""},
                {"全部测试用例",String.valueOf(value)},
                {"测试执行率",String.format("%.1f", (testEx * 100)) + "%"},
                {"测试通过率",String.format("%.1f", (testPass * 100)) + "%"},
        };

        //功能测试结果
        Map<String, List<Map<String, Object>>> caseCategory = allTestCycle.stream().collect(Collectors.groupingBy(f -> f.get("case_category").toString()));
        List<Map<String, Object>> function = caseCategory.get("功能") == null ? new ArrayList<>() : caseCategory.get("功能");
        long runStatusPass = function.stream().filter(f -> String.valueOf(f.get("run_status")).equals(String.valueOf(1))).count();
        long runStatusFail = function.stream().filter(f -> String.valueOf(f.get("run_status")).equals(String.valueOf(2))).count();

        String[][] functionalReportTable = new String[][]{
                {"测试用例",String.valueOf(function.size())},
                {"没有执行",String.valueOf(function.size() - runStatusPass - runStatusFail)},
                {"成功",String.valueOf(runStatusPass)},
                {"失败",String.valueOf(runStatusFail)},
        };

        //性能测试结果
        List<Map<String, Object>> performance = caseCategory.get("性能") == null ? new ArrayList<>() : caseCategory.get("性能");
        long runStatusPassCs = performance.stream().filter(f -> String.valueOf(f.get("run_status")).equals(String.valueOf(1))).count();
        long runStatusFailCs = performance.stream().filter(f -> String.valueOf(f.get("run_status")).equals(String.valueOf(2))).count();

        String[][] performanceReportTable = new String[][]{
                {"测试用例",String.valueOf(performance.size())},
                {"没有执行",String.valueOf(performance.size() - runStatusPassCs - runStatusFailCs)},
                {"成功",String.valueOf(runStatusPass)},
                {"失败",String.valueOf(runStatusFailCs)},
        };

        //测试覆盖
        Map<String, List<Map<String, Object>>> feature = allTestCycle.stream().collect(Collectors.groupingBy(f -> f.get("module").toString()));
        String[][] coverageReportTable = new String[feature.keySet().size()][];
        int index = 0;
        for (String featureId : feature.keySet()) {
            List<Map<String, Object>> maps = feature.get(featureId);
            coverageReportTable[index] = new String[]{featureId,String.valueOf(maps.size())};
            index++;
        }

        //新缺陷
        ArrayList<Issue> issuesList = new ArrayList<>();
        for (Map<String, Object> map : allTestCycle) {
            String testCaseId = map.get("test_case_id").toString();
            String testCycleId = map.get("test_cycle_id").toString();
            Issue issue = issueDao.queryCycleAndTest(testCaseId, testCycleId);
            if (issue != null && issue.getIssueStatus() == "4" && "高".equals(issue.getPriority())) {
                issuesList.add(issue);
            }
        }
        long urgent = issuesList.stream().filter(f -> "高".equals(f.getPriority())).count();
        long important = issuesList.stream().filter(f -> "中".equals(f.getPriority())).count();
        long general = issuesList.stream().filter(f -> "低".equals(f.getPriority())).count();

        String[][] issueRepostTable = new String[][]{
                {"紧急", String.valueOf(urgent)},
                {"重要", String.valueOf(important)},
                {"一般", String.valueOf(general)},
        };

        //已知缺陷
        List<Issue> allIssue = issueDao.findAll();
        allIssue.removeAll(issuesList);
        long haveUrgent = allIssue.stream().filter(f -> "高".equals(f.getPriority())).count();
        long haveImportant = allIssue.stream().filter(f -> "中".equals(f.getPriority())).count();
        long haveGeneral = allIssue.stream().filter(f -> "低".equals(f.getPriority())).count();

        String[][] existedIssueReportTable = new String[][]{
                {"紧急",String.valueOf(haveUrgent)},
                {"重要",String.valueOf(haveImportant)},
                {"一般",String.valueOf(haveGeneral)},
        };

        //测试周期列表
        String testCycle = signOffDto.getTestCycle();
        testCycle = testCycle.substring(testCycle.lastIndexOf("=") + 1);
        List<String> testCycleName = testCycleService.getTestCycleByProjectIdAndEvn(projectId, signOffDto.getEnv(), testCycle);
        if (testCycleName.isEmpty()) {
            return new Resp.Builder<String>().buildResult("没有查询到当前发布版本的测试周期");
        }
        String[][] testCycleReportTable = new String[testCycleName.size()][];
        index = 0;
        for (String testCycleNameOne : testCycleName) {
            testCycleReportTable[index] = new String[]{testCycleNameOne," "};
            index++;
        }

        //测试平台/设备
        Map<String, List<Map<String, Object>>> platforms = allTestCycle.stream().collect(Collectors.groupingBy(f -> f.get("platform").toString()));
        String[][] platformReportTable = new String[platforms.keySet().size()][];
        index = 0;
        for (String platForm : platforms.keySet()) {
            List<Map<String, Object>> maps = platforms.get(platForm);
            platformReportTable[index] = new String[]{platForm,String.valueOf(maps.size())};
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
                {"签队团队",signOffDto.getFileUrl()},
                {"状态",flag ? "通过" : "失败"},
                {"日期",DateUtil.format(new Date())},
                {"备注",""},
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

            pdfTable.save();
        } catch (IOException e) {
            return new Resp.Builder<String>().setData(SysConstantEnum.SYS_ERROR.getValue()).fail();
        }
        return new Resp.Builder<String>().ok();
    }

    /**
     * 检测生成pdf表
     *
     * @param signOffDto
     * @return
     */
    @Override
    public Resp<String> generate1(SignOffDto signOffDto) {
        try {
            if (StringUtils.isEmpty(signOffDto.getProjectId())) {
                return new Resp.Builder<String>().setData("请选择一个项目").fail();
            }
            String realPath = dirPath;
            File folder = new File(realPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            //创建Excel文件(Workbook)
            HSSFWorkbook workbook = new HSSFWorkbook();

            //判断是否是表头
            HashMap<Integer, Boolean> header = new HashMap<>();
            //边框
            HSSFCellStyle style = workbook.createCellStyle();
            style.setBorderTop(BorderStyle.THIN);//上边框
            style.setBorderBottom(BorderStyle.THIN);//下边框
            style.setBorderLeft(BorderStyle.THIN);//左边框
            style.setBorderRight(BorderStyle.THIN);//右边框
            style.setAlignment(HorizontalAlignment.CENTER);//水平居中
            style.setVerticalAlignment(VerticalAlignment.CENTER);//垂直居中
            //创建工作表(Sheet)
            HSSFSheet sheet = workbook.createSheet("SignOff");
            sheet.setDefaultColumnWidth(30);
            // 创建行,从0开始
            HSSFRow row = sheet.createRow(0);
            // 创建行的单元格,也是从0开始
            row.createCell(0).setCellValue("项目");
            // 项目
            String projectId = signOffDto.getProjectId();
            Project project = this.queryById(projectId).getData();
            row.createCell(1).setCellValue(project.getTitle());

            // 测试环境
            HSSFRow row1 = sheet.createRow(1);
            row1.createCell(0).setCellValue("测试环境");
            String env = signOffDto.getEnv();
            row1.createCell(1).setCellValue(env);
            // 测试版本
            HSSFRow row2 = sheet.createRow(2);
            row2.createCell(0).setCellValue("测试版本");
            row2.createCell(1).setCellValue(signOffDto.getVersion());
            //编译URL
            HSSFRow row3 = sheet.createRow(3);
            row3.createCell(0).setCellValue("编译URL");
            row3.createCell(1).setCellValue("");
            //在线报表
            HSSFRow row4 = sheet.createRow(4);
            row4.createCell(0).setCellValue("在线报表");
            row4.createCell(1).setCellValue("");

            List<Map<String, Object>> allTestCycle = testCycleService.getAllTestCycle(signOffDto);
            //全部测试用例
            HSSFRow row5 = sheet.createRow(5);
            row5.createCell(0).setCellValue("全部测试用例");
            int value = allTestCycle.size();
            row5.createCell(1).setCellValue(value);
            //测试执行率
            HSSFRow row6 = sheet.createRow(6);
            row6.createCell(0).setCellValue("测试执行率");
            long count = allTestCycle.stream().filter(f -> String.valueOf(f.get("execute_status")).equals(String.valueOf(1))).count();
            float testEx = (float) count / value;
            row6.createCell(1).setCellValue(String.format("%.1f", (testEx * 100)) + "%");
            //测试通过率
            HSSFRow row7 = sheet.createRow(7);
            row7.createCell(0).setCellValue("测试通过率");
            long runStatus = allTestCycle.stream().filter(f -> String.valueOf(f.get("run_status")).equals(String.valueOf(1))).count();
            float testPass = (float) runStatus / count == 0 ? 1 : count;
            row7.createCell(1).setCellValue(String.format("%.1f", (testPass * 100)) + "%");

            //功能测试结果
            HSSFRow row8 = sheet.createRow(8);
            row8.createCell(0).setCellValue("功能测试结果");
            CellRangeAddress region = new CellRangeAddress(8, 8, 0, 1);
            sheet.addMergedRegion(region);
            header.put(row8.getRowNum(), true);

            Map<String, List<Map<String, Object>>> caseCategory = allTestCycle.stream().collect(Collectors.groupingBy(f -> f.get("case_category").toString()));
            List<Map<String, Object>> function = caseCategory.get("功能") == null ? new ArrayList<>() : caseCategory.get("功能");

            HSSFRow row9 = sheet.createRow(9);
            row9.createCell(0).setCellValue("测试用例");
            row9.createCell(1).setCellValue(function.size());

            long runStatusPass = function.stream().filter(f -> String.valueOf(f.get("run_status")).equals(String.valueOf(1))).count();
            long runStatusFail = function.stream().filter(f -> String.valueOf(f.get("run_status")).equals(String.valueOf(2))).count();

            HSSFRow row10 = sheet.createRow(10);
            row10.createCell(0).setCellValue("没有执行");
            row10.createCell(1).setCellValue(function.size() - runStatusPass - runStatusFail);

            HSSFRow row11 = sheet.createRow(11);
            row11.createCell(0).setCellValue("成功");
            row11.createCell(1).setCellValue(runStatusPass);

            HSSFRow row12 = sheet.createRow(12);
            row12.createCell(0).setCellValue("失败");
            row12.createCell(1).setCellValue(runStatusFail);

            HSSFRow row13 = sheet.createRow(13);
            row13.createCell(0).setCellValue("性能测试结果");
            CellRangeAddress region1 = new CellRangeAddress(13, 13, 0, 1);
            sheet.addMergedRegion(region1);
            header.put(row13.getRowNum(), true);

            List<Map<String, Object>> performance = caseCategory.get("性能") == null ? new ArrayList<>() : caseCategory.get("性能");
            ;
            HSSFRow row14 = sheet.createRow(14);
            row14.createCell(0).setCellValue("测试用例");
            row14.createCell(1).setCellValue(performance.size());

            long runStatusPassCs = performance.stream().filter(f -> String.valueOf(f.get("run_status")).equals(String.valueOf(1))).count();
            long runStatusFailCs = performance.stream().filter(f -> String.valueOf(f.get("run_status")).equals(String.valueOf(2))).count();


            HSSFRow row15 = sheet.createRow(15);
            row15.createCell(0).setCellValue("没有执行");
            row15.createCell(1).setCellValue(performance.size() - runStatusPassCs - runStatusFailCs);

            HSSFRow row16 = sheet.createRow(16);
            row16.createCell(0).setCellValue("成功");
            row16.createCell(1).setCellValue(runStatusPassCs);

            HSSFRow row17 = sheet.createRow(17);
            row17.createCell(0).setCellValue("失败");
            row17.createCell(1).setCellValue(runStatusFailCs);

            HSSFRow row18 = sheet.createRow(18);
            row18.createCell(0).setCellValue("测试覆盖");
            CellRangeAddress region18 = new CellRangeAddress(18, 18, 0, 1);
            sheet.addMergedRegion(region18);
            header.put(row18.getRowNum(), true);

            int rowId = row18.getRowNum() + 1;
            Map<String, List<Map<String, Object>>> feature = allTestCycle.stream().collect(Collectors.groupingBy(f -> f.get("module").toString()));
            for (String featureId : feature.keySet()) {
                List<Map<String, Object>> maps = feature.get(featureId);

                HSSFRow row19 = sheet.createRow(rowId++);

                row19.createCell(0).setCellValue(featureId);
                row19.createCell(1).setCellValue(maps.size());
            }

            HSSFRow row21 = sheet.createRow(rowId++);
            row21.createCell(0).setCellValue("新缺陷");
            CellRangeAddress region21 = new CellRangeAddress(rowId - 1, rowId - 1, 0, 1);
            sheet.addMergedRegion(region21);
            header.put(row21.getRowNum(), true);

            ArrayList<Issue> issuesList = new ArrayList<>();
            for (Map<String, Object> map : allTestCycle) {
                String testCaseId = map.get("test_case_id").toString();
                String testCycleId = map.get("test_cycle_id").toString();
                Issue issue = issueDao.queryCycleAndTest(testCaseId, testCycleId);
                if (issue != null && issue.getIssueStatus() == "4" && "高".equals(issue.getPriority())) {
                    issuesList.add(issue);
                }
            }


            HSSFRow row22 = sheet.createRow(rowId++);
            row22.createCell(0).setCellValue("紧急");
            long urgent = issuesList.stream().filter(f -> "高".equals(f.getPriority())).count();
            row22.createCell(1).setCellValue(urgent);

            HSSFRow row23 = sheet.createRow(rowId++);
            row23.createCell(0).setCellValue("重要");
            long important = issuesList.stream().filter(f -> "中".equals(f.getPriority())).count();
            row23.createCell(1).setCellValue(important);

            HSSFRow row24 = sheet.createRow(rowId++);
            row24.createCell(0).setCellValue("一般");
            long general = issuesList.stream().filter(f -> "低".equals(f.getPriority())).count();
            row24.createCell(1).setCellValue(general);

            HSSFRow row25 = sheet.createRow(rowId++);
            row25.createCell(0).setCellValue("已知缺陷");
            CellRangeAddress region25 = new CellRangeAddress(rowId - 1, rowId - 1, 0, 1);
            sheet.addMergedRegion(region25);
            header.put(row25.getRowNum(), true);


            List<Issue> allIssue = issueDao.findAll();
            allIssue.removeAll(issuesList);

            HSSFRow row26 = sheet.createRow(rowId++);
            row26.createCell(0).setCellValue("紧急");
            long haveUrgent = allIssue.stream().filter(f -> "高".equals(f.getPriority())).count();
            row26.createCell(1).setCellValue(haveUrgent);

            HSSFRow row27 = sheet.createRow(rowId++);
            row27.createCell(0).setCellValue("重要");
            long haveImportant = allIssue.stream().filter(f -> "中".equals(f.getPriority())).count();
            row27.createCell(1).setCellValue(haveImportant);

            HSSFRow row28 = sheet.createRow(rowId++);
            row28.createCell(0).setCellValue("一般");
            long haveGeneral = allIssue.stream().filter(f -> "低".equals(f.getPriority())).count();
            row28.createCell(1).setCellValue(haveGeneral);

            HSSFRow row29 = sheet.createRow(rowId++);
            row29.createCell(0).setCellValue("测试周期列表");
            CellRangeAddress region29 = new CellRangeAddress(rowId - 1, rowId - 1, 0, 1);
            sheet.addMergedRegion(region29);
            header.put(row29.getRowNum(), true);

//            Map<String, List<Map<String, String>>> testCycleIds = allTestCycle.stream().collect(Collectors.groupingBy(f -> f.get("test_cycle_id")));
//            for (String testCycleId : testCycleIds.keySet()) {
//                Resp<TestCycle> testCycleResp = testCycleService.queryById(testCycleId);
//                String title = testCycleResp.getData().getTitle();
//                List<Map<String, String>> maps = testCycleIds.get(testCycleId);
//                HSSFRow row30 = sheet.createRow(rowId++);
//                row30.createCell(0).setCellValue(title);
//                row30.createCell(1).setCellValue(maps.size());
//            }
            String testCycle = signOffDto.getTestCycle();
            testCycle = testCycle.substring(testCycle.lastIndexOf("=") + 1);
            List<String> testCycleName = testCycleService.getTestCycleByProjectIdAndEvn(projectId, env, testCycle);
            if (testCycleName.isEmpty()) {
                return new Resp.Builder<String>().buildResult("没有查询到当前发布版本的测试周期");
            }
            for (String testCycleNameOne : testCycleName) {
                HSSFRow row30 = sheet.createRow(rowId++);
                row30.createCell(0).setCellValue(testCycleNameOne);
                row30.createCell(1).setCellValue("");

            }


            HSSFRow row32 = sheet.createRow(rowId++);
            row32.createCell(0).setCellValue("测试平台/设备");
            CellRangeAddress region32 = new CellRangeAddress(rowId - 1, rowId - 1, 0, 1);
            sheet.addMergedRegion(region32);
            header.put(row32.getRowNum(), true);

            Map<String, List<Map<String, Object>>> platforms = allTestCycle.stream().collect(Collectors.groupingBy(f -> f.get("platform").toString()));
            for (String platForm : platforms.keySet()) {
                List<Map<String, Object>> maps = platforms.get(platForm);
                HSSFRow row33 = sheet.createRow(rowId++);
                row33.createCell(0).setCellValue(platForm);
                row33.createCell(1).setCellValue(maps.size());

            }

            HSSFRow row36 = sheet.createRow(rowId++);
            row36.createCell(0).setCellValue("签发");
            CellRangeAddress region36 = new CellRangeAddress(rowId - 1, rowId - 1, 0, 1);
            sheet.addMergedRegion(region36);
            header.put(row36.getRowNum(), true);

            String url = signOffDto.getFileUrl();

            FileInputStream stream;
            byte[] bytes = null;
            try {
                stream = new FileInputStream(url);
                bytes = new byte[(int) stream.getChannel().size()];
                //读取图片到二进制数组
                stream.read(bytes);
            } catch (Exception e) {
                e.printStackTrace();
            }
            int pictureIdx = workbook.addPicture(bytes, HSSFWorkbook.PICTURE_TYPE_JPEG);
            HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
            HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0, 0, 0, (short) 1, rowId, (short) 2, (rowId + 1));
            patriarch.createPicture(anchor, pictureIdx);


            HSSFRow row37 = sheet.createRow(rowId++);
            row37.createCell(0).setCellValue("签队团队");
            row37.createCell(1).setCellValue("");

            HSSFRow row38 = sheet.createRow(rowId++);
            row38.createCell(0).setCellValue("状态");
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

            row38.createCell(1).setCellValue(flag ? "通过" : "失败");

            HSSFRow row39 = sheet.createRow(rowId++);
            row39.createCell(0).setCellValue("日期");
            row39.createCell(1).setCellValue(DateUtil.format(new Date()));

            HSSFRow row40 = sheet.createRow(rowId++);
            row40.createCell(0).setCellValue("备注");
            row40.createCell(1).setCellValue("");


            for (int i = 0; i < rowId; i++) {

                sheet.getRow(i).setHeightInPoints(20);

                if (header.containsKey(i)) {
                    continue;
                }
                for (int j = 0; j < 2; j++) {

                    try {
                        sheet.getRow(i).getCell(j).setCellStyle(style);
                    } catch (Exception e) {
                        System.out.println(i);
                        e.printStackTrace();
                    }
                }
            }

            try {
                String uuid = UUID.randomUUID().toString();
                String sourceFilePath = realPath + uuid + ".xls";
                String desFilePathd = realPath + uuid + ".pdf";
                FileOutputStream out = new FileOutputStream(sourceFilePath);

                //保存Excel文件
                workbook.write(out);
                // 加载Excel文档.
                Workbook wb = new Workbook();
                wb.loadFromFile(sourceFilePath);
                wb.createFont(new Font("宋体", 10, 20));
                // 调用方法保存为PDF格式.
                wb.saveToFile(desFilePathd, FileFormat.PDF);
                //发送邮件
                AuthLoginUser userLoginInfo = jwtUserService.getUserLoginInfo();
                String signOffId = String.valueOf(SnowFlakeUtil.getFlowIdInstance().nextId());
                String sendName = signOffId + project.getTitle() + signOffDto.getEnv() + signOffDto.getVersion() + ".pdf";

                //存储签收邮件
                saveSignOff(signOffId, userLoginInfo, signOffDto, project, desFilePathd, sendName);

                mailService.sendAttachmentsMail(userLoginInfo.getUsername(), "OneClick验收结果", "请查收验收结果", desFilePathd, sendName);
                out.close();//关闭文件流
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new Resp.Builder<String>().setData(SysConstantEnum.SYS_ERROR.getValue()).fail();
        }

        return new Resp.Builder<String>().ok();
    }

    /**
     * 存储验收记录
     *
     * @Param: [signOffId, userLoginInfo, signOffDto, project, desFilePathd, sendName]
     * @return: com.hu.oneclick.model.domain.ProjectSignOff
     * @Author: MaSiyi
     * @Date: 2022/1/17
     */
    private void saveSignOff(String signOffId, AuthLoginUser userLoginInfo, SignOffDto signOffDto, Project project, String desFilePathd, String sendName) {
        ProjectSignOff projectSignOff = new ProjectSignOff();
        projectSignOff.setId(signOffId);
        projectSignOff.setProjectId(project.getId());
        projectSignOff.setUserId(userLoginInfo.getSysUser().getId());
        projectSignOff.setCreateTime(new Date());
        projectSignOff.setFilePath(desFilePathd);
        projectSignOff.setFileName(sendName);
        projectSignOff.setCreateUser(userLoginInfo.getSysUser().getId());
        projectSignOffDao.insert(projectSignOff);
    }


    @Override
    public Resp<String> upload(MultipartFile file) {
        if (cheakUserSignFile()) {
            return new Resp.Builder<String>().buildResult(SysConstantEnum.UPDATE_FILE_OUT_COUNT.getCode()
                    , SysConstantEnum.UPDATE_FILE_OUT_COUNT.getValue());
        }
        String realPath = dirPath;
        File folder = new File(realPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String oldName = file.getOriginalFilename();
        String imageName = UUID.randomUUID().toString();
        String newName = imageName + oldName.substring(oldName.lastIndexOf("."));
        String uri = folder.getPath() + File.separator + newName;
        //保存文件信息
        Attachment attachment = new Attachment();
        attachment.setUserId(jwtUserService.getId());
        attachment.setUuidFileName(oldName);
        attachment.setFilePath(uri);
        attachment.setUploadTime(new Date(System.currentTimeMillis()));
        attachment.setUploader(jwtUserService.getUserLoginInfo().getUsername());
        attachment.setAreaType(OneConstant.AREA_TYPE.SIGNOFFSIGN);
        attachment.setFileName(file.getOriginalFilename());
        attachmentService.insertAttachment(attachment);

        try {
            FileUtils.copyInputStreamToFile(file.getInputStream(), new File(uri));
        } catch (IOException e) {
            e.printStackTrace();
            return new Resp.Builder<String>().setData(e.getMessage()).fail();
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
        List<Map<String, Object>> data = attachmentService.getUserAttachment().getData();
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
        projectDao.insertUseOpenProject(userUseOpenProject);

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
     * @return: java.util.List<com.hu.oneclick.model.domain.Project>
     * @Author: MaSiyi
     * @Date: 2021/12/31
     */
    @Override
    public List<Project> findAllByProject(Project project) {

        return projectDao.findAllByProject(project);
    }
}
