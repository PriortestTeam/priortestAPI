package com.hu.oneclick.model.domain;

import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.model.base.BaseEntity;
import com.hu.oneclick.model.base.VerifyParam;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 修改记录表(ModifyRecord)实体类
 *
 * @author makejava
 * @since 2021-02-14 14:14:43
 */
public class ModifyRecord extends BaseEntity implements Serializable, VerifyParam {
    private static final long serialVersionUID = 810875069044226355L;

    /**
     * 关联用户id
     */
    private String userId;
    /**
     * 修改人
     */
    private String modifyUser;
    /**
     * 修改前的值
     */
    private String beforeVal;
    /**
     * 修改后的值
     */
    private String afterVal;
    /**
     * 修改时间
     */
    private Date modifyDate;

    private String scope;

    private String projectId;

    private String linkId;

    private String modifyField;

    @Override
    public void verify() throws BizException {
        if(StringUtils.isEmpty(projectId)){
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(),"项目ID" + SysConstantEnum.PARAM_EMPTY.getValue());
        } else if(StringUtils.isEmpty(scope)){
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(),"SCOPE" + SysConstantEnum.PARAM_EMPTY.getValue());
        }
    }

    public String getModifyField() {
        return modifyField;
    }

    public void setModifyField(String modifyField) {
        this.modifyField = modifyField;
    }

    public String getLinkId() {
        return linkId;
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(String modifyUser) {
        this.modifyUser = modifyUser;
    }

    public String getBeforeVal() {
        return beforeVal;
    }

    public void setBeforeVal(String beforeVal) {
        this.beforeVal = beforeVal;
    }

    public String getAfterVal() {
        return afterVal;
    }

    public void setAfterVal(String afterVal) {
        this.afterVal = afterVal;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }



}