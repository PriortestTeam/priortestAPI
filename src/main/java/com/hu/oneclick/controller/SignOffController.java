package com.hu.oneclick.controller;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.dto.SignOffDto;
import com.hu.oneclick.model.domain.dto.SysCustomFieldVo;
import com.hu.oneclick.server.service.AttachmentService;
import com.hu.oneclick.server.service.ProjectService;
import com.hu.oneclick.server.service.SysCustomFieldService;
import com.hu.oneclick.server.service.TestCaseService;
import com.hu.oneclick.server.service.TestCycleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @author 王富贵
 * @date 2021/9/16 - 21:13
 */
@RestController
@RequestMapping("signOff")
@Api(tags = "验收")
public class SignOffController {

    @Autowired
    private ProjectService projectService;
    @Autowired
    private TestCaseService testCaseService;
    @Autowired
    private TestCycleService testCycleService;
    @Autowired
    private AttachmentService attachmentService;
    @Autowired
    private SysCustomFieldService sysCustomFieldService;

    @GetMapping("/getProjectEnv")
    public Resp<SysCustomFieldVo> getProjectEnv() {
        return sysCustomFieldService.getSysCustomField("test_env");
    }


    @GetMapping("/getProjectVersion")
    public Resp<SysCustomFieldVo> getProjectVersion() {
        return sysCustomFieldService.getSysCustomField("versions");
    }

    @GetMapping("/getIssue")
    public Resp<SysCustomFieldVo> getIssue() {
        return sysCustomFieldService.getSysCustomField("issue_status");
    }

    @GetMapping("/getTestCycleDetail")
    public Resp<List<Map<String, String>>> getTestCycleVersion(@RequestParam String projectId, @RequestParam String env, @RequestParam String version) {
        return testCycleService.getTestCycleVersion(projectId, env, version);
    }

    @PostMapping("/generate")
    @ApiOperation("生成pdf文档")
    public Resp<String> generate(@RequestBody SignOffDto signOffDto) {
        return projectService.generate(signOffDto);
    }

    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Resp<String> upload(@RequestBody MultipartFile file) {
        return projectService.upload(file);
    }

    @GetMapping("/getUserAttachmentSign")
    @ApiOperation("访问用户签名文件路径")
    public Resp<List<String>> getUserAttachmentSign() {
        return attachmentService.getUserAttachment();
    }


}
