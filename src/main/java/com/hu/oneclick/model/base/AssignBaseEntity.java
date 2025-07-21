package com.hu.oneclick.model.base;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.io.Serializable;
import java.util.Date;

@Data
public class AssignBaseEntity implements Serializable {

    private static final long serialVersionUID = -1025285783773774055L;

    //解决swagger获取id精度缺失问题,postman不会有这个问题
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(description = "主键id")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建人ID")
    private Long createUserId;
    //
    //@TableField(fill = FieldFill.INSERT)
    //@Schema(description = "创建人名")
    //private String createUserName;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(description = "更新人ID")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUserId;
    //
    //@Schema(description = "更新人名")
    //@TableField(fill = FieldFill.INSERT_UPDATE)
    //private String updateUserName;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    //@TableLogic
    //@TableField(value = "is_delete", fill = FieldFill.INSERT)
    //@Schema(description = "逻辑删除")
    //private Integer isDelete;

}