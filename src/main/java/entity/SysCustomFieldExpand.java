package entity;

import java.io.Serializable;
import java.util.Date;

/**
 * (SysCustomFieldExpand)实体类
 *
 * @author makejava
 * @since 2021-04-01 10:57:39
 */
public class SysCustomFieldExpand implements Serializable {
    private static final long serialVersionUID = -20297181306272036L;

    private Long id;

    private Long userId;

    private Long projectId;

    private Long linkSysCustomField;

    private String values;

    private Date createTime;

    private Date updateTime;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getLinkSysCustomField() {
        return linkSysCustomField;
    }

    public void setLinkSysCustomField(Long linkSysCustomField) {
        this.linkSysCustomField = linkSysCustomField;
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
