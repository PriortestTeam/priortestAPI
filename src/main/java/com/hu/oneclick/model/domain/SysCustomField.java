package com.hu.oneclick.model.domain;

import java.io.Serializable;

/**
 * (SysCustomField)实体类
 *
 * @author makejava
 * @since 2021-04-11 14:40:13
 */
public class SysCustomField implements Serializable {
    private static final long serialVersionUID = 680119388243867579L;

    private String id;
    /**
     * 字段名
     */
    private String fieldName;
    /**
     * 中文字段名
     */
    private String fieldNameCn;
    /**
     * 默认值
     */
    private String defaultValues;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldNameCn() {
        return fieldNameCn;
    }

    public void setFieldNameCn(String fieldNameCn) {
        this.fieldNameCn = fieldNameCn;
    }

    public String getDefaultValues() {
        return defaultValues;
    }

    public void setDefaultValues(String defaultValues) {
        this.defaultValues = defaultValues;
    }
}