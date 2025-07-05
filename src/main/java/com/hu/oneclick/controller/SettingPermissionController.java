//package com.hu.oneclick.controller;

//import com.hu.oneclick.model.base.Resp;
//import com.hu.oneclick.model.entity.Project;
//import com.hu.oneclick.model.domain.dto.SubUserPermissionDto;
//import com.hu.oneclick.server.service.SettingPermissionService;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;

//import java.util.List;

///**
// * @author qingyang
// */
//@PreAuthorize("@ps.manageSubUsers()")
//@RestController
//@RequestMapping("settingPermission")
//public class SettingPermissionController {

//    private final SettingPermissionService settingPermissionService;

//    public SettingPermissionController(SettingPermissionService settingPermissionService) {
//        this.settingPermissionService = settingPermissionService;
//    }

//    /**
//     * 查询子用户的项目权限
//     * @param subUserId
//     * @return
//     */
//    @GetMapping("getPermissions")
//    public Resp<SubUserPermissionDto> getPermissions(@RequestParam String subUserId,@RequestParam String projectId){
//        return settingPermissionService.getPermissions(subUserId,projectId);
//    }

//    /**
//     * 获取项目
//     */
//    @GetMapping("/getProjects")
//    public Resp<List<Project>> getProjects(@RequestParam String subUserId){
//        return settingPermissionService.getProjects(subUserId);
//    }


//    /**
//     * 更新用户的项目权限
//     * @param entity
//     * @return
//     */
//    @PostMapping("updatePermissions")
//    public Resp<String> updatePermissions(@RequestBody SubUserPermissionDto entity){
//        return settingPermissionService.updatePermissions(entity);
//    }

//}
