package com.hu.oneclick.model.domain.dto;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@TableName(value = "use_case")
@EqualsAndHashCode(callSuper=false)
public class UserCaseDto {
    private static final long serialVersionUID = 43132559253115264L;
    
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;
    
    private String title;

    private String useCategory;
    /**
     * 级别
     */
    private String level;
    /**
     * 等级
     */
    private String grade;

    /**
     * 流程场景、
     */
    private String scenario;
    private String usecaseExpand;
    private long featureId;

    private Long projectId;

    private String version;

    private String remarks;

    /**
     * 创建时间 - 自动填充
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;
    
    /**
     * 创建用户ID - 自动填充  
     */
    @TableField(value = "create_user_id", fill = FieldFill.INSERT)
    private Long createUserId;

}