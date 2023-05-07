package com.hu.oneclick.model.domain;

import com.hu.oneclick.model.base.AssignBaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 故事(Feature)实体类
 *
 * @author makejava
 * @since 2021-02-03 13:54:35
 */
@Data
public class Feature extends AssignBaseEntity implements Serializable {
    private static final long serialVersionUID = 495256750642592776L;

    /**
     * 名称
     */
    @ApiModelProperty("名称")
    private String title;

    /**
     * 记录
     */
    @ApiModelProperty("记录")
    private String epic;
    /**
     * 关联项目id
     */
    @ApiModelProperty("关联项目id")
    private Long projectId;
    /**
     * 指派给谁
     */
    @ApiModelProperty("指派给谁")
    private String reportTo;
    /**
     * 状态
     */
    @ApiModelProperty("状态")
    private String featureStatus;
    /**
     * 版本
     */
    @ApiModelProperty("版本")
    private String version;
    /**
     * 描述
     */
    @ApiModelProperty("描述")
    private String description;
    /**
     * 关闭时间
     */
    @ApiModelProperty("关闭时间")
    private Date closeDate;

    @ApiModelProperty("模块")
    private String moudle;

    @ApiModelProperty("上报人姓名")
    private String reportName;

    @ApiModelProperty("备注")
    private String remarks;

    @ApiModelProperty("扩展数据")
    private String featureExpend;
}
