package com.hu.oneclick.model.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 操作权限(SysOperationAuthority)实体类
 *
 * @author makejava
 * @since 2021-01-11 10:07:54
 */
public class SysOperationAuthority implements Serializable {
    private static final long serialVersionUID = -18157859770872896L;

    private String id;
    /**
     * 标识名
     */
    private String markName;
    /**
     * 标识中文描述
     */
    private String markNameDesc;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 父id
     */
    private String parentId;

    private Date createTime;

    private Date updateTime;

    /**
     * 是否选中
     */
    private String isSelect = "0";

    private List<SysOperationAuthority> childList;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMarkName() {
        return markName;
    }

    public void setMarkName(String markName) {
        this.markName = markName;
    }

    public String getMarkNameDesc() {
        return markNameDesc;
    }

    public void setMarkNameDesc(String markNameDesc) {
        this.markNameDesc = markNameDesc;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
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

    public List<SysOperationAuthority> getChildList() {
        return childList;
    }

    public void setChildList(List<SysOperationAuthority> childList) {
        this.childList = childList;
    }

    public String getIsSelect() {
        return isSelect;
    }

    public void setIsSelect(String isSelect) {
        this.isSelect = isSelect;
    }
}
