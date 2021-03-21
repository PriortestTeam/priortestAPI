package com.hu.oneclick.model.domain;

import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.model.base.BaseEntity;
import com.hu.oneclick.model.base.VerifyParam;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 测试周期(TestCycle)实体类
 *
 * @author makejava
 * @since 2021-02-16 15:39:36
 */
public class TestCycle extends BaseEntity implements Serializable, VerifyParam {
    private static final long serialVersionUID = -99559444962908046L;

    /**
     * 用户id
     */
    private String userId;
    /**
     * 项目id
     */
    private String projectId;
    /**
     * 名称
     */
    private String title;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 运行状态
     */
    private Integer runStatus;
    /**
     * 最后一次运行时间
     */
    private Date lastRunDate;
    /**
     * 最后修改时间
     */
    private Date lastModify;

    /**
     * 版本
     */
    private String version;

    private Date createTime;

    private Date updateTime;

    private Date ped;
    /**
     * 创建人
     */
    private String authorName;

    private String description;

    private String assignTo;
    private String notifiyList;


    @Transient
    private String scope = OneConstant.SCOPE.ONE_TEST_CYCLE;


    @Transient
    private List<Feature> features;

    @Transient
    private List<Sprint> sprints;

    @Override
    public void verify() throws BizException {
        if(StringUtils.isEmpty(projectId)){
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(),"项目ID" + SysConstantEnum.PARAM_EMPTY.getValue());
        }else if(StringUtils.isEmpty(title)){
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(),"测试周期名称" + SysConstantEnum.PARAM_EMPTY.getValue());
        }
    }

    public void queryListVerify() {
        if(StringUtils.isEmpty(projectId)){
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(),"项目ID" + SysConstantEnum.PARAM_EMPTY.getValue());
        }
        super.setId(null);
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getRunStatus() {
        return runStatus;
    }

    public void setRunStatus(Integer runStatus) {
        this.runStatus = runStatus;
    }

    public Date getLastRunDate() {
        return lastRunDate;
    }

    public void setLastRunDate(Date lastRunDate) {
        this.lastRunDate = lastRunDate;
    }

    public Date getLastModify() {
        return lastModify;
    }

    public void setLastModify(Date lastModify) {
        this.lastModify = lastModify;
    }
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
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

    public Date getPed() {
        return ped;
    }

    public void setPed(Date ped) {
        this.ped = ped;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAssignTo() {
        return assignTo;
    }

    public void setAssignTo(String assignTo) {
        this.assignTo = assignTo;
    }

    public String getNotifiyList() {
        return notifiyList;
    }

    public void setNotifiyList(String notifiyList) {
        this.notifiyList = notifiyList;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(List<Feature> features) {
        this.features = features;
    }

    public List<Sprint> getSprints() {
        return sprints;
    }

    public void setSprints(List<Sprint> sprints) {
        this.sprints = sprints;
    }
}
