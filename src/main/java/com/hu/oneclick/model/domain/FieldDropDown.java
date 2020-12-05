package com.hu.oneclick.model.domain;

import java.io.Serializable;
import java.util.List;

/**
 * 下拉菜单(FieldDropDown)实体类
 *
 * @author qingyang
 * @since 2020-12-05 21:49:12
 */
public class FieldDropDown extends CustomField implements Serializable {
    private static final long serialVersionUID = 668370284822611568L;

    private String customFieldId = super.getId();
    /**
     * 默认下拉菜单第一项
     */
    private String defaultValue;
    /**
     * 数组格式，每一个值按照 逗号分隔
     */
    private List<String> dropDownList;
    /**
     * 数组长度
     */
    private Integer length;

    @Override
    public void subVerify() {
        super.verify();

    }



    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getCustomFieldId() {
        return customFieldId;
    }

    public void setCustomFieldId(String customFieldId) {
        this.customFieldId = customFieldId;
    }

    public List<String> getDropDownList() {
        return dropDownList;
    }

    public void setDropDownList(List<String> dropDownList) {
        this.dropDownList = dropDownList;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

}
