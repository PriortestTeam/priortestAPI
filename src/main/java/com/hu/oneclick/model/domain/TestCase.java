package com.hu.oneclick.model.domain;

import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.model.base.BaseEntity;
import com.hu.oneclick.model.base.VerifyParam;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 测试用例(TestCase)实体类
 *
 * @author makejava
 * @since 2021-02-04 13:51:21
 */
public class TestCase extends BaseEntity implements Serializable, VerifyParam {
    private static final long serialVersionUID = 114802398790239711L;

    /**
     * 关联项目id
     */
    private String projectId;
    /**
     * 名称
     */
    private String title;
    /**
     * 优先权
     */
    private String priority;
    /**
     * 特征
     */
    private String feature;
    /**
     * 状态，（0 no run, 2 pass , 3 failed）
     */
    private Integer status;
    /**
     * 描述
     */
    private String description;
    /**
     * 执行时间
     */
    private Date executedDate;
    /**
     * 管理人
     */
    private String authorName;
    /**
     * 创建时间
     */
    private Date createTime;

    private Date updateTime;
    /**
     * 关联用户id
     */
    private String userId;


    @Override
    public void verify() throws BizException {
        if(StringUtils.isEmpty(projectId)){
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(),"项目ID" + SysConstantEnum.PARAM_EMPTY.getValue());
        }else if(StringUtils.isEmpty(title)){
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(),"迭代名称" + SysConstantEnum.PARAM_EMPTY.getValue());
        }
    }

    public void queryListVerify() {
        if(StringUtils.isEmpty(projectId)){
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(),"项目ID" + SysConstantEnum.PARAM_EMPTY.getValue());
        }
        super.setId(null);
    }


    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
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

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
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

    public Date getExecutedDate() {
        return executedDate;
    }

    public void setExecutedDate(Date executedDate) {
        this.executedDate = executedDate;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
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


}
