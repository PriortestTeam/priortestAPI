package com.hu.oneclick.controller;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.entity.UITestSourceCodeAccess;
import com.hu.oneclick.model.param.GitSettingsParam;
import com.hu.oneclick.server.service.GitMangerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.regex.Pattern;

@Api(tags = "Git管理")
@RestController
@RequestMapping("gitManager")
public class GitManagerController {
    private GitMangerService gitMangerService;

    public GitManagerController(GitMangerService gitMangerService) {
        this.gitMangerService = gitMangerService;
    }

    @ApiOperation("设置Git信息")
    @PostMapping("settings")
    public Object settings(@RequestBody GitSettingsParam settings) {
        UITestSourceCodeAccess access = new UITestSourceCodeAccess();
        access.setUsername(settings.getUsername());
        access.setPasswd(settings.getPassword());
        access.setRemoteName(settings.getRemoteName());
        access.setRemoteUrl(settings.getRemoteUrl());
        this.gitMangerService.create(access);

        return new Resp.Builder<>().ok();
    }

    @ApiOperation("更新Git信息")
    @PostMapping("change/{id}")
    public Object change(@PathVariable String id, @RequestBody GitSettingsParam settings) {
        var pattern = "^\\d+";
        boolean ok = Pattern.matches(pattern, id);
        if (!ok) {
            return new Resp.Builder<>().buildResult("200", "地址参数类型错误", HttpStatus.NOT_ACCEPTABLE.value());
        }

        UITestSourceCodeAccess access = new UITestSourceCodeAccess();
        access.setUsername(settings.getUsername());
        access.setPasswd(settings.getPassword());
        access.setRemoteName(settings.getRemoteName());
        access.setRemoteUrl(settings.getRemoteUrl());
        gitMangerService.update(id, access);

        return new Resp.Builder<>().ok();
    }

    @ApiOperation("删除Git信息")
    @GetMapping("delete/{id}")
    public Object delete(@PathVariable String id) {
        String regex = "^\\d+";
        boolean ok = Pattern.matches(regex, id);
        if (!ok) {
            return new Resp.Builder<String>().buildResult("200", "地址参数类型错误", HttpStatus.NOT_ACCEPTABLE.value());
        }
        gitMangerService.remove(id);
        return new Resp.Builder<>().ok();
    }
}
