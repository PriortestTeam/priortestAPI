package com.hu.oneclick.model.domain;

import java.io.Serializable;

/**
 * (SubUserProject)实体类
 *
 * @author qingyang
 * @since 2020-12-09 21:23:07
 */
public class SubUserProject implements Serializable {
    private static final long serialVersionUID = 133628657690892064L;

    private String userId;
    /**
     * 如果该字段为All 则表示关联所有的项目
     */
    private String projectId;

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
