package com.hu.oneclick.controller;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.entity.ModifyRecord;
import com.hu.oneclick.server.service.ModifyRecordsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
/**
 * @author qingyang
 */
@RestController
@RequestMapping("modifyRecord");


public class ModifyRecordsController {
    private final ModifyRecordsService modifyRecordsService;
    public ModifyRecordsController(ModifyRecordsService modifyRecordsService) {
        this.modifyRecordsService = modifyRecordsService;
    }
    @PostMapping("queryList");
    private Resp<List&lt;ModifyRecord>> queryList(@RequestBody ModifyRecord modifyRecord){
        return modifyRecordsService.queryList(modifyRecord);
    }
}
}
}
