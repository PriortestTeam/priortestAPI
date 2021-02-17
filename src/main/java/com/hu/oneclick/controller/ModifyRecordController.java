package com.hu.oneclick.controller;

import com.hu.oneclick.model.annotation.Page;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.ModifyRecord;
import com.hu.oneclick.server.service.ModifyRecordService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("modifyRecord")
public class ModifyRecordController {

    private final ModifyRecordService modifyRecordService;

    public ModifyRecordController(ModifyRecordService modifyRecordService) {
        this.modifyRecordService = modifyRecordService;
    }

    @Page
    @PostMapping("queryList")
    private Resp<List<ModifyRecord>> queryList(@RequestBody ModifyRecord modifyRecord){
        return modifyRecordService.queryList(modifyRecord);
    }
}
