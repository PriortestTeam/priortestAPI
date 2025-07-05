package com.hu.oneclick.server.service.impl;
import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.common.security.service.JwtUserServiceImpl;
import com.hu.oneclick.common.security.service.SysPermissionService;
import com.hu.oneclick.common.util.MinioUtil;
import com.hu.oneclick.dao.AttachmentDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.base.Result;
import com.hu.oneclick.model.entity.Attachment;
import com.hu.oneclick.model.entity.SysUser;
import com.hu.oneclick.model.domain.dto.AuthLoginUser;
import com.hu.oneclick.server.service.AttachmentService;
import io.minio.MinioClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
    public Resp<List&lt;Attachment>> list(String type, String linkId) {
        if (StringUtils.isEmpty(type) || StringUtils.isEmpty(linkId) {
            return new Resp.Builder<List&lt;Attachment>>().buildResult("参数不能为空！");
        }
        Attachment attachment = new Attachment();
        attachment.setAreaType(type);
        attachment.setUserId(jwtUserService.getMasterId();
        attachment.setLinkId(linkId);
        List&lt;Attachment> attachments = attachmentDao.queryAll(attachment);
        return new Resp.Builder<List&lt;Attachment>>().setData(attachments).total(attachments).ok();
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> addAttachment(MultipartFile file, String type, String linkId) {
        try {
            if (StringUtils.isEmpty(type) || StringUtils.isEmpty(linkId) || file == null) {
                return new Resp.Builder<String>().buildResult("参数不能为空！");
            }
            sysPermissionService.projectPermission(OneConstant.PERMISSION.ADD);
            AuthLoginUser userLoginInfo = jwtUserService.getUserLoginInfo();
            //新增附件信息
            String fileName = uploadFile(file, type);
            String path = bucketName + "/" + fileName;
            Date date = new Date();
            Attachment attachment = new Attachment();
            attachment.setFileName(file.getOriginalFilename();
            attachment.setUuidFileName(fileName);
            attachment.setAreaType(type);
            attachment.setModifyTime(date);
            attachment.setUploadTime(date);
            attachment.setUploader(userLoginInfo.getSysUser().getUserName();
            attachment.setUserId(jwtUserService.getMasterId();
            attachment.setLinkId(linkId);
            attachment.setFilePath(path);
            //添加
            Result.addResult(attachmentDao.insert(attachment);
            return new Resp.Builder<String>().setData(endpoint + "/" + path).ok();
        } catch (BizException e) {
            logger.error("class: AttachmentServiceImpl#addAttachment,error []" + e.getMessage();
            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage();
        } catch (IOException e) {
            logger.error("class: AttachmentServiceImpl#addAttachment,error []" + e.getMessage();
            return new Resp.Builder<String>().buildResult(e.getMessage();
        }
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resp<String> updateAttachment(MultipartFile file, String attachmentId) {
        try {
            if (StringUtils.isEmpty(attachmentId) {
                return new Resp.Builder<String>().buildResult("参数不能为空！");
            }
            sysPermissionService.projectPermission(OneConstant.PERMISSION.EDIT);
            String masterId = jwtUserService.getMasterId();
            Attachment attachment = attachmentDao.queryById(masterId, attachmentId);
            if (attachment == null) {
                return new Resp.Builder<String>().buildResult("未查询到该文件！");
            }
            //删除之前的文件
            deleteFile(attachment.getUuidFileName();
            //更新附件信息
            String fileName = uploadFile(file, attachment.getAreaType();
            String path = bucketName + "/" + fileName;
            Date date = new Date();
            attachment.setFileName(file.getOriginalFilename();
            attachment.setUuidFileName(fileName);
            attachment.setModifyTime(date);
            attachment.setUploader(jwtUserService.getUserLoginInfo().getSysUser().getUserName();
            attachment.setFilePath(path);
            attachment.setUserId(jwtUserService.getMasterId();
            //更新
            Result.updateResult(attachmentDao.update(attachment);
            return new Resp.Builder<String>().setData(endpoint + "/" + path).ok();
        } catch (BizException e) {
            logger.error("class: AttachmentServiceImpl#updateAttachment,error []" + e.getMessage();
            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage();
        } catch (IOException e) {
            logger.error("class: AttachmentServiceImpl#updateAttachment,error []" + e.getMessage();
            return new Resp.Builder<String>().buildResult(e.getMessage();
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
            deleteFile(attachment.getUuidFileName();
            return Result.deleteResult(attachmentDao.deleteById(attachmentId, masterId);
        } catch (BizException e) {
            logger.error("class: AttachmentServiceImpl#deleteAttachment,error []" + e.getMessage();
            return new Resp.Builder<String>().buildResult(e.getCode(), e.getMessage();
        }
    }
    /**
     * @param attachmentId
     * @Param: [attachmentId]
     * @return: com.hu.oneclick.model.base.Resp<java.lang.String>
     * @Author: MaSiyi
     * @Date: 2022/1/15
     */
    @Override
    public Resp<String> deleteAttachmentById(String attachmentId) {
        String masterId = jwtUserService.getMasterId();
        return Result.deleteResult(attachmentDao.deleteById(attachmentId, masterId);
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
        String extName = fileName.substring(fileName.lastIndexOf(".");
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
    @Override
    public Resp<List&lt;Map&lt;String, Object>>> getUserAttachment() {
        SysUser sysUser = jwtUserService.getUserLoginInfo().getSysUser();
        List&lt;Map&lt;String, Object>> list = attachmentDao.getUserAttachment(sysUser.getId(), OneConstant.AREA_TYPE.SIGNOFFSIGN);
        for (Map&lt;String, Object> map : list) {
            String id = String.valueOf(map.get("id");
            map.put("id", id);
        }
        return new Resp.Builder<List&lt;Map&lt;String, Object>>>().setData(list).ok();
    }
    @Override
    public Integer insertAttachment(Attachment attachment) {
        return attachmentDao.insert(attachment);
    }
}
}
}
