package com.hu.oneclick.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * custom_field_data
 * @author 
 */
@Schemavalue="com.hu.oneclick.model.entity.CustomFieldData"
@Data
public class CustomFieldData implements Serializable {

    private Integer id;

    /**
     * 用户id
     */
    @Schemavalue="用户id"
    private String userId;

    /**
     * 项目id
     */
    @Schemavalue="项目id"
    private String projectId;

    /**
     * 自定义字段id
     */
    @Schemavalue="自定义字段id"
    private String customFieldId;

    /**
     * scope对应值的id
     */
    @Schemavalue="scope对应值的id"
    private String scopeId;

    /**
     * 范围
     */
    @Schemavalue="范围"
    private String scope;

    /**
     * 字段名
     */
    @Schemavalue="字段名"
    private String fieldName;

    /**
     * 自定义存储字段的值
     */
    @Schemavalue="自定义存储字段的值"
    private String valueData;

    /**
     * 创建时间
     */
    @Schemavalue="创建时间"
    private Date createTime;

    /**
     * 是否删除
     */
    @Schemavalue="是否删除"
    private Boolean isDel;

    /**
     * 创建用户id
     */
    @Schemavalue="创建用户id"
    private String createUserId;

    private static final long serialVersionUID = 1L;
}
