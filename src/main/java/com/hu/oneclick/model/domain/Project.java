package com.hu.oneclick.model.domain;

import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.model.base.BaseEntity;
import com.hu.oneclick.model.base.VerifyParam;
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
public class Project extends BaseEntity implements VerifyParam , Serializable {
    private static final long serialVersionUID = -54866876049537399L;

    /**
     * 关联用户id
     */
    private String userId;
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
    private Integer status;
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

    private String scope = OneConstant.SCOPE.ONE_PROJECT;


    private Date closeDateBegin;
    private Date closeDateEnd;

    private Date planReleaseDateBegin;
    private Date planReleaseDateEnd;


    /**
     * 项目绑定的权限
     */
    private String operationAuthIds;

    List<SysOperationAuthority> sysOperationAuthorities;

    @Override
    public void verify() throws BizException {
        if(StringUtils.isEmpty(title)){
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(),"项目名称" + SysConstantEnum.PARAM_EMPTY.getValue());
        }
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReportToName() {
        return reportToName;
    }

    public void setReportToName(String reportToName) {
        this.reportToName = reportToName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getPlanReleaseDate() {
        return planReleaseDate;
    }

    public void setPlanReleaseDate(Date planReleaseDate) {
        this.planReleaseDate = planReleaseDate;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
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

    public Date getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(Date closeDate) {
        this.closeDate = closeDate;
    }

    public String getOperationAuthIds() {
        return operationAuthIds;
    }

    public void setOperationAuthIds(String operationAuthIds) {
        this.operationAuthIds = operationAuthIds;
    }

    public List<SysOperationAuthority> getSysOperationAuthorities() {
        return sysOperationAuthorities;
    }

    public void setSysOperationAuthorities(List<SysOperationAuthority> sysOperationAuthorities) {
        this.sysOperationAuthorities = sysOperationAuthorities;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Date getCloseDateBegin() {
        return closeDateBegin;
    }

    public void setCloseDateBegin(Date closeDateBegin) {
        this.closeDateBegin = closeDateBegin;
    }

    public Date getCloseDateEnd() {
        return closeDateEnd;
    }

    public void setCloseDateEnd(Date closeDateEnd) {
        this.closeDateEnd = closeDateEnd;
    }

    public Date getPlanReleaseDateBegin() {
        return planReleaseDateBegin;
    }

    public void setPlanReleaseDateBegin(Date planReleaseDateBegin) {
        this.planReleaseDateBegin = planReleaseDateBegin;
    }

    public Date getPlanReleaseDateEnd() {
        return planReleaseDateEnd;
    }

    public void setPlanReleaseDateEnd(Date planReleaseDateEnd) {
        this.planReleaseDateEnd = planReleaseDateEnd;
    }

    public String getTestFrame() {
        return testFrame;
    }

    public void setTestFrame(String testFrame) {
        this.testFrame = testFrame;
    }

    public String getProjectCategory() {
        return projectCategory;
    }

    public void setProjectCategory(String projectCategory) {
        this.projectCategory = projectCategory;
    }

    public String getCloseDesc() {
        return closeDesc;
    }

    public void setCloseDesc(String closeDesc) {
        this.closeDesc = closeDesc;
    }
}
