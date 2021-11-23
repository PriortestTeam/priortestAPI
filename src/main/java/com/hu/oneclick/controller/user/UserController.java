package com.hu.oneclick.controller.user;

import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.SysUser;
import com.hu.oneclick.model.domain.SysUserToken;
import com.hu.oneclick.model.domain.dto.ActivateAccountDto;
import com.hu.oneclick.model.domain.dto.SubUserDto;
import com.hu.oneclick.model.domain.dto.SysProjectPermissionDto;
import com.hu.oneclick.model.domain.dto.SysUserTokenDto;
import com.hu.oneclick.server.user.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author qingyang
 */
@RestController
@Api(tags = "用户管理")
@RequestMapping("user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("register")
    public Resp<String> register(@RequestBody SysUser registerUser) {
        return userService.register(registerUser);
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

    @ApiOperation("激活账户")
    @PostMapping("activateAccount")
    public Resp<String> activateAccount(@RequestBody ActivateAccountDto activateAccountDto) {
        return userService.activateAccount(activateAccountDto, OneConstant.PASSWORD.ACTIVATION);
    }

    @ApiOperation("忘记密码填写邮箱")
    @PostMapping("forgetThePassword")
    public Resp<String> forgetThePassword(@RequestParam String email) {
        return userService.forgetThePassword(email);
    }

    @ApiOperation("忘记密码输入密码")
    @PostMapping("forgetThePasswordIn")
    public Resp<String> forgetThePasswordIn(@RequestBody ActivateAccountDto activateAccountDto) {
        return userService.forgetThePasswordIn(activateAccountDto);
    }

    @ApiOperation("申请延期填写邮箱")
    @PostMapping("applyForAnExtension")
    public Resp<String> applyForAnExtension(@RequestParam String email) {
        return userService.applyForAnExtension(email);
    }


    @ApiOperation("申请延期输入密码")
    @PostMapping("applyForAnExtensionIn")
    public Resp<String> applyForAnExtensionIn(@RequestBody ActivateAccountDto activateAccountDto) {
        return userService.applyForAnExtensionIn(activateAccountDto);
    }


    @ApiOperation("管理员生成token")
    @PostMapping("makeToken")
    public Resp<SysUserToken> makeToken(@RequestBody SysUserTokenDto sysUserTokenDto) {
        return userService.makeToken(sysUserTokenDto);
    }

    @ApiOperation("获取生成的token列表")
    @PostMapping("listTokens")
    public Resp<List<SysUserToken>> listTokens() {
        return userService.listTokens();
    }


    @ApiOperation("删除token")
    @PostMapping("deleteToken")
    public Resp<String> deleteToken(@RequestParam Integer tokenId) {
        return userService.deleteToken(tokenId);
    }


    @ApiOperation("验证链接字符串")
    @PostMapping("verifyLinkString")
    public Resp<String> verifyLinkString(@RequestParam String linkStr) {
        return userService.verifyLinkString(linkStr);
    }



}
