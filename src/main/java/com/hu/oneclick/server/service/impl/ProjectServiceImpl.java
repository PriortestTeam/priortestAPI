package com.hu.oneclick.server.service.impl;

import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.common.security.service.SysPermissionService;
import com.hu.oneclick.common.util.PDFUtil;
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
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFPicture;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
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
    public Resp<String> generate(SignOffDto signOffDto, HttpServletRequest req) {
        if (StringUtils.isEmpty(signOffDto.getProjectId())) {
            return new Resp.Builder<String>().setData("请选择一个项目").fail();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("/yyyy/MM/dd/");
        String format = sdf.format(new Date());
        //文件路径
        String realPath = req.getServletContext().getRealPath("/") + format;
        File folder = new File(realPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
//        String[] split = signOffDto.getFileUrl().split("。");
//        creatSignExcel(split[0], split[1]);

        //创建Excel文件(Workbook)
        HSSFWorkbook workbook = new HSSFWorkbook();

        //边框
        HSSFCellStyle style = workbook.createCellStyle();
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);//下边框
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);//水平居中
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直居中
        //创建工作表(Sheet)
        HSSFSheet sheet = workbook.createSheet("Test");
        sheet.setDefaultColumnWidth(30);
        // 创建行,从0开始
        HSSFRow row = sheet.createRow(0);
        // 创建行的单元格,也是从0开始
        row.createCell(0).setCellValue("项目");
        // 项目
        row.createCell(1).setCellValue(this.queryById(signOffDto.getProjectId()).getData().getTitle());

        // 测试环境
        HSSFRow row1 = sheet.createRow(1);
        row1.createCell(0).setCellValue("测试环境");
        row1.createCell(1).setCellValue(signOffDto.getEnv());
        // 测试版本
        HSSFRow row2 = sheet.createRow(2);
        row2.createCell(0).setCellValue("测试版本");
        row2.createCell(1).setCellValue(signOffDto.getVersion());
        //编译URL
        HSSFRow row3 = sheet.createRow(3);
        row3.createCell(0).setCellValue("编译URL");
        row3.createCell(1).setCellValue(signOffDto.getVersion());
        //在线报表
        HSSFRow row4 = sheet.createRow(4);
        row4.createCell(0).setCellValue("在线报表");
        row4.createCell(1).setCellValue(signOffDto.getVersion());
        //全部测试用例
        HSSFRow row5 = sheet.createRow(5);
        row5.createCell(0).setCellValue("全部测试用例");
        row5.createCell(1).setCellValue(signOffDto.getVersion());
        //测试执行率
        HSSFRow row6 = sheet.createRow(6);
        row6.createCell(0).setCellValue("测试执行率");
        row6.createCell(1).setCellValue(signOffDto.getVersion());
        //测试通过率
        HSSFRow row7 = sheet.createRow(7);
        row7.createCell(0).setCellValue("测试通过率");
        row7.createCell(1).setCellValue(signOffDto.getVersion());

        //功能测试结果
        HSSFRow row8 = sheet.createRow(8);
        row8.createCell(0).setCellValue("功能测试结果");
        CellRangeAddress region = new CellRangeAddress(8, 8, 0, 1);
        sheet.addMergedRegion(region);

        HSSFRow row9 = sheet.createRow(9);
        row9.createCell(0).setCellValue("测试用例");
        row9.createCell(1).setCellValue(signOffDto.getVersion());

        HSSFRow row10 = sheet.createRow(10);
        row10.createCell(0).setCellValue("没有执行");
        row10.createCell(1).setCellValue(signOffDto.getVersion());

        HSSFRow row11 = sheet.createRow(11);
        row11.createCell(0).setCellValue("成功");
        row11.createCell(1).setCellValue(signOffDto.getVersion());

        HSSFRow row12 = sheet.createRow(12);
        row12.createCell(0).setCellValue("失败");
        row12.createCell(1).setCellValue(signOffDto.getVersion());

        HSSFRow row13 = sheet.createRow(13);
        row13.createCell(0).setCellValue("性能测试结果");
        CellRangeAddress region1 = new CellRangeAddress(13, 13, 0, 1);
        sheet.addMergedRegion(region1);

        HSSFRow row14 = sheet.createRow(14);
        row14.createCell(0).setCellValue("测试用例");
        row14.createCell(1).setCellValue(signOffDto.getVersion());

        HSSFRow row15 = sheet.createRow(15);
        row15.createCell(0).setCellValue("没有执行");
        row15.createCell(1).setCellValue(signOffDto.getVersion());

        HSSFRow row16 = sheet.createRow(16);
        row16.createCell(0).setCellValue("成功");
        row16.createCell(1).setCellValue(signOffDto.getVersion());

        HSSFRow row17 = sheet.createRow(17);
        row17.createCell(0).setCellValue("失败");
        row17.createCell(1).setCellValue(signOffDto.getVersion());

        HSSFRow row18 = sheet.createRow(18);
        row18.createCell(0).setCellValue("测试覆盖");
        CellRangeAddress region18 = new CellRangeAddress(18, 18, 0, 1);
        sheet.addMergedRegion(region18);

        HSSFRow row19 = sheet.createRow(19);
        row19.createCell(0).setCellValue("Feature1");
        row19.createCell(1).setCellValue(signOffDto.getVersion());

        HSSFRow row20 = sheet.createRow(20);
        row20.createCell(0).setCellValue("Feature2");
        row20.createCell(1).setCellValue(signOffDto.getVersion());

        HSSFRow row21 = sheet.createRow(21);
        row21.createCell(0).setCellValue("新缺陷");
        CellRangeAddress region21 = new CellRangeAddress(21, 21, 0, 1);
        sheet.addMergedRegion(region21);

        HSSFRow row22 = sheet.createRow(22);
        row22.createCell(0).setCellValue("紧急");
        row22.createCell(1).setCellValue(signOffDto.getVersion());

        HSSFRow row23 = sheet.createRow(23);
        row23.createCell(0).setCellValue("重要");
        row23.createCell(1).setCellValue(signOffDto.getVersion());

        HSSFRow row24 = sheet.createRow(24);
        row24.createCell(0).setCellValue("一般");
        row24.createCell(1).setCellValue(signOffDto.getVersion());

        HSSFRow row25 = sheet.createRow(25);
        row25.createCell(0).setCellValue("已知缺陷");
        CellRangeAddress region25 = new CellRangeAddress(25, 25, 0, 1);
        sheet.addMergedRegion(region25);

        HSSFRow row26 = sheet.createRow(26);
        row26.createCell(0).setCellValue("紧急");
        row26.createCell(1).setCellValue(signOffDto.getVersion());

        HSSFRow row27 = sheet.createRow(27);
        row27.createCell(0).setCellValue("重要");
        row27.createCell(1).setCellValue(signOffDto.getVersion());

        HSSFRow row28 = sheet.createRow(28);
        row28.createCell(0).setCellValue("一般");
        row28.createCell(1).setCellValue(signOffDto.getVersion());

        HSSFRow row29 = sheet.createRow(29);
        row29.createCell(0).setCellValue("测试周期列表");
        CellRangeAddress region29 = new CellRangeAddress(29, 29, 0, 1);
        sheet.addMergedRegion(region29);

        HSSFRow row30 = sheet.createRow(30);
        row30.createCell(0).setCellValue("测试周期 1");
        row30.createCell(1).setCellValue(signOffDto.getVersion());

        HSSFRow row31 = sheet.createRow(31);
        row31.createCell(0).setCellValue("测试周期 2");
        row31.createCell(1).setCellValue(signOffDto.getVersion());

        HSSFRow row32 = sheet.createRow(32);
        row32.createCell(0).setCellValue("测试平台/设备");
        CellRangeAddress region32 = new CellRangeAddress(32, 32, 0, 1);
        sheet.addMergedRegion(region32);

        HSSFRow row33 = sheet.createRow(33);
        row33.createCell(0).setCellValue("Win");
        row33.createCell(1).setCellValue(signOffDto.getVersion());

        HSSFRow row34 = sheet.createRow(34);
        row34.createCell(0).setCellValue("Linux");
        row34.createCell(1).setCellValue(signOffDto.getVersion());

        HSSFRow row35 = sheet.createRow(35);
        row35.createCell(0).setCellValue("Mac");
        row35.createCell(1).setCellValue(signOffDto.getVersion());

        HSSFRow row36 = sheet.createRow(36);
        row36.createCell(0).setCellValue("签发");
        CellRangeAddress region36 = new CellRangeAddress(36, 36, 0, 1);
        sheet.addMergedRegion(region36);

        HSSFRow row37 = sheet.createRow(37);
        row37.createCell(0).setCellValue("签队团队");
        row37.createCell(1).setCellValue(signOffDto.getVersion());

        HSSFRow row38 = sheet.createRow(38);
        row38.createCell(0).setCellValue("状态");
        row38.createCell(1).setCellValue(signOffDto.getVersion());

        HSSFRow row39 = sheet.createRow(39);
        row39.createCell(0).setCellValue("日期");
        row39.createCell(1).setCellValue(signOffDto.getVersion());

        HSSFRow row40 = sheet.createRow(40);
        row40.createCell(0).setCellValue("备注");
        row40.createCell(1).setCellValue(signOffDto.getVersion());


        for (int i = 0; i < 41; i++) {

            sheet.getRow(i).setHeightInPoints(20);
            if (i == 8 || i == 13 || i == 18 || i == 21 || i == 25 || i == 29 ||i == 32|| i == 36) {
                continue;
            }
            for (int j = 0; j < 2; j++) {

                sheet.getRow(i).getCell(j).setCellStyle(style);
            }
        }

        try {
            String uuid = UUID.randomUUID().toString();
            String sourceFilePath = realPath + uuid + ".xls";
            String desFilePathd = realPath + uuid + ".pdf";
            FileOutputStream out = new FileOutputStream(sourceFilePath);

            //保存Excel文件
            workbook.write(out);
            PDFUtil.excelTopdf(sourceFilePath, desFilePathd);
            out.close();//关闭文件流
        } catch (IOException e) {
            e.printStackTrace();
        }

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

    private void creatSignExcel(String realPath, String imageName) {

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
