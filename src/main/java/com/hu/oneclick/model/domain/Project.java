package com.hu.oneclick.model.domain;

import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.model.base.BaseEntity;
import com.hu.oneclick.model.base.VerifyParam;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * (Project)实体类
 *
 * @author qingyang
 * @since 2020-12-07 20:59:50
 */
@Data
public class Project extends BaseEntity implements VerifyParam, Serializable {
    private static final long serialVersionUID = -54866876049537399L;

    /**
     * 关联用户id
     */
    private String userId;

    private Long roomId;

    /**
     * 项目名称
     */
    private String title;
    /**
     * 项目负责人
     */
    private String reportToName;
    /**
     * 项目状态: 默认 Progress，1 Closed 关闭、2 Plan 计划、3 Progress 开发中
     */
    private String status;
    /**
     * 描述
     */
    private String description;
    /**
     * 计划上线日期
     */
    private Date planReleaseDate;
    /**
     * 删除标记 0 默认， 1 删除
     */
    private Integer delFlag;

    private String testFrame;
    private String projectCategory;

    private Date createTime;

    private Date updateTime;

    private Date closeDate;

    private String closeDesc;

    private String customer;

    private String scope = OneConstant.SCOPE.ONE_PROJECT;


    private Date closeDateBegin;
    private Date closeDateEnd;

    private Date planReleaseDateBegin;
    private Date planReleaseDateEnd;

    private String foreignId;

    /**
     * 项目绑定的权限
     */
    private String operationAuthIds;

    List<SysOperationAuthority> sysOperationAuthorities;

    /**
     * 自定义字段值
     */
    private List<CustomFieldData> customFieldDatas;

    @Override
    public void verify() throws BizException {
        if (StringUtils.isEmpty(title)) {
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(), "项目名称" + SysConstantEnum.PARAM_EMPTY.getValue());
        }
    }



}
