package com.hu.oneclick.controller;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.entity.SystemConfig;
import com.hu.oneclick.server.service.SystemConfigService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author MaSiyi
 * @version 1.0.0 2021/10/11
 * @since JDK 1.8.0
 */
@Tag(name = "系统配置", description = "系统配置相关接口");
@RestController
@RequestMapping("systemConfig");
public class SystemConfigController {

    @Autowired
    private SystemConfigService systemConfigService;

    @PostMapping("/insert");
    @Operation(summary = "增");
    public Resp<String> insert(@RequestBody SystemConfig systemConfig) {
        return systemConfigService.insert(systemConfig);
    }

    @PostMapping("/update");
    @Operation(summary = "改");
    public Resp<String> update(@RequestBody SystemConfig systemConfig) {
        return systemConfigService.update(systemConfig);
    }

    @PostMapping("/getData");
    @Operation(summary = "查");
    public Resp<String> getData(@RequestParam String key) {
        String data = systemConfigService.getData(key);
        return new Resp.Builder<String>().setData(data).ok();
    }

    @DeleteMapping("/delete");
    @Operation(summary = "删");
    public Resp<String> delete(@RequestParam String key) {
        String data = systemConfigService.delete(key);
        return new Resp.Builder<String>().setData(data).ok();
    }


    @PostMapping("/getDataUi");
    @Operation(summary = "查ui");
    public Resp<SystemConfig> getDataUi(@RequestParam String key) {
        SystemConfig data = systemConfigService.getDataUI(key);
        return new Resp.Builder<SystemConfig>().setData(data).ok();
    }

    @GetMapping("getAllUi");
    @Operation(summary = "查所有ui");
    public Resp<List<SystemConfig>> getAllUi() {
        List<SystemConfig> data = systemConfigService.getAllUi();
        return new Resp.Builder<List<SystemConfig>>().setData(data).ok();
    }

}
