package com.hu.oneclick.model.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * (SysCustomFieldExpand)实体类
 *
 * @author makejava
 * @since 2021-04-11 15:04:59
 */
public class SysCustomFieldExpand implements Serializable {
    private static final long serialVersionUID = -66541877898615659L;

    private  String id;

    private String userId;

    private String projectId;

    private String linkSysCustomField;

    private String values;

    private String sysCustomFieldId;

    private Date createTime;

    private Date updateTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getLinkSysCustomField() {
        return linkSysCustomField;
    }

    public void setLinkSysCustomField(String linkSysCustomField) {
        this.linkSysCustomField = linkSysCustomField;
    }

    public String getSysCustomFieldId() {
        return sysCustomFieldId;
    }

    public void setSysCustomFieldId(String sysCustomFieldId) {
        this.sysCustomFieldId = sysCustomFieldId;
    }

    public String getValues() {
        return values;
    }

    public void setValues(String values) {
        this.values = values;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
