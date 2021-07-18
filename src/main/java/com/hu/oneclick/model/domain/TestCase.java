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


    private String browser;

    private String platform;

    private String version;

    private String caseCategory;

    private String testType;

    private String externaId;

    private String env;

    private String preCondition;

    private Integer lastRunStatus;

    private String module;
    private String testDevice;
    private String testData;
    private String testMethod;

    private String foreignId;

    @Transient
    private String featureTitle;

    @Transient
    private String scope = OneConstant.SCOPE.ONE_TEST_CASE;


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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getTestType() {
        return testType;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }

    public String getExternaId() {
        return externaId;
    }

    public void setExternaId(String externaId) {
        this.externaId = externaId;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getPreCondition() {
        return preCondition;
    }

    public void setPreCondition(String preCondition) {
        this.preCondition = preCondition;
    }

    public Integer getLastRunStatus() {
        return lastRunStatus;
    }

    public void setLastRunStatus(Integer lastRunStatus) {
        this.lastRunStatus = lastRunStatus;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getTestDevice() {
        return testDevice;
    }

    public void setTestDevice(String testDevice) {
        this.testDevice = testDevice;
    }

    public String getTestData() {
        return testData;
    }

    public void setTestData(String testData) {
        this.testData = testData;
    }

    public String getTestMethod() {
        return testMethod;
    }

    public void setTestMethod(String testMethod) {
        this.testMethod = testMethod;
    }

    public String getForeignId() {
        return foreignId;
    }

    public void setForeignId(String foreignId) {
        this.foreignId = foreignId;
    }

    public String getFeatureTitle() {
        return featureTitle;
    }

    public void setFeatureTitle(String featureTitle) {
        this.featureTitle = featureTitle;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
