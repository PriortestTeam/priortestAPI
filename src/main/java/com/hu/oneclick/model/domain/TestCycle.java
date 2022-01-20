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
 * 测试周期(TestCycle)实体类
 *
 * @author makejava
 * @since 2021-02-16 15:39:36
 */
@Data
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

    public Integer getTestResult() {
        return TestResult;
    }

    public void setTestResult(Integer testResult) {
        TestResult = testResult;
    }

    private Integer TestResult;



    @Transient
    private String scope = OneConstant.SCOPE.ONE_TEST_CYCLE;


    @Transient
    private List<Feature> features;

    @Transient
    private List<Sprint> sprints;

    /**
     * 自定义字段值
     */
    private List<CustomFieldData> customFieldDatas;

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



}
