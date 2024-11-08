import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.hu.oneclick.OneClickApplication;
import com.hu.oneclick.model.domain.dto.SignOffDto;
import com.hu.oneclick.model.param.SignOffParam;
import com.hu.oneclick.server.service.PdfGenerateService;
import com.hu.oneclick.server.service.ProjectService;
import com.hu.oneclick.server.service.TestCycleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest(classes = OneClickApplication.class)
@RunWith(SpringRunner.class)
public class PDFGenerateTest {
    @Autowired
    ProjectService projectService;
    @Autowired
    PdfGenerateService pdfGenerateService;
    @Autowired
    TestCycleService testCycleService;

    @Test
    public void testGeneratePDFFromJsonFile() {
        File file = new File("/Users/air/Desktop/test.json");
        JSON json = JSONUtil.readJSON(file, Charset.defaultCharset());
        SignOffParam signOffParam = JSONUtil.toBean((JSONObject) json, SignOffParam.class);
        signOffParam.setFileUrl("/Users/air/Desktop/signoff.png");
        pdfGenerateService.generatePdf(signOffParam);
    }

    @Test
    public void testGeneratePDF() {
        SignOffParam signOffParam = new SignOffParam();
        signOffParam.setAutoGenerate(false);
        signOffParam.setEnv("开发");
        signOffParam.setTestCycle(List.of(
            new HashMap<>() {{
                put("testCycleId", "1854844381899558914");
                put("testCycleTitle", "开发_Windows 11_4.0.0.0");
            }},
            new HashMap<>() {{
                put("testCycleId", "1854845247826202625");
                put("testCycleTitle", "开发周期验");
            }},
            new HashMap<>() {{
                put("testCycleId", "1854850132047073282");
                put("testCycleTitle", "克隆开发周期验");
            }}
        ));
        signOffParam.setProjectId("867268378685870080");
        signOffParam.setIssue("修改中,关闭");
//        signOffParam.setFileUrl("C:\\Users\\ywp\\Desktop\\d2691895-c6e7-4661-9ea9-082c19f9121e.png");
        signOffParam.setFileUrl("/Users/air/Desktop/signoff.png");
        signOffParam.setVersion("4.0.0.0");
        signOffParam.setCurrentRelease(-1);

        pdfGenerateService.generatePdf(signOffParam);
    }

    @Test
    public void testGeneratePDF2() {
        SignOffDto signOffDto = new SignOffDto();
        signOffDto.setAuto("false");
        signOffDto.setEnv("开发");
        signOffDto.setTestCycle("curentReleaseVersion=1");
        signOffDto.setProjectId("593988941040848898");
        signOffDto.setIssue("修改中,关闭");
        signOffDto.setFileUrl("C:\\Users\\ywp\\Desktop\\d2691895-c6e7-4661-9ea9-082c19f9121e.png");
        signOffDto.setVersion("3.0.0.0");
        List<Map<String, Object>> allTestCycle = testCycleService.getAllTestCycle(signOffDto);
        System.out.println(allTestCycle);
//        Resp<String> resp = projectService.generate(signOffDto);
//        System.out.println(resp.getMsg());
    }
}
