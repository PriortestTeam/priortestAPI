package com.hu.oneclick.model.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class VersionRequestDto {

    /**
     * 项目id
     */
    @NotNull(message = "projectId不能为空")
    private Long projectId;

    /**
     *发布日期
     */
    @NotNull(message = "发布日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date releaseDate;

    @NotNull(message = "版本不能为空")
    private String version;

    /**
     * 状态
     */
    private String status;

    /**
     * 描述
     */
    private String description;

    /**
     * 版本id
     */
    @NotNull(message = "修改ID不能为空")
    private Long id;
}
