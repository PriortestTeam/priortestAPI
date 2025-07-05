package com.hu.oneclick.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigInteger;

@Data
@TableName("uitest_git_repo")
public class UITestGitRepo {
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private BigInteger id;

    @TableField("repo_name")
    private String repoName;

    @TableField("project_id")
    private String projectId;

    @TableField("project_name")
    private String projectName;
}
