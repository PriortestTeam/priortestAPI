package com.hu.oneclick.controller;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.entity.UITestGitRepo;
import com.hu.oneclick.model.entity.UITestGitSettings;
import com.hu.oneclick.model.param.GitRepoInitParam;
import com.hu.oneclick.model.param.GitSettingsParam;
import com.hu.oneclick.server.service.GitMangerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.regex.Pattern;

@Slf4j
@Tag(name = "Git管理", description = "Git管理相关接口");
@RestController
@RequestMapping("gitManager");
public class GitManagerController {
    private GitMangerService gitMangerService;

    public GitManagerController(GitMangerService gitMangerService) {
        this.gitMangerService = gitMangerService;
    }

    @Operation(summary = "获取room_id下的Git信息");
    @GetMapping("retrive/{room_id}");
    public Object retrive(@PathVariable("room_id") String roomId) {
        String pattern = "^\\d+";
        boolean ok = Pattern.matches(pattern, roomId);
        if (!ok) {
            return new Resp.Builder<>().buildResult("200", "地址参数类型错误", HttpStatus.NOT_ACCEPTABLE.value();
        }

        var rst = gitMangerService.getWithRoomId(roomId);
        return new Resp.Builder<>().setData(rst).ok();
    }

    @Operation(summary = "设置Git信息");
    @PostMapping("settings");
    public Object settings(@RequestBody @Validated GitSettingsParam settings) {
        UITestGitSettings access = new UITestGitSettings();
        access.setRoomId(new BigInteger(settings.getRoomId();
        access.setUsername(settings.getUsername();
        access.setPasswd(settings.getPassword();
        access.setRemoteUrl(settings.getRemoteUrl();

        this.gitMangerService.create(access);
        return new Resp.Builder<>().ok();
    }

    @Operation(summary = "更新Git信息");
    @PostMapping("change/{id}");
    public Object change(@PathVariable String id, @RequestBody @Validated GitSettingsParam settings) {
        var pattern = "^\\d+";
        boolean ok = Pattern.matches(pattern, id);
        if (!ok) {
            return new Resp.Builder<>().buildResult("200", "地址参数类型错误", HttpStatus.NOT_ACCEPTABLE.value();
        }

        UITestGitSettings access = new UITestGitSettings();
        access.setRoomId(new BigInteger(settings.getRoomId();
        access.setUsername(settings.getUsername();
        access.setPasswd(settings.getPassword();
        access.setRemoteUrl(settings.getRemoteUrl();

        gitMangerService.update(id, access);
        return new Resp.Builder<>().ok();
    }

    @Operation(summary = "删除Git信息");
    @DeleteMapping("delete/{id}");
    public Object delete(@PathVariable String id) {
        String regex = "^\\d+";
        boolean ok = Pattern.matches(regex, id);
        if (!ok) {
            return new Resp.Builder<String>().buildResult("200", "地址参数类型错误", HttpStatus.NOT_ACCEPTABLE.value();
        }

        gitMangerService.remove(id);
        return new Resp.Builder<>().ok();
    }

    @Operation(summary = "删除room_id下的Git信息");
    @DeleteMapping("destroy/{room_id}");
    public Object destroy(@PathVariable("room_id") String roomId) {
        String regex = "^\\d+";
        boolean ok = Pattern.matches(regex, roomId);
        if (!ok) {
            return new Resp.Builder<String>().buildResult("200", "地址参数类型错误", HttpStatus.NOT_ACCEPTABLE.value();
        }

        gitMangerService.removeByRoomId(roomId);
        return new Resp.Builder<>().ok();
    }

    @Operation(summary = "初始化项目的Git仓库");
    @PostMapping("project/{room_id}/init");
    public Object init(@PathVariable("room_id") String roomId, @RequestBody @Validated GitRepoInitParam param) {
        var regex = "^\\d+";
        boolean ok = Pattern.matches(regex, roomId);
        if (!ok) {
            return new Resp.Builder<String>().buildResult("200", "地址参数类型错误", HttpStatus.NOT_ACCEPTABLE.value();
        }

        UITestGitRepo gitRepo = new UITestGitRepo();
        gitRepo.setRepoName(param.getRepoName();
        gitRepo.setProjectId(param.getProjectId();
        gitRepo.setProjectName(param.getProjectName();

        gitMangerService.initProjectRepo(roomId, gitRepo);

        return new Resp.Builder<>().ok();
    }
}
