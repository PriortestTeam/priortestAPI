package com.hu.oneclick.model.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName CustomFields.java
 * @Description
 * @Author Vince
 * @CreateTime 2022年12月13日 17:27:00
 */
@Data
public class CustomFields implements Serializable {
    private static final long serialVersionUID = 870331260917684968L;
    /**
     * 主键
     */
    private Long customFieldId;
    /**
     * 用户id， 创建字段者
     */
    private Long createUser;

    /**
     * 自定义字段类型
     */
    private String type;

    /**
     * 创建日期时间
     */
    private Date createTime;

    /**
     * 更新日期时间
     */
    private Date updateTime;

    /**
     * 字段名称
     */
    private String fieldNameCn;

    /**
     * 关联项目id
     */
    private Long projectId;

    /**
     * 用户id，修改者
     */
    private Long modifyUser;

    /**
     * 用于文本字段
     */
    private Integer length;

    /**
     * 可能的值
     */
    private String possibleValue;

    /**
     * field_type
     */
    private String fieldType;


}
