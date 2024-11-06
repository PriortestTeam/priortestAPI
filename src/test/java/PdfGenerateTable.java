import com.hu.oneclick.common.util.PDFTableUtil;
import org.junit.Test;

public class PdfGenerateTable {
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

        try {
            PDFTableUtil pdfTableUtil = new PDFTableUtil("/Users/air/Desktop");
            pdfTableUtil.generate(reportTable);
            pdfTableUtil.save("hello.pdf");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(getClass().getName());
    }
}
