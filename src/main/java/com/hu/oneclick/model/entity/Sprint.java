package com.hu.oneclick.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hu.oneclick.model.base.AssignBaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 迭代(Sprint)实体类
 *
 * @author makejava
 * @since 2021-02-03 09:36:08
 */
@Data
public class Sprint extends AssignBaseEntity implements Serializable {
    private static final long serialVersionUID = -33132559253115264L;

    /**
     * 关联项目id
     */
    @ApiModelProperty("关联项目id")
    private Long projectId;
    /**
     * 名称
     */
    @ApiModelProperty("名称")
    private String title;

    /**
     * 开始时间
     */
    @ApiModelProperty("开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startDate;
    /**
     * 结束时间
     */
    @ApiModelProperty("结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endDate;
    /**
     * 描述
     */
    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("记录")
    private String epic;

    @ApiModelProperty("模块")
    private String module;

    @ApiModelProperty("sprintGoal")
    private String sprintGoal;

    @ApiModelProperty("状态")
    private String sprintStatus;

    @ApiModelProperty("扩展数据")
    private String sprintExpand;

}
