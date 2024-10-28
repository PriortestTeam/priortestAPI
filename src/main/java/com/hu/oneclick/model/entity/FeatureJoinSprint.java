package com.hu.oneclick.model.entity;

import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.model.base.VerifyParam;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * (FeatureJoinSprint)实体类
 *
 * @author makejava
 * @since 2021-03-16 09:53:28
 */
public class FeatureJoinSprint implements VerifyParam, Serializable {
    private static final long serialVersionUID = -98473243422555529L;

    private String featureId;

    private String sprint;

    @Override
    public void verify() throws BizException {
        if(StringUtils.isEmpty(featureId)){
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(),"故事ID" + SysConstantEnum.PARAM_EMPTY.getValue());
        }else if(StringUtils.isEmpty(sprint)){
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(),"迭代ID" + SysConstantEnum.PARAM_EMPTY.getValue());
        }
    }
    public String getFeatureId() {
        return featureId;
    }

    public void setFeatureId(String featureId) {
        this.featureId = featureId;
    }

    public String getSprint() {
        return sprint;
    }

    public void setSprint(String sprint) {
        this.sprint = sprint;
    }


}
