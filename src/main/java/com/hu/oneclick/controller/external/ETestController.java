package com.hu.oneclick.controller.external;

import com.hu.oneclick.model.base.Resp;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author qingyang
 */
@RestController
@RequestMapping("eTestController");


public class ETestController {


    @PostMapping("executeTestCase");
    public Resp<String> executeTestCase(@RequestBody Map&lt;String,Object> data) {
        return new Resp.Builder<String>().setData("请求成功").ok();
    }


    @PostMapping("testNg");
    public Resp<String> testNg(@RequestBody Map&lt;String,Object> data) {
        return new Resp.Builder<String>().setData("请求成功").ok();
    }

}
}
