package com.hu.oneclick.controller;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.TestCaseTemplateJson;
import com.hu.oneclick.server.service.TestCaseService;
import com.hu.oneclick.server.service.TestCaseTemplateJsonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * @author xwf
 * @date 2021/8/4 22:16
 */
@RestController
@RequestMapping("/testCaseTemplate")
public class TestCaseTemplateJsonController {

    @Autowired
    private TestCaseTemplateJsonService testCaseTemplateJsonService;

    @Autowired
    private TestCaseService testCaseService;

    @Resource(name="asyncTaskExecutor")
    private ExecutorService executorService;

    @PostMapping("insert")
    public Resp<String> insert(@RequestBody TestCaseTemplateJson testCaseTemplateJson) {
        return testCaseTemplateJsonService.insert(testCaseTemplateJson);
    }

    @PostMapping("update")
    public Resp<String> update(@RequestBody TestCaseTemplateJson testCaseTemplateJson) {
        return testCaseTemplateJsonService.update(testCaseTemplateJson);
    }

    /**
     * 获取当前登录人的模板以及默认模板
     * @return
     */
    @GetMapping("queryListByUserId")
    public Resp<List<TestCaseTemplateJson>> queryListByUserId() {
        return testCaseTemplateJsonService.queryListByUserId();
    }


    @DeleteMapping("delete/{id}")
    public Resp<String> deleteProject(@PathVariable String id){
        return testCaseTemplateJsonService.deleteById(id);
    }

    @GetMapping("queryById/{id}")
    public Resp<TestCaseTemplateJson> queryById(@PathVariable String id){
        return testCaseTemplateJsonService.queryById(id);
    }


    /**
     * 导入测试测试用例
     * @param file 导入文件
     * @param param 参数：json类型
     * @return
     */
    @PostMapping("importTestCase")
    public  Resp<Object> importTestCase(@RequestParam("file") MultipartFile file, @RequestParam("param") String param, HttpServletRequest request) throws IOException {
//.getStoreLocation().getAbsolutePath();
        File path = new File(request.getSession().getServletContext().getRealPath("/") + "/upload/");
        String originalFilename = file.getOriginalFilename();
        if (!path.exists()) {
            path.mkdirs();
        }
        File newfile = new File(path,originalFilename);
        file.transferTo(newfile);
        executorService.submit(() -> {
            testCaseService.importTestCase(newfile, param);
        });
       return new Resp.Builder<Object>().buildResult("正在导入中，请稍后关注您的邮箱，查看导入结果~");
    }



}
