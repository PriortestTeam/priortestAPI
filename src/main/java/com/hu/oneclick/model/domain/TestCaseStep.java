package com.hu.oneclick.model.domain;

import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.model.base.BaseEntity;
import com.hu.oneclick.model.base.VerifyParam;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * (TestCaseStep)实体类
 *
 * @author makejava
 * @since 2021-02-07 10:25:52
 */
public class TestCaseStep extends BaseEntity implements Serializable, VerifyParam {
    private static final long serialVersionUID = -81638569883101943L;

    /**
     * 关联testCase id
     */
    private String testCaseId;
    /**
     * 步骤
     */
    private String step;
    /**
     * 测试日期
     */
    private Date testDate;
    /**
     * 预期结果
     */
    private String expectedResult;
    /**
     * 0 未执行， 1 执行失败 2 执行 成功
     */
    private Integer status;

    private Date createTime;

    @Override
    public void verify() throws BizException {
        if(StringUtils.isEmpty(testCaseId)){
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(),"测试用例ID" + SysConstantEnum.PARAM_EMPTY.getValue());
        }else if(StringUtils.isEmpty(step)){
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(),"迭代名称" + SysConstantEnum.PARAM_EMPTY.getValue());
        }
    }

    public void queryListVerify() {

        if(StringUtils.isEmpty(testCaseId)){
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(),"测试用例ID" + SysConstantEnum.PARAM_EMPTY.getValue());
        }

        super.setId(null);

    }


    public String getTestCaseId() {
        return testCaseId;
    }

    public void setTestCaseId(String testCaseId) {
        this.testCaseId = testCaseId;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public Date getTestDate() {
        return testDate;
    }

    public void setTestDate(Date testDate) {
        this.testDate = testDate;
    }

    public String getExpectedResult() {
        return expectedResult;
    }

    public void setExpectedResult(String expectedResult) {
        this.expectedResult = expectedResult;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }



}
