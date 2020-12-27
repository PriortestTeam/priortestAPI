package com.hu.oneclick.controller;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.server.service.AttachmentService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author qingyang
 */
@RestController
@RequestMapping("attachment")
public class AttachmentController {

    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }


    @PostMapping("addAttachment")
    public Resp<String> addAttachment(@RequestBody MultipartFile file, @RequestParam String type){
        return attachmentService.addAttachment(file,type);
    }





}
