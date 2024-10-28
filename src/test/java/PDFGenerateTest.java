import cn.hutool.core.bean.BeanUtil;
import com.hu.oneclick.OneClickApplication;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.dto.SignOffDto;
import com.hu.oneclick.model.param.SignOffParam;
import com.hu.oneclick.server.service.ProjectService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SpringBootTest(classes = OneClickApplication.class)
@RunWith(SpringRunner.class)
public class PDFGenerateTest {
    @Autowired
    ProjectService projectService;

    @Test
    public void testGeneratePDF() {
        SignOffParam signOffParam = new SignOffParam();
        signOffParam.setAutoGenerate(false);
        signOffParam.setEnv("开发");
        signOffParam.setTestCycle(List.of(new HashMap<>(){{
            put("testCycleId","1845418189232611330");
            put("testCycleTitle","克隆开发_Windows 11_3.0.0.0");
        }}));
        signOffParam.setProjectId("593988941040848898");
        signOffParam.setIssue("修改中,关闭");
        signOffParam.setFileUrl("C:\\Users\\ywp\\Desktop\\d2691895-c6e7-4661-9ea9-082c19f9121e.png");
        signOffParam.setVersion("3.0.0.0");

        SignOffDto signOffDto = new SignOffDto();
        signOffDto.setAuto("false");
        signOffDto.setEnv("开发");
        signOffDto.setTestCycle("curentReleaseVersion=1");
        signOffDto.setProjectId("593988941040848898");
        signOffDto.setIssue("修改中,关闭");
        signOffDto.setFileUrl("C:\\Users\\ywp\\Desktop\\d2691895-c6e7-4661-9ea9-082c19f9121e.png");
        signOffDto.setVersion("3.0.0.0");

        Resp<String> resp = projectService.generate(signOffDto);
        System.out.println(resp.getMsg());
    }
}
