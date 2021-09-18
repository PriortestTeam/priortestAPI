package com.hu.oneclick.controller;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.dto.SignOffDto;
import com.hu.oneclick.model.domain.dto.SubUserPermissionDto;
import com.hu.oneclick.server.service.ProjectService;
import com.hu.oneclick.server.service.TestCaseService;
import com.hu.oneclick.server.service.TestCycleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author 王富贵
 * @date 2021/9/16 - 21:13
 */
@RestController
@RequestMapping("signOff")
public class SignOffController {

    @Autowired
    private ProjectService projectService;
    @Autowired
    private TestCaseService testCaseService;
    @Autowired
    private TestCycleService testCycleService;

    @GetMapping("/getProject")
    public Resp<List<String>> getProject(){
        return projectService.getProject();
    }

    @GetMapping("/getProjectEnv")
    public Resp<List<String>> getProjectEnv(@RequestParam String projectId){
        return testCaseService.getProjectEnv(projectId);
    }

    @GetMapping("/getProjectVersion")
    public Resp<List<String>> getProjectVersion(@RequestParam String projectId){
        return testCaseService.getProjectVersion(projectId);
    }

    @GetMapping("/getTestCycleVersion")
    public Resp<List<String>> getTestCycleVersion(@RequestParam String projectId){
        return testCycleService.getTestCycleVersion(projectId);
    }

    @PostMapping("/generate")
    public Resp<String> generate(@RequestBody SignOffDto signOffDto) {
        return projectService.generate(signOffDto);
    }

    @PostMapping("/upload")
    public Resp<String> upload(@RequestBody MultipartFile file, HttpServletRequest req) {
        return projectService.upload(file,req);
    }


}
