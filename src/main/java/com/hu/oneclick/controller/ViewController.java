package com.hu.oneclick.controller;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.View;
import com.hu.oneclick.server.service.ViewService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author qingyang
 */
@RestController
@RequestMapping("view")
public class ViewController {

    private final ViewService viewService;


    public ViewController(ViewService viewService) {
        this.viewService = viewService;
    }


    @PostMapping("queryViews")
    private Resp<List<View>> queryViews(@RequestBody View view){ return viewService.list(view); }

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
}
