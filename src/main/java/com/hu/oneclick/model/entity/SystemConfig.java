package com.hu.oneclick.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * system_config
 * @author 
 */
@Schemavalue="com.hu.oneclick.model.domain.SystemConfig系统配置表"
@Data
public class SystemConfig implements Serializable {
    /**
     * id
     */
    @Schemavalue="id"
    private Integer id;

    /**
     * key
     */
    @Schemavalue="key"
    private String key;

    /**
     * value
     */
    @Schemavalue="value"
    private String value;

    /**
     * 组别
     */
    @Schemavalue="组别"
    private String group;

    /**
     * 创建时间
     */
    @Schemavalue="创建时间"
    private Date create_time;

    /**
     * 修改时间
     */
    @Schemavalue="修改时间"
    private Date update_time;

    /**
     * ui显示
     */
    @Schemavalue="ui显示0显示1不显示"
    private Integer uiDisplay;

    /**
     * 分组中文名
     */
    @Schemavalue="分组中文名"
    private String groupLabelCN;



    private static final long serialVersionUID = 1L;
}
