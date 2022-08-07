package com.hu.oneclick.model.domain;

import com.hu.oneclick.model.base.BaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * (SysUser)实体类
 *
 * @author makejava
 * @since 2020-11-14 23:32:42
 */
@Data
public class SysUser extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -67242942971294342L;

    /**
     * 邮箱
     */
    private String email;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 密码
     */
    private String password;
    /**
     * 用户头像地址
     */
    private String photo;
    /**
     * 用户状态 1 启用 3 停止
     */
    private Integer status;
    /**
     * 用户注册时间
     */
    private Date registerDate;
    /**
     * 联系方式
     */
    private String contactNo;
    /**
     * 用户所在企业
     */
    private String company;
    /**
     * 职业
     */
    private String profession;
    /**
     * 行业
     */
    private String industry;
    /**
     * （最近一次关闭账户日期，暂不使用）
     */
    private Date closeDate;
    /**
     * 激活状态， 1 试用中 3 试用过期 2 激活成功 4 激活失败 5 待激活
     */
    private Integer activeState;
    /**
     * 是否启用用户 1启用 3 停用 默认1
     */
    private Integer enable;
    /**
     * 用户注册的时间区域，默认中国，前期中国
     */
    private String timeZone;
    /**
     * 用户注册国家，默认中国，前期中国
     */
    private String locale;
    /**
     * 是否是管理员， 默认为 0, 1 是管理员（负责后台系统的登录）3 子成员
     */
    private Integer manager;
    /**
     * 激活时间
     */
    private Date activitiDate;
    /**
     * 激活次数
     */
    private Integer activitiNumber;
    /**
     * 过期时间
     */
    private Date expireDate;
    /**
     * 0 存在，1 删除
     */
    private Integer delFlag;

    private Date createTime;

    private Date updateTime;

    private Integer sysRoleId;

    /**
     * 用户是否有打开的项目，默认 0 没有
     */
    private int isUseProject = 0;

    private UserUseOpenProject userUseOpenProject;

    /**
     * VIP,Classic,Trialer 用户身份类型
     */
    private String userClass;
    private Long roomId;
}
