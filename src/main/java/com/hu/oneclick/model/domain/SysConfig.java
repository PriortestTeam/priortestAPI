package com.hu.oneclick.model.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName SysConfig.java
 * @Description
 * @Author Vince
 * @CreateTime 2022年12月24日 18:21:00
 */
@Data
public class SysConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * 范围名称
     */
    private String scopeName;

    /**
     * sort
     */
    private Integer sort;

    /**
     * 中文名称
     */
    private String nameCn;

    /**
     * group
     */
    private String group;


}
