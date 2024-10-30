package com.hu.oneclick.server.service.impl;

import com.hu.oneclick.dao.ProjectDao;
import com.hu.oneclick.dao.TestCycleDao;
import com.hu.oneclick.model.entity.Project;
import com.hu.oneclick.model.param.SignOffParam;
import com.hu.oneclick.server.service.PdfGenerateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

@Service
public class PdfGenerateServiceImpl implements PdfGenerateService {
    @Value("${onclick.dirPath}")
    private String dirPath;
    @Autowired
    ProjectDao projectDao;
    @Autowired
    TestCycleDao testCycleDao;

    @Override
    public void generatePdf(SignOffParam signOffParam) {
        File dir = new File(dirPath);
        if(!dir.exists()){
            boolean b = dir.mkdirs();
            if (!b){
                throw new RuntimeException("无法创建目录");
            }
        }

        Project project = projectDao.queryById(signOffParam.getProjectId());

        Map<String,Object>cond = new HashMap<>();
        cond.put("project_id", signOffParam.getProjectId());
        cond.put("env", signOffParam.getEnv());
        cond.put("version",signOffParam.getVersion());
        cond.put("current_release",signOffParam.getCurrentRelease());

        List<Map<String, Object>> cycles = testCycleDao.queryTestCyclesWithCasesByConditions(cond);

        long caseCount = cycles.stream().filter(obj -> !Objects.isNull(obj.get("test_case_id"))).count();
        long execCount = cycles.stream().filter(obj -> !Objects.isNull(obj.get("execute_status"))).filter(obj->Integer.parseInt(obj.get("execute_status").toString())==1).count();
        long runCount = cycles.stream().filter(obj -> !Objects.isNull(obj.get("run_status"))).filter(obj->Integer.parseInt(obj.get("run_status").toString())==1).count();
        float execCentage = caseCount==0 ? 0 : (float) (execCount / caseCount);
        float passCentage = execCount==0 ? 0 : (float) (runCount / execCount);

        String[][] reportTable = new String[][]{
            {"项目",project.getTitle()},
            {"测试环境",signOffParam.getEnv()},
            {"测试版本",signOffParam.getVersion()},
            {"编译URL",""},
            {"在线报表",""},
            {"全部测试用例",String.valueOf(caseCount)},
            {"测试执行率",String.format("%.1f", (execCentage * 100)) + "%"},
            {"测试通过率",String.format("%.1f", (passCentage * 100)) + "%"},
        };
        System.out.println("reportTable" + Arrays.deepToString(reportTable));
    }
}
