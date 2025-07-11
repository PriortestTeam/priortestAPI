package com.hu.oneclick.controller.user;

import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.controller.req.RegisterBody;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.entity.SysUser;
import com.hu.oneclick.model.entity.SysUserToken;
import com.hu.oneclick.model.domain.dto.ActivateAccountDto;
import com.hu.oneclick.model.domain.dto.SubUserDto;
import com.hu.oneclick.model.domain.dto.SysProjectPermissionDto;
import com.hu.oneclick.model.domain.dto.SysUserTokenDto;
import com.hu.oneclick.server.user.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;
import java.util.Map;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author qingyang
 */
@RestController
@Tag(name = "用户管理", description = "用户管理相关接口")
@RequestMapping("user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("register")
    public Resp<String> register(@RequestBody @Validated RegisterBody registerBody) {
        return userService.register(registerBody);
    }

    @PostMapping("modifyPassword")
    public Resp<String> modifyPassword(@RequestBody Map<String, String> args) {
        return userService.modifyPassword(args);
    }

    @PostMapping("resetPassword")
    public Resp<String> resetPassword(@RequestBody Map<String, String> args) {
        return userService.resetPassword(args);
    }


    @GetMapping("queryEmailDoesItExist")
    public Resp<String> queryEmailDoesItExist(@RequestParam String email) {
        return userService.queryEmailDoesItExist(email);
    }

    @PostMapping("updateUserInfo")
    public Resp<String> updateUserInfo(@RequestBody SysUser sysUser) {
        return userService.updateUserInfo(sysUser);
    }

    @GetMapping("queryUserInfo")
    public Resp<SysUser> queryUserInfo() {
        return userService.queryUserInfo();
    }

    @GetMapping("queryUserPermissions")
    public Resp<List<SysProjectPermissionDto>> queryUserPermissions() {
        return userService.queryUserPermissions();
    }

    /**
     * 获取子用户列表，下拉框使用
     *
     * @param subUserName
     * @return
     */
    @PostMapping("queryByNameSubUsers")
    public Resp<List<SubUserDto>> queryByNameSubUsers(@RequestParam(required = false) String subUserName) {
        return userService.queryByNameSubUsers(subUserName);
    }

    @Operation(summary = "激活账户")
    @PostMapping("activateAccount")
    public Resp<String> activateAccount(@RequestBody ActivateAccountDto activateAccountDto) {
        return userService.activateAccount(activateAccountDto, OneConstant.PASSWORD.ACTIVATION);
    }

    @Operation(summary = "忘记密码填写邮箱")
    @PostMapping("forgetThePassword")
    public Resp<String> forgetThePassword(@RequestBody java.util.Map<String, String> request) {
        System.out.println(">>> ========== UserController.forgetThePassword 被调用 ==========");
        System.out.println(">>> 接收到的请求体: " + request);
        System.out.println(">>> 请求体类型: " + (request != null ? request.getClass().getName() : "null"));
        System.out.println(">>> 请求体大小: " + (request != null ? request.size() : 0));
        
        String email = request.get("email");
        System.out.println(">>> 提取的email: " + email);
        
        if (email == null || email.trim().isEmpty()) {
            System.out.println(">>> 邮箱参数为空，返回400错误");
            return new Resp.Builder<String>().buildResult("400", "邮箱参数不能为空", 400);
        }
        
        System.out.println(">>> 调用userService.forgetThePassword，email: " + email);
        Resp<String> result = userService.forgetThePassword(email);
        System.out.println(">>> userService返回结果: " + result);
        System.out.println(">>> ========== UserController.forgetThePassword 执行完成 ==========");
        
        return result;
    }

    @Operation(summary = "忘记密码输入密码")
    @PostMapping("forgetThePasswordIn")
    public Resp<String> forgetThePasswordIn(@RequestBody ActivateAccountDto activateAccountDto) {
        return userService.forgetThePasswordIn(activateAccountDto);
    }

    @Operation(summary = "申请延期填写邮箱")
    @PostMapping("applyForAnExtension")
    public Resp<String> applyForAnExtension(@RequestBody java.util.Map<String, String> request) {
        System.out.println(">>> ========== UserController.applyForAnExtension 被调用 ==========");
        System.out.println(">>> 接收到的请求体: " + request);
        System.out.println(">>> 请求体类型: " + (request != null ? request.getClass().getName() : "null"));
        System.out.println(">>> 请求体大小: " + (request != null ? request.size() : 0));
        
        String email = request.get("email");
        System.out.println(">>> 提取的email: " + email);
        
        if (email == null || email.trim().isEmpty()) {
            System.out.println(">>> 邮箱参数为空，返回400错误");
            return new Resp.Builder<String>().buildResult("400", "邮箱参数不能为空", 400);
        }
        
        System.out.println(">>> 调用userService.applyForAnExtension，email: " + email);
        Resp<String> result = userService.applyForAnExtension(email);
        System.out.println(">>> userService返回结果: " + result);
        System.out.println(">>> ========== UserController.applyForAnExtension 执行完成 ==========");
        
        return result;
    }

    @Operation(summary = "申请延期输入密码")
    @PostMapping("applyForAnExtensionIn")
    public Resp<String> applyForAnExtensionIn(@RequestBody ActivateAccountDto activateAccountDto) {
        return userService.applyForAnExtensionIn(activateAccountDto);
    }

    @Operation(summary = "返回用户的激活次数")
    @PostMapping("getUserActivNumber")
    public Resp<String> getUserActivNumber(@RequestBody java.util.Map<String, String> request) {
        System.out.println(">>> ========== UserController.getUserActivNumber 被调用 ==========");
        System.out.println(">>> 接收到的请求体: " + request);
        System.out.println(">>> 请求体类型: " + (request != null ? request.getClass().getName() : "null"));
        System.out.println(">>> 请求体大小: " + (request != null ? request.size() : 0));
        
        String email = request.get("email");
        System.out.println(">>> 提取的email: " + email);
        
        if (email == null || email.trim().isEmpty()) {
            System.out.println(">>> 邮箱参数为空，返回400错误");
            return new Resp.Builder<String>().buildResult("400", "邮箱参数不能为空", 400);
        }
        
        System.out.println(">>> 调用userService.getUserActivNumber，email: " + email);
        Resp<String> result = userService.getUserActivNumber(email);
        System.out.println(">>> userService返回结果: " + result);
        System.out.println(">>> ========== UserController.getUserActivNumber 执行完成 ==========");
        
        return result;
    }

    @Operation(summary = "管理员生成token")
    @PostMapping("makeToken")
    public Resp<SysUserToken> makeToken(@RequestBody SysUserTokenDto sysUserTokenDto) {
        return userService.makeToken(sysUserTokenDto);
    }

    @Operation(summary = "获取生成的token列表")
    @PostMapping("listTokens")
    public Resp<List<SysUserToken>> listTokens() {
        return userService.listTokens();
    }


    @Operation(summary="删除token")
    @PostMapping("deleteToken")
    public Resp<String> deleteToken(@RequestParam Integer tokenId) {
        return userService.deleteToken(tokenId);
    }


    @Operation(summary="验证链接字符串")
    @PostMapping("verifyLinkString")
    public Resp<String> verifyLinkString(@RequestParam String params) {
        return userService.verifyLinkString(params);
    }

    @Operation(summary="通过项目Id获取用户列表")
    @GetMapping("/listUserByProjectId/{projectId}")
    public Resp<List<Map<String, Object>>> listUserByProjectId(@PathVariable("projectId") Long projectId) {
        return userService.listUserByProjectId(projectId);
    }

}