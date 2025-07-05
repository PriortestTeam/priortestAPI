package com.hu.oneclick.model.entity;

import com.hu.oneclick.model.base.BaseEntity;

import java.io.Serializable;
import java.util.Date;

/**
 * 子用户关联的项目权限表(SysProjectPermission)实体类
 *
 * @author makejava
 * @since 2020-11-20 10:32:50
 */
public class SysProjectPermission extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 344033102544943132L;
    /**
     * 关联用户id
     */
    private String subUserId;
    /**
     * 关联的项目id
     */
    private String projectId;
    /**
     * 关联权限表id
     */
    private String operationAuthId;

    private Date updateTime;

    private Date createTime;

    public String getSubUserId() {
        return subUserId;
    }

    public void setSubUserId(String subUserId) {
        this.subUserId = subUserId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getOperationAuthId() {
        return operationAuthId;
    }

    public void setOperationAuthId(String operationAuthId) {
        this.operationAuthId = operationAuthId;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
