
package com.hu.oneclick.model.domain.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class CustomFieldsDto {

    /**
     * fieldType 为集合中的任何一项，则 possibleValue 中不能包含 parentListId 值
     */
    public static final List<String> NOT_PARENT_LIST_ID = new ArrayList<String>() {{
        this.add("multiList");
        this.add("dropDown");
        this.add("userList");
        this.add("number");
    }};

    /**
     * fieldType 为集合中的任何一项，则 possibleValue 中必须包含 parentListId 值
     */
    public static final List<String> NEED_PARENT_LIST_ID = new ArrayList<String>() {{
        this.add("linkedDropDown");
    }};

    private Long customFieldId;

    private String possibleValue;

    private String fieldType;

    private Date updateTime;

    private Long modifyUserId;

    private String projectId;

    private String type;

    // 手动添加缺失的getter/setter方法
    public Long getCustomFieldId() {
        return customFieldId;
    }

    public void setCustomFieldId(Long customFieldId) {
        this.customFieldId = customFieldId;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getPossibleValue() {
        return possibleValue;
    }

    public void setPossibleValue(String possibleValue) {
        this.possibleValue = possibleValue;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getModifyUserId() {
        return modifyUserId;
    }

    public void setModifyUserId(Long modifyUserId) {
        this.modifyUserId = modifyUserId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
