package com.hu.oneclick.server.service;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.Attachment;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @author qingyang
 */
public interface AttachmentService {

    Resp<List<Attachment>> list(String type, String linkId);

    Resp<String> addAttachment(MultipartFile file, String type, String linkId);

    Resp<String> updateAttachment(MultipartFile file, String attachmentId);

    Resp<String> deleteAttachment(String attachmentId);

    /**
     * @Param: [attachmentId]
     * @return: com.hu.oneclick.model.base.Resp<java.lang.String>
     * @Author: MaSiyi
     * @Date: 2022/1/15
     */
    Resp<String> deleteAttachmentById(String attachmentId);



    Resp<List<Map<String, Object>>> getUserAttachment();

    Integer insertAttachment(Attachment attachment);


}
