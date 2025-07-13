
package com.hu.oneclick.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hu.oneclick.model.base.AssignBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@TableName("version_mapping")
@EqualsAndHashCode(callSuper=false)
public class VersionMapping extends AssignBaseEntity implements Serializable {

    /**
     * 项目id
     */
    private Long projectId;

    /**
     * 发布版本ID (关联 release_management 表)
     */
    private Long releaseId;

    /**
     * 发布版本号 (PROD版本)
     */
    private String releaseVersion;

    /**
     * 环境：dev/stg
     */
    private String env;

    /**
     * 环境对应的版本号
     */
    private String envVersion;

    /**
     * 备注
     */
    private String remark;
}
