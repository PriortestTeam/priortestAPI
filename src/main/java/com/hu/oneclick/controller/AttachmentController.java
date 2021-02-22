package com.hu.oneclick.controller;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.Attachment;
import com.hu.oneclick.server.service.AttachmentService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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


    @GetMapping("list")
    public Resp<List<Attachment>> list(@RequestParam String type, @RequestParam String linkId){
        return attachmentService.list(type,linkId);
    }

    @PostMapping("addAttachment")
    public Resp<String> addAttachment(@RequestBody MultipartFile file,
                                      @RequestParam String type,
                                      @RequestParam String linkId){
        return attachmentService.addAttachment(file,type,linkId);
    }

    @PostMapping("updateAttachment/{attachmentId}")
    public Resp<String> updateAttachment(@RequestBody MultipartFile file, @PathVariable String attachmentId){
        return attachmentService.updateAttachment(file,attachmentId);
    }

    @DeleteMapping("deleteAttachment/{attachmentId}")
    public Resp<String> deleteAttachment(@PathVariable String attachmentId){
        return attachmentService.deleteAttachment(attachmentId);
    }




}
