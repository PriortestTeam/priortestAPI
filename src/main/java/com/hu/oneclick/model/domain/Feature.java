package com.hu.oneclick.model.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 故事(Feature)实体类
 *
 * @author makejava
 * @since 2021-02-03 13:54:35
 */
public class Feature implements Serializable {
    private static final long serialVersionUID = 495256750642592776L;

    /**
     * 关联项目id
     */
    private String projectId;
    /**
     * 关联迭代id
     */
    private String sprintId;
    /**
     * 记录
     */
    private String epic;
    /**
     * 指派给谁
     */
    private String reportTo;
    /**
     * 状态，（1 progress ，0 closed, 2 plan）
     */
    private Integer status;
    /**
     * 版本
     */
    private String version;
    /**
     * 描述
     */
    private String description;
    /**
     * 关闭时间
     */
    private Date closeDate;
    /**
     * 管理人
     */
    private String authorName;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 名称
     */
    private String title;
    /**
     * 关联用户id
     */
    private String userId;


    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getSprintId() {
        return sprintId;
    }

    public void setSprintId(String sprintId) {
        this.sprintId = sprintId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEpic() {
        return epic;
    }

    public void setEpic(String epic) {
        this.epic = epic;
    }

    public String getReportTo() {
        return reportTo;
    }

    public void setReportTo(String reportTo) {
        this.reportTo = reportTo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(Date closeDate) {
        this.closeDate = closeDate;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
