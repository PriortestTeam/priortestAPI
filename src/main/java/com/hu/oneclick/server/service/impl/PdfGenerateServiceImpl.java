package com.hu.oneclick.server.service.impl;

import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.common.util.DateUtil;
import com.hu.oneclick.common.util.PDFTableUtil;
import com.hu.oneclick.common.util.SnowFlakeUtil;
import com.hu.oneclick.dao.ProjectDao;
import com.hu.oneclick.dao.ProjectSignOffDao;
import com.hu.oneclick.dao.TestCycleDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.dto.AuthLoginUser;
import com.hu.oneclick.model.entity.Project;
import com.hu.oneclick.model.entity.ProjectSignOff;
import com.hu.oneclick.model.param.SignOffParam;
import com.hu.oneclick.server.service.MailService;
import com.hu.oneclick.server.service.PdfGenerateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Service


public class PdfGenerateServiceImpl implements PdfGenerateService {
    private final static Logger logger = LoggerFactory.getLogger(ProjectServiceImpl.class);

    @Value("${onclick.dirPath}");
    private String dirPath;
    @Autowired
    ProjectDao projectDao;
    @Autowired
    TestCycleDao testCycleDao;

    private final ProjectSignOffDao projectSignOffDao;
    private final JwtUserServiceImpl jwtUserService;
    private final MailService mailService;

    public PdfGenerateServiceImpl(ProjectSignOffDao projectSignOffDao, JwtUserServiceImpl jwtUserService, ProjectServiceImpl projectService, MailService mailService) {
        this.projectSignOffDao = projectSignOffDao;
        this.jwtUserService = jwtUserService;
        this.mailService = mailService;
    }

    @Override
    public Object generatePdf(SignOffParam signOffParam) {
        File dir = new File(dirPath);
        if (!dir.exists() {
            if (!dir.mkdirs() {
                throw new RuntimeException("无法创建目录");
            }
        }

        Project project = projectDao.queryById(signOffParam.getProjectId();

        Map&lt;String, Object> cond = new HashMap&lt;>();

        if (signOffParam.getCurrentRelease() <= 0) {
            String ids = signOffParam.getTestCycle().stream().map(e -> e.get("testCycleId").collect(Collectors.joining(",");
            cond.put("ids", ids);
        } else {
            cond.put("project_id", signOffParam.getProjectId();
            cond.put("env", signOffParam.getEnv();
            cond.put("version", signOffParam.getVersion();
            cond.put("current_release", signOffParam.getCurrentRelease();
        }

        List&lt;Map&lt;String, Object>> cycles = testCycleDao.queryTestCyclesWithCasesByConditions(cond);
        if (cycles.isEmpty() {
//            return new Resp.Builder<String>().setData(SysConstantEnum.TEST_CASE_NOT_EXIST.getValue().fail();
            var resp = new Resp.Builder<String>().buildResult("404", SysConstantEnum.FAILED.getValue(), HttpStatus.NOT_FOUND.value();
            resp.setData(SysConstantEnum.TEST_CASE_NOT_EXIST.getValue();
            return resp;
        }

        int cycle_instance = cycles.stream().collect(Collectors.collectingAndThen(
            Collectors.toMap(k -> k.get("test_cycle_id"), v -> v.get("test_cycle_instance"), (oldValue, newValue) -> oldValue),
            map -> new ArrayList&lt;>(map.values()
        ).stream().mapToInt(v -> Integer.parseInt(v.toString().sum();

        var allures = cycles.stream().filter(v -> !v.get("allure_report_url").toString().isEmpty().map(v -> v.get("allure_report_url").toString()
            .collect(Collectors.toSet();
        String allures_str = String.join(",", allures);

        //总览
        String[][] reportTable = new String[][]{
            {"项目", project.getTitle()},
            {"测试环境", signOffParam.getEnv()},
            {"测试版本", signOffParam.getVersion()},
            {"编译URL", ""},
            {"在线报表", allures_str},
            {"全部测试用例", String.valueOf(cycle_instance)},
            {"测试执行率", ""},
            {"测试通过率", ""},
        };

        //功能测试报告
        String[][] functionalReportTable = new String[][]{
            {"测试用例", ""},
            {"没有执行", ""},
            {"成功", ""},
            {"失败", ""},
        };

        //性能测试报告
        String[][] performanceReportTable = new String[][]{
            {"测试用例", ""},
            {"没有执行", ""},
            {"成功", ""},
            {"失败", ""},
        };

        //测试覆盖报告
        int idx = 0;
        Map&lt;String, List&lt;Map&lt;String, Object>>> modules = cycles.stream().collect(Collectors.groupingBy(arg -> arg.get("test_case_module").toString();
        String[][] coverageReportTable = new String[modules.keySet().size()][];
        for (var m : modules.keySet() {
            coverageReportTable[idx] = new String[]{m, ""};
            idx++;
        }

        //运行案例的缺陷
        String[][] issueRepostTable = new String[][]{
            {"紧急", ""},
            {"重要", ""},
            {"一般", ""},
        };

        //全部缺陷
        String[][] existedIssueReportTable = new String[][]{
            {"紧急", ""},
            {"重要", ""},
            {"一般", ""},
        };

        //测试周期
        idx = 0;
        Set<Object> titles = cycles.stream().map(arg -> arg.get("test_cycle_title").collect(Collectors.toSet();
        String[][] testCycleReportTable = new String[titles.size()][];
        for (var item : titles) {
            testCycleReportTable[idx] = new String[]{item.toString(), ""};
            idx++;
        }

        //测试平台
        idx = 0;
        Map&lt;String, List&lt;Map&lt;String, Object>>> platforms = cycles.stream().collect(Collectors.groupingBy(arg -> arg.get("test_platform").toString();
        String[][] platformReportTable = new String[platforms.keySet().size()][];
        for (var item : platforms.keySet() {
            platformReportTable[idx] = new String[]{item, ""};
            idx++;
        }

        //签署
        String[][] signOffReportTable = new String[][]{
            {"签队团队", signOffParam.getFileUrl()},
            {"状态", "通过"},
            {"日期", DateUtil.format(new Date()},
            {"备注", ""},
        };

        var saveFile = DateUtil.format(new Date(), "yyyyMMddHHmmssSSS").concat("-").
            concat(project.getTitle().concat("_").concat(signOffParam.getEnv().concat("_").
            concat(signOffParam.getVersion().concat("_").concat("通过_SignOff.pdf");

        try {
            PDFTableUtil pdfTable = new PDFTableUtil(dirPath);
            pdfTable.showText(saveFile);
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

            pdfTable.save(saveFile);

            //发送邮件
            String desFilePathd = dirPath + "/" + saveFile;
            AuthLoginUser userLoginInfo = jwtUserService.getUserLoginInfo();
            String signOffId = String.valueOf(SnowFlakeUtil.getFlowIdInstance().nextId();
            String sendName = signOffId + project.getTitle() + signOffParam.getEnv() + signOffParam.getVersion() + ".pdf";

            //存储签收邮件
            ProjectSignOff projectSignOff = new ProjectSignOff();
            projectSignOff.setId(signOffId);
            projectSignOff.setProjectId(project.getId();
            projectSignOff.setUserId(userLoginInfo.getSysUser().getId();
            projectSignOff.setCreateTime(new Date();
            projectSignOff.setFilePath(desFilePathd);
            projectSignOff.setFileName(sendName);
            projectSignOff.setCreateUser(userLoginInfo.getSysUser().getId();
            projectSignOffDao.insert(projectSignOff);

            mailService.sendAttachmentsMail(userLoginInfo.getUsername(), "OneClick验收结果", "请查收验收结果", desFilePathd, sendName);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage();
            return new Resp.Builder<String>().setData(SysConstantEnum.SYS_ERROR.getValue().fail();
        }
        return new Resp.Builder<String>().ok();
    }
}
}
