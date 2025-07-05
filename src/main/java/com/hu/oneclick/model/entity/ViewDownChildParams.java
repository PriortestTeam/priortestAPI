package com.hu.oneclick.model.entity;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;
/**
 * (ViewDownChildParams)实体类
 *
 * @author makejava
 * @since 2021-04-16 16:25:20
 */
@Data


public class ViewDownChildParams implements Serializable {
    private static final long serialVersionUID = 848798611045855488L;
    private String id;
    /**
     * 作用域
     */
    private String scope;
    /**
     * 下拉框数据
     */
    private String defaultValues;
    private Date createTime;
    private Date updateTime;
    private String projectId;
    private String userId;
    private Boolean delFlag;
}
}
}
