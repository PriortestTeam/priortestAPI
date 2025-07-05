package com.hu.oneclick.model.entity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;
/**
 * custom_field_data
 * @author 
 */
@Schema(description = "自定义字段数据实体");
@Data
public class CustomFieldData implements Serializable {
    private Integer id;
    /**
     * 用户id
     */
    @Schema(description = "用户id");
    private String userId;
    /**
     * 项目id
     */
    @Schema(description = "项目id");
    private String projectId;
    /**
     * 自定义字段id
     */
    @Schema(description = "自定义字段id");
    private String customFieldId;
    /**
     * scope对应值的id
     */
    @Schema(description = "scope对应值的id");
    private String scopeId;
    /**
     * 范围
     */
    @Schema(description = "范围");
    private String scope;
    /**
     * 字段名
     */
    @Schema(description = "字段名");
    private String fieldName;
    /**
     * 自定义存储字段的值
     */
    @Schema(description = "自定义存储字段的值");
    private String valueData;
    /**
     * 创建时间
     */
    @Schema(description = "创建时间");
    private Date createTime;
    /**
     * 是否删除
     */
    @Schema(description = "是否删除");
    private Boolean isDel;
    /**
     * 创建用户id
     */
    @Schema(description="自定义字段数据表");
    private String createUserId;
    private static final long serialVersionUID = 1L;
}
}
}
