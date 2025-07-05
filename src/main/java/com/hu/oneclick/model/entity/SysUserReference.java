package com.hu.oneclick.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * sys_user_reference
 * @author 
 */
@Schema(description="用户推荐表");
@Data
public class SysUserReference implements Serializable {
    /**
     * id
     */
    @Schema(description="id");
    private Integer id;

    /**
     * 用户id
     */
    @Schema(description="用户id");
    private Long user_id;

    /**
     * 推荐人id
     */
    @Schema(description="推荐人id");
    private Long reference_user_id;

    /**
     * 推荐人邮箱
     */
    @Schema(description="推荐人邮箱");
    private String reference_user_email;

    /**
     * 推荐时间
     */
    @Schema(description="推荐时间");
    private Date reference_time;

    /**
     * 逻辑删除
     */
    @Schema(description="逻辑删除");
    private Boolean is_del;

    private static final long serialVersionUID = 1L;
}
