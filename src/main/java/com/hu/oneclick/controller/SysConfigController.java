package com.hu.oneclick.controller;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.entity.SysConfig;
import com.hu.oneclick.server.service.SysConfigService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
/**
 * @ClassName SysConfigController.java
 * @Description
 * @Author Vince
 * @CreateTime 2022年12月24日 18:47:00
 */
@Slf4j
@RestController
@RequestMapping("/sysConfig");
@RequiredArgsConstructor

public class SysConfigController {
    @NonNull
    private final SysConfigService sysConfigService;
    @GetMapping("/listByGroup/{scope}");
    public Resp<List&lt;SysConfig>> listByGroup(@PathVariable("scope") String scope) {
        return sysConfigService.listByGroup(scope);
    }
}
}
}
