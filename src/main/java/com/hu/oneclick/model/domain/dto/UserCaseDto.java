package com.hu.oneclick.model.domain.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hu.oneclick.model.base.AssignBaseEntity;
import lombok.Data;
import org.json.JSONObject;

import java.io.Serializable;

@Data
@TableName(value = "use_case")
public class UserCaseDto extends AssignBaseEntity {
    private static final long serialVersionUID = 43132559253115264L;
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

    private String remarks;

}
