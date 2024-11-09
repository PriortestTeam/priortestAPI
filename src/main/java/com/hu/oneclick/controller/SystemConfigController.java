package com.hu.oneclick.controller;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.entity.SystemConfig;
import com.hu.oneclick.server.service.SystemConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@Api(tags = "系统配置")
@RestController
@RequestMapping("systemConfig")
public class SystemConfigController {

    @Autowired
    private SystemConfigService systemConfigService;

    @PostMapping("/insert")
    @ApiOperation("增")
    public Resp<String> insert(@RequestBody SystemConfig systemConfig) {
        return systemConfigService.insert(systemConfig);
    }

    @PostMapping("/update")
    @ApiOperation("改")
    public Resp<String> update(@RequestBody SystemConfig systemConfig) {
        return systemConfigService.update(systemConfig);
    }

    @PostMapping("/getData")
    @ApiOperation("查")
    public Resp<String> getData(@RequestParam String key) {
        String data = systemConfigService.getData(key);
        return new Resp.Builder<String>().setData(data).ok();
    }

    @DeleteMapping("/delete")
    @ApiOperation("删")
    public Resp<String> delete(@RequestParam String key) {
        String data = systemConfigService.delete(key);
        return new Resp.Builder<String>().setData(data).ok();
    }


    @PostMapping("/getDataUi")
    @ApiOperation("查ui")
    public Resp<SystemConfig> getDataUi(@RequestParam String key) {
        SystemConfig data = systemConfigService.getDataUI(key);
        return new Resp.Builder<SystemConfig>().setData(data).ok();
    }

    @GetMapping("getAllUi")
    @ApiOperation("查所有ui")
    public Resp<List<SystemConfig>> getAllUi() {
        List<SystemConfig> data = systemConfigService.getAllUi();
        return new Resp.Builder<List<SystemConfig>>().setData(data).ok();
    }

}
