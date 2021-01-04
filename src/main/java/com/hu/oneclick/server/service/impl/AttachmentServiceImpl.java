package com.hu.oneclick.server.service.impl;

import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.common.security.service.SysPermissionService;
import com.hu.oneclick.common.util.MinioUtil;
import com.hu.oneclick.dao.AttachmentDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.base.Result;
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
import java.util.List;
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

    private final SysPermissionService sysPermissionService;


    @Value("${onclick.minioConfig.bucketName}")
    private String bucketName;

    @Value("${onclick.minioConfig.endpoint}")
    private String endpoint;


    public AttachmentServiceImpl(JwtUserServiceImpl jwtUserService, MinioClient minioClient, AttachmentDao attachmentDao, SysPermissionService sysPermissionService) {
        this.jwtUserService = jwtUserService;
        this.minioClient = minioClient;
        this.attachmentDao = attachmentDao;
        this.sysPermissionService = sysPermissionService;
    }

    @Override
    public Resp<Attachment> list(String type) {
        Attachment attachment = new Attachment();
        attachment.setAreaType(type);
        attachment.setUserId(jwtUserService.getMasterId());
        List<Attachment> attachments = attachmentDao.queryAll(attachment);
        return new Resp.Builder<Attachment>().setData(attachment).total(attachments.size()).ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> addAttachment(MultipartFile file, String type) {
        try {
            sysPermissionService.projectPermission(OneConstant.PERMISSION.ADD);
            AuthLoginUser userLoginInfo = jwtUserService.getUserLoginInfo();
            //新增附件信息
            String fileName = uploadFile(file, type);
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
            if (attachmentDao.insert(attachment) > 0) {
                return new Resp.Builder<String>().setData(endpoint + "/" + path).ok();
            }
            throw new BizException(SysConstantEnum.ADD_FAILED.getCode(), SysConstantEnum.ADD_FAILED.getValue());
        } catch (BizException e) {
            logger.error("class: AttachmentServiceImpl#addAttachment,error []" + e.getMessage());
            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
        } catch (IOException e) {
            logger.error("class: AttachmentServiceImpl#addAttachment,error []" + e.getMessage());
            return new Resp.Builder<String>().buildResult(e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> updateAttachment(MultipartFile file, String attachmentId) {
        try {
            sysPermissionService.projectPermission(OneConstant.PERMISSION.EDIT);
            String masterId = jwtUserService.getMasterId();
            Attachment attachment = attachmentDao.queryById(masterId, attachmentId);
            if (attachment == null) {
                return new Resp.Builder<String>().buildResult("未查询到该文件！");
            }
            //删除之前的文件
            deleteFile(attachment.getUuidFileName());
            //更新附件信息
            String fileName = uploadFile(file, attachment.getAreaType());
            String path = bucketName + "/" + fileName;
            Date date = new Date();
            attachment.setFileName(file.getOriginalFilename());
            attachment.setUuidFileName(fileName);
            attachment.setModifyTime(date);
            attachment.setUploader(jwtUserService.getUserLoginInfo().getSysUser().getUserName());
            attachment.setFilePath(path);
            return Result.updateResult(attachmentDao.update(attachment));
        } catch (BizException e) {
            logger.error("class: AttachmentServiceImpl#updateAttachment,error []" + e.getMessage());
            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
        } catch (IOException e) {
            logger.error("class: AttachmentServiceImpl#updateAttachment,error []" + e.getMessage());
            return new Resp.Builder<String>().buildResult(e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> deleteAttachment(String attachmentId) {
        try {
            sysPermissionService.projectPermission(OneConstant.PERMISSION.DELETE);
            String masterId = jwtUserService.getMasterId();
            Attachment attachment = attachmentDao.queryById(masterId, attachmentId);
            if (attachment == null) {
                return new Resp.Builder<String>().buildResult("未查询到该文件！");
            }
            //删除之前的文件
            deleteFile(attachment.getUuidFileName());
            return Result.deleteResult(attachmentDao.deleteById(attachmentId, masterId));
        } catch (BizException e) {
            logger.error("class: AttachmentServiceImpl#deleteAttachment,error []" + e.getMessage());
            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage());
        }
    }

    /**
     * 文件上传
     *
     * @param file
     */
    private String uploadFile(MultipartFile file, String type) throws IOException {
        String fileName = file.getOriginalFilename();
        verifyFileSize(file, type);
        assert fileName != null;
        String extName = fileName.substring(fileName.lastIndexOf("."));
        fileName = UUID.randomUUID().toString().replace("-", "") + extName;
        MinioUtil.putObject(minioClient, bucketName, fileName, file.getInputStream(), file);
        return fileName;
    }

    /**
     * 删除文件
     *
     * @param fileName
     */
    private void deleteFile(String fileName) {
        MinioUtil.rmObject(minioClient, bucketName, fileName);
    }

    /**
     * TODO 验证文件大小
     *
     * @param file
     * @param type
     */
    private void verifyFileSize(MultipartFile file, String type) {

    }

}
