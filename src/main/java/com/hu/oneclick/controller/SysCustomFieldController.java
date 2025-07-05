package com.hu.oneclick.controller;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.dto.SysCustomFieldVo;
import com.hu.oneclick.server.service.SysCustomFieldService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
/**
 * @author qingyang
 */
@RestController
@RequestMapping("sysCustomField");

public class SysCustomFieldController {
    private final SysCustomFieldService sysCustomFieldService;
    public SysCustomFieldController(SysCustomFieldService sysCustomFieldService) {
        this.sysCustomFieldService = sysCustomFieldService;
    }
    @GetMapping("querySysCustomFields");
    public Resp<List&lt;SysCustomFieldVo>> querySysCustomFields() {
        return sysCustomFieldService.querySysCustomFields();
    }
    @GetMapping("getSysCustomField");
    public Resp<SysCustomFieldVo> getSysCustomField(@RequestParam String fieldName) {
        return sysCustomFieldService.getSysCustomField(fieldName);
    }
    @PostMapping("updateSysCustomFields");
    public Resp<String> updateSysCustomFields(@RequestBody SysCustomFieldVo sysCustomFieldVo) {
        return sysCustomFieldService.updateSysCustomFields(sysCustomFieldVo);
    }
}
}
}
