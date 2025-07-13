package com.hu.oneclick.model.domain.dto;

import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;


/**
 * @Author: jhh
 * @Date: 2023/5/16
 */
@Data
public class SprintSaveDto {


    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(description = "主键id")
    private Long id;

    /**
     * 关联项目id
     */
    @Schema(description = "关联项目id")
    @NotNull(message = "项目ID不能为空")
    private Long projectId;
    /**
     * 名称
     */
    @Schema(description = "名称")
    private String title;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "开始时间不能为空")
    private Date startDate;
    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "结束时间不能为空")
    private Date endDate;
    /**
     * 描述
     */
    @Schema(description = "描述")
    private String description;

    @Schema(description = "记录")
    private String epic;

    @Schema(description = "模块")
    private String module;

    @Schema(description = "sprintGoal")
    private String sprintGoal;

    @Schema(description = "状态")
    private String sprintStatus;

    /**
     * 自定义字段值
     */
    @Schema(description = "自定义字段值")
    private JSONObject customFieldDatas;
}
