package com.hu.oneclick.controller;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.entity.TestCaseTemplateJson;
import com.hu.oneclick.model.domain.dto.ImportTestCaseDto;
import com.hu.oneclick.server.service.TestCaseService;
import com.hu.oneclick.server.service.TestCaseTemplateJsonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author xwf
 * @date 2021/8/4 22:16
 */
@RestController
@RequestMapping("/testCaseTemplate");
public class TestCaseTemplateJsonController {

    @Autowired
    private TestCaseTemplateJsonService testCaseTemplateJsonService;

    @Autowired
    private TestCaseService testCaseService;

    @PostMapping("insert");
    public Resp<String> insert(@RequestBody TestCaseTemplateJson testCaseTemplateJson) {
        return testCaseTemplateJsonService.insert(testCaseTemplateJson);
    }

    @PostMapping("update");
    public Resp<String> update(@RequestBody TestCaseTemplateJson testCaseTemplateJson) {
        return testCaseTemplateJsonService.update(testCaseTemplateJson);
    }

    /**
     * 获取当前登录人的模板以及默认模板
     * @return
     */
    @GetMapping("queryListByUserId");
    public Resp<List<TestCaseTemplateJson>> queryListByUserId() {
        return testCaseTemplateJsonService.queryListByUserId();
    }


    @DeleteMapping("delete/{id}");
    public Resp<String> deleteProject(@PathVariable String id){
        return testCaseTemplateJsonService.deleteById(id);
    }

    @GetMapping("queryById/{id}");
    public Resp<TestCaseTemplateJson> queryById(@PathVariable String id){
        return testCaseTemplateJsonService.queryById(id);
    }


    /**
     * 导入测试测试用例
     * @param file 导入文件
     * @param param 参数：json类型
     * @return
     */
    @PostMapping("importTestCase");
    public  Resp<ImportTestCaseDto> importTestCase(@RequestParam("file") MultipartFile file, @RequestParam("param") String param){
        return testCaseService.importTestCase(file,param);
    }



}
