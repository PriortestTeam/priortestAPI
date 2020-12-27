package com.hu.oneclick.server.service.impl;

import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.common.util.MinioUtil;
import com.hu.oneclick.dao.AttachmentDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.Attachment;
import com.hu.oneclick.model.domain.dto.AuthLoginUser;
import com.hu.oneclick.server.service.AttachmentService;
import io.minio.MinioClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

/**
 * @author qingyang
 */
@Service
public class AttachmentServiceImpl implements AttachmentService {

    private final static Logger logger = LoggerFactory.getLogger(AttachmentServiceImpl.class);

    private final JwtUserServiceImpl jwtUserService;

    private final MinioClient minioClient;

    private final AttachmentDao attachmentDao;


    @Value("${oneclick.minioConfig.oneclick}")
    private String bucketName;

    @Value("${oneclick.minioConfig.endpoint}")
    private String endpoint;


    public AttachmentServiceImpl(JwtUserServiceImpl jwtUserService, MinioClient minioClient, AttachmentDao attachmentDao) {
        this.jwtUserService = jwtUserService;
        this.minioClient = minioClient;
        this.attachmentDao = attachmentDao;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> addAttachment(MultipartFile file, String type) {
        String fileName = file.getOriginalFilename();
        try {
            assert fileName != null;
            String extName = fileName.substring(fileName.lastIndexOf("."));
            fileName = UUID.randomUUID().toString().replace("-", "") + extName;
            MinioUtil.putObject(minioClient, bucketName, fileName, file.getInputStream(),file);

            AuthLoginUser userLoginInfo = jwtUserService.getUserLoginInfo();
            //新增附件信息
            String path = bucketName + "/" + fileName;
            Date date = new Date();
            Attachment attachment = new Attachment();
            attachment.setFileName(file.getOriginalFilename());
            attachment.setUuidFileName(fileName);
            attachment.setAreaType(type);
            attachment.setModifyTime(date);
            attachment.setUploadTime(date);
            attachment.setUploader(userLoginInfo.getSysUser().getUserName());
            attachment.setUserId(jwtUserService.getMasterId());
            attachment.setFilePath(path);
            if (attachmentDao.insert(attachment) > 0){
                return new Resp.Builder<String>().setData(endpoint + "/" + path).ok();
            }
            throw new BizException(SysConstantEnum.ADD_FAILED.getCode(),SysConstantEnum.ADD_FAILED.getValue());
        }catch (BizException e){
            logger.error("class: AttachmentServiceImpl#addAttachment,error []" + e.getMessage());
            return new Resp.Builder<String>().buildResult(e.getCode(),e.getMessage());
        } catch (IOException e) {
            logger.error("class: AttachmentServiceImpl#addAttachment,error []" + e.getMessage());
            return new Resp.Builder<String>().buildResult(e.getMessage());
        }
    }
}
