package com.hu.oneclick.controller;

import com.hu.oneclick.model.annotation.Page;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.TestCycle;
import com.hu.oneclick.server.service.TestCycleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("testCycle")
public class TestCycleController {

    private final TestCycleService testCycleService;

    public TestCycleController(TestCycleService testCycleService) {
        this.testCycleService = testCycleService;
    }


    @GetMapping("queryById/{id}")
    public Resp<TestCycle> queryById(@PathVariable String id) {
        return testCycleService.queryById(id);
    }

    @Page
    @PostMapping("queryList")
    public Resp<List<TestCycle>> queryList(@RequestBody TestCycle testCycle) {
        return testCycleService.queryList(testCycle);
    }

    @PostMapping("insert")
    public Resp<String> insert(@RequestBody TestCycle testCycle) {
        return testCycleService.insert(testCycle);
    }

    @PostMapping("update")
    public Resp<String> update(@RequestBody TestCycle testCycle) {
        return testCycleService.update(testCycle);
    }

    @DeleteMapping("delete/{id}")
    public Resp<String> delete(@PathVariable String id) {
        return testCycleService.delete(id);
    }

}
