package com.hu.oneclick.controller;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.View;
import com.hu.oneclick.model.domain.dto.ViewScopeChildParams;
import com.hu.oneclick.model.domain.dto.ViewTreeDto;
import com.hu.oneclick.server.service.ViewService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author qingyang
 */
@RestController
@RequestMapping("view")
@Api(tags = "视图管理")
public class ViewController {

    private final ViewService viewService;


    public ViewController(ViewService viewService) {
        this.viewService = viewService;
    }



    @GetMapping("queryDoesExistByTitle")
    public Resp<String> queryDoesExistByTitle(@RequestParam("projectId") String projectId,
                                              @RequestParam("title") String title,
                                              @RequestParam("scope") String scope){
        return viewService.queryDoesExistByTitle(projectId,title,scope);
    }

    @GetMapping("queryById/{id}")
    public Resp<View> queryById(@PathVariable String id){
        return viewService.queryById(id);
    }

    @GetMapping("getViewScopeChildParams")
    @ApiOperation("根据范围搜索所有字段")
    public Resp<List<ViewScopeChildParams>> getViewScopeChildParams(@RequestParam String scope){
        return viewService.getViewScopeChildParams(scope);
    }


    @PostMapping("queryViews")
    private Resp<List<View>> queryViews(@RequestBody View view){
        return viewService.list(view);
    }

    @PostMapping("addView")
    private Resp<String> addView(@RequestBody View view){
        return viewService.addView(view);
    }

    @PostMapping("updateView")
    private Resp<String> updateView(@RequestBody View view){
        return viewService.updateView(view);
    }

    @DeleteMapping("deleteView/{viewId}")
    private Resp<String> deleteView(@PathVariable String viewId){
        return viewService.deleteView(viewId);
    }


    @GetMapping("queryViewParents")
    private Resp<List<View>> queryViewParents(@RequestParam String scope, @RequestParam String viewTitle){
        return viewService.queryViewParents(scope,viewTitle);
    }

    @GetMapping("queryViewTrees")
    private Resp<List<ViewTreeDto>> queryViewTrees(@RequestParam String scope){
        return viewService.queryViewTrees(scope);
    }

}
