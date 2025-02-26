package com.hu.oneclick.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigInteger;
import java.sql.Date;

@Data
@TableName("uitest_git_settings")
public class UITestGitSettings {
    @TableId(type = IdType.AUTO)
    private BigInteger id;

    @TableField("room_id")
    private BigInteger roomId;

    @TableField("username")
    private String username;

    @TableField("passwd")
    private String passwd;

    @TableField("remote_url")
    private String remoteUrl;

    @TableField("created_at")
    private Date createdAt;
}
