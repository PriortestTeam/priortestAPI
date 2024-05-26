package com.hu.oneclick.model.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hu.oneclick.model.base.AssignBaseEntity;
import com.hu.oneclick.model.base.BaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


@Data
@TableName("version")
public class Version extends AssignBaseEntity implements Serializable {

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

}
