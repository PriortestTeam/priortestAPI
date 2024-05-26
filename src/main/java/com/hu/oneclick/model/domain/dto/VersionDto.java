package com.hu.oneclick.model.domain.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@ApiModel("版本Dto")
@Data
public class VersionDto implements Serializable {
    /**
     * 项目id
     */
    private Long projectId;

    /**
     *发布日期
     */
    private Date releaseDate;

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
    private Long id;
}
