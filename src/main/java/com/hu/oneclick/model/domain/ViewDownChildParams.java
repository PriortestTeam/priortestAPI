package com.hu.oneclick.model.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * (ViewDownChildParams)实体类
 *
 * @author makejava
 * @since 2021-04-16 16:25:20
 */
public class ViewDownChildParams implements Serializable {
    private static final long serialVersionUID = 848798611045855488L;

    private String id;
    /**
     * 作用域
     */
    private String scope;
    /**
     * 下拉框数据
     */
    private String defaultValues;

    private Date createTime;

    private Date updateTime;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getDefaultValues() {
        return defaultValues;
    }

    public void setDefaultValues(String defaultValues) {
        this.defaultValues = defaultValues;
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
