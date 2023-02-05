package com.hu.oneclick.model.domain;

import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.model.base.BaseEntity;
import com.hu.oneclick.model.base.VerifyParam;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 测试用例(TestCase)实体类
 *
 * @author makejava
 * @since 2021-02-04 13:51:21
 */
@Data
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
    private Integer runStatus;
    private Integer stepStatus;

    public Integer getRunStatus() {
        return runStatus;
    }

    public void setRunStatus(Integer runStatus) {
        this.runStatus = runStatus;
    }

    public Integer getStepStatus() {
        return stepStatus;
    }

    public void setStepStatus(Integer stepStatus) {
        this.stepStatus = stepStatus;
    }

    @Transient
    private String featureTitle;

    @Transient
    private String scope = OneConstant.SCOPE.ONE_TEST_CASE;

    /**
     * 状态
     */
    private String status;

    /**
     *
     */
    private String comments;


    /**
     * 自定义字段值
     */
    private List<CustomFieldData> customFieldDatas;

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



}
