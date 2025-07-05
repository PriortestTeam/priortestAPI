package com.hu.oneclick.model.entity;
import lombok.Data;
import java.io.Serializable;
/**
 * (SubUserProject)实体类
 *
 * @author qingyang
 * @since 2020-12-09 21:23:07
 */
@Data


public class SubUserProject implements Serializable {
    private static final long serialVersionUID = 133628657690892064L;
    private String userId;
    /**
     * 如果该字段为All 则表示关联所有的项目
     */
    private String projectId;
    /** 默认打开项目号
     * @Param: 
     * @return: 
     * @Author: MaSiyi
     * @Date: 2021/12/25
     */
    private String openProjectByDefaultId;
}
}
}
