package com.hu.oneclick.model.entity;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigInteger;
import java.util.Date;
@Data
@TableName("uitest_git_repo");

public class UITestGitRepo {
    @TableId
    private BigInteger id;
    private String repoName;
    private Long projectId;
    private String projectName;
    private BigInteger roomId;
    private Date createTime;
    private Date updateTime;
    private static final long serialVersionUID = 1L;
    // 手动添加setter方法
    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}
}
}
