import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.hu.oneclick.common.util.PDFTableUtil;
import com.hu.oneclick.model.param.SignOffParam;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestCode {
    @Test
    public void test() throws IOException {
        File file = new File("C:/Users/ywp/Desktop/out-data.json");
        String s = Files.readString(file.toPath());
        JSONArray parsedArray = JSONUtil.parseArray(s);

//        List<? extends Map<String, Object>> xxxx = JSONUtil.toList(parsedArray, (new HashMap<String, Object>() {
//        }).getClass());


        List<? extends Map<String, Object>> list = JSONUtil.toList(parsedArray, new HashMap<String, Object>() {
        }.getClass());
        System.out.println(list.size());

        List<? extends Map<String, Object>> fields = list.stream().filter(map -> new BigInteger(map.get("customFieldLinkId").toString())
            .compareTo(BigInteger.ZERO) == 0).collect(Collectors.toList());
        for (var map1 : fields) {
            Map<String, Object> existed = list.stream().filter(map -> map.get("customFieldLinkId").equals(map1.get("customFieldId")))
                .findFirst().orElse(null);
            if (existed != null) {
                map1.put("child", new HashMap<>() {{
                    put("type", existed.get("type").toString());
                    put("possibleValue", existed.get("possibleValue").toString());
                    put("projectId", existed.get("projectId").toString());
                }});
                System.out.println(map1);
            }
        }

    }

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
