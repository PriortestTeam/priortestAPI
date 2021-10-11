package com.hu.oneclick.controller;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.SystemConfig;
import com.hu.oneclick.model.domain.TestCase;
import com.hu.oneclick.server.service.SystemConfigService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public Resp<String> insert(@RequestBody SystemConfig systemConfig) {
        return systemConfigService.insert(systemConfig);
    }

    @PostMapping("/update")
    public Resp<String> update(@RequestBody SystemConfig systemConfig) {
        return systemConfigService.update(systemConfig);
    }

    @PostMapping("/getData")
    public Resp<String> getData(@RequestParam String key) {
        String data = systemConfigService.getData(key);
        return new Resp.Builder().setData(data).ok();
    }

    @DeleteMapping("/delete")
    public Resp<String> delete(@RequestParam String key) {
        String data = systemConfigService.delete(key);
        return new Resp.Builder().setData(data).ok();
    }

}
