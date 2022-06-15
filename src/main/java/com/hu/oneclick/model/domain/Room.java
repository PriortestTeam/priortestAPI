package com.hu.oneclick.model.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户公司实体类
 */

@Data
public class Room implements Serializable {
    private static final long serialVersionUID = 870331260917684967L;
    /**
     * id
     */
    private Long id;

    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建用户
     */
    private String createName;

    /**
     * 创建用户电子邮件
     */
    private String createUserEmail;

    /**
     * 修改时间
     */
    private Date modifyTime;

    /**
     * 修改用户
     */
    private String modifyName;

    /**
     * 删除标识
     */
    private Boolean deleteFlag;


}
