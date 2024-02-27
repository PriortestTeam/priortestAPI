package com.hu.oneclick.model.domain.dto;

import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @Author: jhh
 * @Date: 2023/5/16
 */
@Data
public class SprintSaveDto {


    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "主键id")
    private Long id;

    /**
     * 关联项目id
     */
    @ApiModelProperty("关联项目id")
    @NotNull(message = "项目ID不能为空")
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
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "开始时间不能为空")
    private Date startDate;
    /**
     * 结束时间
     */
    @ApiModelProperty("结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "结束时间不能为空")
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


    /**
     * 自定义字段值
     */
    @ApiModelProperty("自定义字段值")
    private JSONObject customFieldDatas;
}
