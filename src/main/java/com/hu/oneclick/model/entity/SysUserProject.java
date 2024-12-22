package com.hu.oneclick.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
@TableName("sys_user_project")
public class SysUserProject {
    @TableId(type = IdType.AUTO)
    private BigInteger id;

    @TableField("user_id")
    private BigInteger userId;

    @TableField("project_id")
    private BigInteger projectId;

    @TableField("is_default")
    private Integer isDefault;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
