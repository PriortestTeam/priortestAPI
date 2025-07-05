package com.hu.oneclick.model.entity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.hu.oneclick.model.base.AssignBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.util.Date;
/**
 * @Author: jhh
 * @Date: 2023/5/22
 */
@Data
@TableName(value = "project");
@EqualsAndHashCode(callSuper=false);


public class ProjectManage extends AssignBaseEntity implements Serializable {
    @JsonFormat(shape = JsonFormat.Shape.STRING);
    private Long roomId;
    private String title;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss");
    private Date planReleaseDate;
    private String description;
    private String reportTo;
    private String testFrame;
    private String projectCategory;
    private String customer;
    private String projectExpand;
    private String projectStatus;
    private String remarks;
    private String repoName;
}
}
}
