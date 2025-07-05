package com.hu.oneclick.model.entity;

import com.hu.oneclick.model.base.BaseEntity;

import java.io.Serializable;
import java.util.Date;

/**
 * 附件表(Attachment)实体类
 *
 * @author makejava
 * @since 2020-12-20 20:40:38
 */


public class Attachment extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 720017392887734003L;

    /**
     * 关联主用户
     */
    private String userId;
    /**
     * 唯一文件名
     */
    private String uuidFileName;
    /**
     * 文件访问路径
     */
    private String filePath;
    /**
     * 上传时间
     */
    private Date uploadTime;
    /**
     * 上传用户
     */
    private String uploader;
    /**
     * 修改时间
     */
    private Date modifyTime;
    /**
     * 修改人
     */
    private String modifyUser;
    /**
     * project、testCase 、testCayle
     */
    private String areaType;
    /**
     * 文件名称
     */
    private String fileName;

    private String linkId;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUuidFileName() {
        return uuidFileName;
    }

    public void setUuidFileName(String uuidFileName) {
        this.uuidFileName = uuidFileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getUploader() {
        return uploader;
    }

    public void setUploader(String uploader) {
        this.uploader = uploader;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(String modifyUser) {
        this.modifyUser = modifyUser;
    }

    public String getAreaType() {
        return areaType;
    }

    public void setAreaType(String areaType) {
        this.areaType = areaType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getLinkId() {
        return linkId;
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId;
    }

    @Override
    public String toString() {
        return "Attachment{" +
                "userId='" + userId + '\'' +
                ", uuidFileName='" + uuidFileName + '\'' +
                ", filePath='" + filePath + '\'' +
                ", uploadTime=" + uploadTime +
                ", uploader='" + uploader + '\'' +
                ", modifyTime=" + modifyTime +
                ", modifyUser='" + modifyUser + '\'' +
                ", areaType='" + areaType + '\'' +
                ", fileName='" + fileName + '\'' +
                ", linkId='" + linkId + '\'' +
                '}';
    }
}
}
}
