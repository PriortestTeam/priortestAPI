package com.hu.oneclick.model.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * sys_user_token
 * @author 
 */
@ApiModel(value="com.hu.oneclick.model.domain.SysUserToken")
@Data
public class SysUserToken implements Serializable {
    private Integer id;

    /**
     * 用户id
     */
    @ApiModelProperty(value="用户id")
    private String user_id;

    /**
     * token名称
     */
    @ApiModelProperty(value="token名称")
    private String token_name;

    /**
     * token值
     */
    @ApiModelProperty(value="token值")
    private String token_value;

    /**
     * 过期时间
     */
    @ApiModelProperty(value="过期时间")
    private Date expiration_time;

    /**
     * 创建时间
     */
    @ApiModelProperty(value="创建时间")
    private Date create_time;

    /**
     * 是否删除
     */
    @ApiModelProperty(value="是否删除")
    private Boolean is_del;

    /**
     * 状态
     */
    @ApiModelProperty(value="状态")
    private Boolean status;

    /**
     * 剩余调用api次数
     */
    @ApiModelProperty(value="剩余调用api次数")
    private Long api_times;

    /**
     * 创建人
     */
    @ApiModelProperty(value="创建人")
    private String create_id;

    private static final long serialVersionUID = 1L;
}