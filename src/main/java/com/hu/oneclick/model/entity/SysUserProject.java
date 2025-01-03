package com.hu.oneclick.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigInteger;
import java.sql.Date;

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

    @TableField(value = "created_at")
    private Date createdAt;
}
