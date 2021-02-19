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

/**
 * 缺陷(Issue)实体类
 *
 * @author makejava
 * @since 2021-02-17 16:20:43
 */
public class Issue extends BaseEntity implements Serializable, VerifyParam {
    private static final long serialVersionUID = 418948698502600149L;
    /**
     * 项目id
     */
    private String projectId;
    /**
     * 用户id
     */
    private String userId;
    /**
     * 名称
     */
    private String title;
    /**
     * 创建人
     */
    private String author;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 计划发行日期
     */
    private Date plannedReleaseDate;
    /**
     * 关闭日期
     */
    private Date closeDate;
    /**
     * 关联测试用例
     */
    private String testCase;
    /**
     * 关联测试周期
     */
    private String testCycle;
    /**
     * 关联故事
     */
    private String feature;
    /**
     * 优先级
     */
    private String priority;
    /**
     * 环境
     */
    private String env;
    /**
     * 浏览器
     */
    private String browser;
    /**
     * 平台
     */
    private String platform;
    /**
     * 版本
     */
    private String version;
    /**
     * 用例分类
     */
    private String caseCategory;

    private String description;

    private Date createTime;

    private Date updateTime;

    @Transient
    private String scope = OneConstant.SCOPE.ONE_ISSUE;
    @Transient
    private String testCaseTitle;
    @Transient
    private String testCycleTitle;

    @Override
    public void verify() throws BizException {
        if(StringUtils.isEmpty(projectId)){
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(),"项目ID" + SysConstantEnum.PARAM_EMPTY.getValue());
        }else if(StringUtils.isEmpty(title)){
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(),"缺陷名称" + SysConstantEnum.PARAM_EMPTY.getValue());
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getPlannedReleaseDate() {
        return plannedReleaseDate;
    }

    public void setPlannedReleaseDate(Date plannedReleaseDate) {
        this.plannedReleaseDate = plannedReleaseDate;
    }

    public Date getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(Date closeDate) {
        this.closeDate = closeDate;
    }

    public String getTestCase() {
        return testCase;
    }

    public void setTestCase(String testCase) {
        this.testCase = testCase;
    }

    public String getTestCycle() {
        return testCycle;
    }

    public void setTestCycle(String testCycle) {
        this.testCycle = testCycle;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCaseCategory() {
        return caseCategory;
    }

    public void setCaseCategory(String caseCategory) {
        this.caseCategory = caseCategory;
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

    public String getTestCaseTitle() {
        return testCaseTitle;
    }

    public void setTestCaseTitle(String testCaseTitle) {
        this.testCaseTitle = testCaseTitle;
    }

    public String getTestCycleTitle() {
        return testCycleTitle;
    }

    public void setTestCycleTitle(String testCycleTitle) {
        this.testCycleTitle = testCycleTitle;
    }
}