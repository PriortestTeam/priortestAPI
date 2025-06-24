package com.hu.oneclick.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * sys_user_reference
 * @author 
 */
@Schemavalue="com.hu.oneclick.model.domain.SysUserReference用户推荐人"
@Data
public class SysUserReference implements Serializable {
    /**
     * id
     */
    @Schemavalue="id"
    private Integer id;

    /**
     * 用户id
     */
    @Schemavalue="用户id"
    private Long user_id;

    /**
     * 推荐人id
     */
    @Schemavalue="推荐人id"
    private Long reference_user_id;

    /**
     * 推荐人邮箱
     */
    @Schemavalue="推荐人邮箱"
    private String reference_user_email;

    /**
     * 推荐时间
     */
    @Schemavalue="推荐时间"
    private Date reference_time;

    /**
     * 逻辑删除
     */
    @Schemavalue="逻辑删除"
    private Boolean is_del;

    private static final long serialVersionUID = 1L;
}
