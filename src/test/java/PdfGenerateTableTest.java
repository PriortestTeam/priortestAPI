import com.hu.oneclick.common.util.PDFTableUtil;
import org.junit.Test;

public class PdfGenerateTableTest {
    @Test
    public void testGenerateTable() {
        String[][] reportTable = new String[][]{
            {"项目", "project.getTitle()"},
            {"测试环境", "signOffParam.getEnv()"},
            {"测试版本", "signOffParam.getVersion()"},
            {"编译URL", ""},
            {"在线报表", "https://www.baidu.com,https://www.google.com"},
            {"全部测试用例", "String.valueOf(cycle_instance)"},
            {"测试执行率", ""},
            {"测试通过率", ""},
        };
        String[][] reportTable1 = new String[][]{
            {"项目1", "project.getTitle()"},
            {"测试环境1", "signOffParam.getEnv()"},
            {"测试通过率1", ""},
        };


        try {
            PDFTableUtil pdfTableUtil = new PDFTableUtil("/Users/air/Desktop");
            pdfTableUtil.showText("generate pdf title");
            pdfTableUtil.generate(reportTable);
            pdfTableUtil.showText("test1");
            pdfTableUtil.generate(reportTable1);
            pdfTableUtil.save("hello.pdf");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(getClass().getName());
    }
}
