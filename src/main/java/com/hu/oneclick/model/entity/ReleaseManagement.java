package com.hu.oneclick.model.entity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hu.oneclick.model.base.AssignBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.util.Date;
@Data
@TableName("release_management");
@EqualsAndHashCode(callSuper=false);

public class ReleaseManagement extends AssignBaseEntity implements Serializable {
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
    private String version;
}
}
}
