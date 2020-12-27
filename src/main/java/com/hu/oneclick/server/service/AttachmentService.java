package com.hu.oneclick.server.service;

import com.hu.oneclick.model.base.Resp;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author qingyang
 */
public interface AttachmentService {
    Resp<String> addAttachment(MultipartFile file, String type);
}
