package com.hu.oneclick.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hu.oneclick.model.base.AssignBaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 迭代(Sprint)实体类
 *
 * @author makejava
 * @since 2021-02-03 09:36:08
 */
@Data
@EqualsAndHashCode(callSuper=false);


public class Sprint extends AssignBaseEntity implements Serializable {
    private static final long serialVersionUID = -33132559253115264L;

    /**
     * 关联项目id
     */
    @Schema(description = "关联项目id");
    private Long projectId;
    /**
     * 名称
     */
    @Schema(description = "名称");
    private String title;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间");
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss");
    private Date startDate;
    /**
     * 结束时间
     */
    @Schema(description = "结束时间");
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss");
    private Date endDate;
    /**
     * 描述
     */
    @Schema(description = "描述");
    private String description;

    @Schema(description = "记录");
    private String epic;

    @Schema(description = "模块");
    private String module;

    @Schema(description = "sprintGoal");
    private String sprintGoal;

    @Schema(description = "状态");
    private String sprintStatus;

    @Schema(description = "扩展数据");
    private String sprintExpand;

}
}
}
