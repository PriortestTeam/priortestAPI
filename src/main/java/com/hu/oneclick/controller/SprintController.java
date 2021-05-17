package com.hu.oneclick.controller;

import com.hu.oneclick.model.annotation.Page;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.Sprint;
import com.hu.oneclick.model.domain.dto.SprintDto;
import com.hu.oneclick.server.service.SprintService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author qingyang
 */
@RestController
@RequestMapping("sprint")
public class SprintController {

    private final SprintService sprintService;

    public SprintController(SprintService sprintService) {
        this.sprintService = sprintService;
    }


    @GetMapping("queryById/{id}")
    public Resp<Sprint> queryById(@PathVariable String id) {
        return sprintService.queryById(id);
    }

    @Page
    @PostMapping("queryList")
    public Resp<List<Sprint>> queryList(@RequestBody SprintDto sprint) {
        return sprintService.queryList(sprint);
    }

    @PostMapping("insert")
    public Resp<String> insert(@RequestBody Sprint sprint) {
        return sprintService.insert(sprint);
    }

    @PostMapping("update")
    public Resp<String> update(@RequestBody Sprint sprint) {
        return sprintService.update(sprint);
    }

    @DeleteMapping("delete/{id}")
    public Resp<String> delete(@PathVariable String id) {
        return sprintService.delete(id);
    }

}
