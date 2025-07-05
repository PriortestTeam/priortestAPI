package com.hu.oneclick.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * system_config
 * @author
 */
@Schema(description="系统配置表"))
@Data
public class SystemConfig implements Serializable {
    /**
     * id
     */
    @Schema(description="id"))
    private Integer id;

    /**
     * key
     */
    @Schema(description="key"))
    private String key;

    /**
     * value
     */
    @Schema(description="value"))
    private String value;

    /**
     * 组别
     */
    @Schema(description="组别"))
    private String group;

    /**
     * 创建时间
     */
    @Schema(description="创建时间"))
    private Date create_time;

    /**
     * 修改时间
     */
    @Schema(description="修改时间"))
    private Date update_time;

    /**
     * ui显示
     */
    @Schema(description="ui显示0显示1不显示"))
    private Integer uiDisplay;

    /**
     * 分组中文名
     */
    @Schema(description="分组中文名"))
    private String groupLabelCN;



    private static final long serialVersionUID = 1L;
}
