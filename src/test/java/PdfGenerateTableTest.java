import cn.hutool.Hutool;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.hu.oneclick.common.util.PDFTableUtil;
import com.hu.oneclick.model.param.SignOffParam;
import org.junit.Test;

import java.io.File;
import java.nio.charset.Charset;

public class PdfGenerateTableTest {
    @Test
    public void testFromFileJson() {
        File file = new File("/Users/air/Desktop/test.json");
        JSON json = JSONUtil.readJSON(file, Charset.defaultCharset());
        System.out.println(json.toString());
        SignOffParam signOffParam = JSONUtil.toBean((JSONObject) json, SignOffParam.class);
        System.out.println(signOffParam);
        System.out.println();
    }

    @Test
    public void testGenerateTable() {
        String[][] reportTable1 = new String[][]{
            {"项目", "project.getTitle()"},
            {"测试环境", "signOffParam.getEnv()"},
            {"测试版本", "signOffParam.getVersion()"},
            {"编译URL", ""},
            {"在线报表", "https://www.baidu.com,https://www.google.com"},
            {"全部测试用例", "String.valueOf(cycle_instance)"},
            {"测试执行率", ""},
            {"测试通过率", ""},
        };
        String[][] reportTable2 = new String[][]{
            {"项目1", "project.getTitle()"},
            {"测试环境1", "signOffParam.getEnv()"},
            {"测试通过率1", ""},
        };
        String[][] reportTable3 = new String[][]{
            {"项目1", "project.getTitle()"},
            {"测试环境1", "signOffParam.getEnv()"},
            {"测试通过率1", ""},
        };
        String[][] reportTable4 = new String[][]{
            {"项目1", "project.getTitle()"},
            {"测试环境1", "signOffParam.getEnv()"},
            {"测试通过率1", ""},
        };
        String[][] reportTable5 = new String[][]{
            {"项目1", "project.getTitle()"},
            {"测试环境1", "signOffParam.getEnv()"},
            {"测试通过率1", ""},
        };
        String[][] reportTable6 = new String[][]{
            {"项目1", "project.getTitle()"},
            {"测试环境1", "signOffParam.getEnv()"},
            {"测试通过率1", ""},
        };
        String[][] reportTable7 = new String[][]{
            {"项目1", "project.getTitle()"},
            {"测试环境1", "signOffParam.getEnv()"},
            {"测试通过率1", ""},
        };
        String[][] reportTable8 = new String[][]{
            {"项目1", "project.getTitle()"},
            {"测试环境1", "signOffParam.getEnv()"},
            {"测试通过率1", ""},
        };
        String[][] reportTable9 = new String[][]{
            {"项目1", "project.getTitle()"},
            {"测试环境1", "signOffParam.getEnv()"},
            {"测试通过率1", ""},
        };
        String[][] reportTable10 = new String[][]{
            {"项目1", "project.getTitle()"},
            {"测试环境1", "signOffParam.getEnv()"},
            {"测试通过率1", ""},
        };
        String[][] reportTable11 = new String[][]{
            {"项目1", "project.getTitle()"},
            {"测试环境1", "signOffParam.getEnv()"},
            {"测试通过率1", ""},
        };
        String[][] reportTable12 = new String[][]{
            {"项目1", "project.getTitle()"},
            {"测试环境1", "signOffParam.getEnv()"},
            {"测试通过率1", ""},
        };


        try {
            PDFTableUtil pdfTableUtil = new PDFTableUtil("/Users/air/Desktop");
            pdfTableUtil.showText("generate pdf title");
            pdfTableUtil.generate(reportTable1);

            pdfTableUtil.showText("test1");
            pdfTableUtil.generate(reportTable2);

            pdfTableUtil.showText("test1");
            pdfTableUtil.generate(reportTable3);

            pdfTableUtil.showText("test1");
            pdfTableUtil.generate(reportTable4);

            pdfTableUtil.showText("test1");
            pdfTableUtil.generate(reportTable5);

            pdfTableUtil.showText("test1");
            pdfTableUtil.generate(reportTable6);

            pdfTableUtil.showText("test1");
            pdfTableUtil.generate(reportTable7);

            pdfTableUtil.showText("test1");
            pdfTableUtil.generate(reportTable8);

            pdfTableUtil.showText("test9");
            pdfTableUtil.generate(reportTable9);

            pdfTableUtil.showText("test10");
            pdfTableUtil.generate(reportTable10);

            pdfTableUtil.showText("test11");
            pdfTableUtil.generate(reportTable11);

            pdfTableUtil.showText("test12");
            pdfTableUtil.generate(reportTable12);

            pdfTableUtil.save("hello.pdf");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(getClass().getName());
    }
}
