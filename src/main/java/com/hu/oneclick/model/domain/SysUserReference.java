package com.hu.oneclick.model.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * sys_user_reference
 * @author 
 */
@ApiModel(value="com.hu.oneclick.model.domain.SysUserReference用户推荐人")
@Data
public class SysUserReference implements Serializable {
    /**
     * id
     */
    @ApiModelProperty(value="id")
    private Integer id;

    /**
     * 用户id
     */
    @ApiModelProperty(value="用户id")
    private Long user_id;

    /**
     * 推荐人id
     */
    @ApiModelProperty(value="推荐人id")
    private Long reference_user_id;

    /**
     * 推荐人邮箱
     */
    @ApiModelProperty(value="推荐人邮箱")
    private String reference_user_email;

    /**
     * 推荐时间
     */
    @ApiModelProperty(value="推荐时间")
    private Date reference_time;

    /**
     * 逻辑删除
     */
    @ApiModelProperty(value="逻辑删除")
    private Boolean is_del;

    private static final long serialVersionUID = 1L;
}