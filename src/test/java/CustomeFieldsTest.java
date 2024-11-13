import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.hu.oneclick.OneClickApplication;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.dto.CustomFieldsDto;
import com.hu.oneclick.server.service.CustomFieldsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.nio.charset.Charset;

@SpringBootTest(classes = OneClickApplication.class)
@RunWith(SpringRunner.class)
public class CustomeFieldsTest {
    @Autowired
    CustomFieldsService customFieldsService;

    @Test
    public void testUpdate() {
        File file = new File("C:/Users/ywp/Desktop/data.json");
        JSON json = JSONUtil.readJSON(file, Charset.defaultCharset());
        CustomFieldsDto bean = JSONUtil.toBean((JSONObject) json, CustomFieldsDto.class);

        Resp<String> stringResp = customFieldsService.updateValueDropDownBox(bean);
        System.out.println(stringResp.getMsg());
    }
}
