package com.hu.oneclick.model.entity;

import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.model.base.BaseEntity;
import com.hu.oneclick.model.base.VerifyParam;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 自定义字段表(CustomFieldDto)实体类
 *
 * @author qingyang
 * @since 2020-12-04 15:35:47
 */


public class CustomField extends BaseEntity  implements VerifyParam, Serializable {
    private static final long serialVersionUID = 870331260917684967L;
    /**
     * 自定义字段类型
     */
    private String type;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 作用范围，范围以逗号分隔 （1,0,1,1,1）
     */
    private String scope;
    /**
     * 对应 scope， 填写 yes 如scope（1,0,1,1,1），mandatory （1,0,1,1,1）
     */
    private String mandatory;

    private Date createTime;

    private Date updateTime;
    /**
     * 字段名称
     */
    private String fieldName;

    private String projectId;



    @Override
    public void verify() throws BizException {
        if(StringUtils.isEmpty(projectId){
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(),"项目ID" + SysConstantEnum.PARAM_EMPTY.getValue();
        } else if (StringUtils.isEmpty(fieldName){
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(),"字段名称" + SysConstantEnum.PARAM_EMPTY.getValue();
        }else if (StringUtils.isEmpty(scope){
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(),"作用域" + SysConstantEnum.PARAM_EMPTY.getValue();
        }else if (StringUtils.isEmpty(mandatory){
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(),"mandatory" + SysConstantEnum.PARAM_EMPTY.getValue();
        }
    }

    public void subVerify()throws BizException {}


    public void setType(){
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getMandatory() {
        return mandatory;
    }

    public void setMandatory(String mandatory) {
        this.mandatory = mandatory;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
}
}
}
