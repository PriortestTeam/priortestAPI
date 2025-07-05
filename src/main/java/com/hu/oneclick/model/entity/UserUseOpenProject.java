package com.hu.oneclick.model.entity;

import com.hu.oneclick.model.base.BaseEntity;

import java.io.Serializable;

/**
 * @author qingyang
 */


public class UserUseOpenProject extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 4060874610596261708L;

    private String userId;
    private String projectId;
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
}
}
