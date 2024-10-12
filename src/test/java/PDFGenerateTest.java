import com.hu.oneclick.OneClickApplication;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.dto.SignOffDto;
import com.hu.oneclick.server.service.ProjectService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = OneClickApplication.class)
@RunWith(SpringRunner.class)
public class PDFGenerateTest {
    @Autowired
    ProjectService projectService;

    @Test
    public void testGeneratePDF() {
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
