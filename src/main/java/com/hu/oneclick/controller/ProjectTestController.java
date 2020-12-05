package com.hu.oneclick.controller;

import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.security.service.PermissionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author qingyang
 */
@RestController
@RequestMapping("project")
public class ProjectTestController {

    private final PermissionService permissionService;

    public ProjectTestController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping("/getHello")
    public String getHello(@RequestParam String projectId){
        permissionService.hasPermission(OneConstant.PERMISSION.ONE_SPRINT,
                OneConstant.PERMISSION.VIEW,projectId);
        return "hello 您可以访问project 接口";
    }

}
