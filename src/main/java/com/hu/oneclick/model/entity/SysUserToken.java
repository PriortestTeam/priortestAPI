package com.hu.oneclick.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * sys_user_token
 * @author 
 */
@Schema(description = "用户Token实体")
@Data
public class SysUserToken implements Serializable {
    private Integer id;

    /**
     * 用户id
     */
    @Schema(description = "用户id")
    private String userId;

    /**
     * token名称
     */
    @Schema(description = "token名称")
    private String tokenName;

    /**
     * token值
     */
    @Schema(description = "token值")
    private String tokenValue;

    /**
     * 过期时间
     */
    @Schema(description = "过期时间")
    private Date expirationTime;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     * 是否删除
     */
    @Schema(description = "是否删除")
    private Boolean isDel;

    /**
     * 状态
     */
    @Schema(description = "状态")
    private Boolean status;

    /**
     * 剩余调用api次数
     */
    @Schema(description = "剩余调用api次数")
    private Long apiTimes;

    /**
     * 创建人
     */
    @Schemavalue="创建人"
    private String createId;

    private static final long serialVersionUID = 1L;
}
