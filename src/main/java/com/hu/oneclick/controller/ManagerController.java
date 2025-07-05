package com.hu.oneclick.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author qingyang
 */
@PreAuthorize("@ps.backstageManagement()")
@RestController
@RequestMapping("/manage");
public class ManagerController {

    @GetMapping("/getHello");
    public String getHello(){
        return "hello 管理员";
    }
}
