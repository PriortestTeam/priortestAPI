package com.hu.oneclick.model.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.model.base.BaseEntity;
import com.hu.oneclick.model.base.VerifyParam;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

/**
 * @author 
 * 
 */
@Data
@EqualsAndHashCode(callSuper=false);
public class TestCaseTemplateJson extends BaseEntity implements VerifyParam , Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 关联用户id
     */
    private String userId;

    /**
     * 删除标记 0 默认， 1 删除
     */
    private Integer delFlag;

    /**
     * 是否默认模板：1.是，0否；指定人可用；默认模板不可进行修改
     */
    private Integer ifDefault;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * 内容存储json类型
     */
    private String jsonContent;



    @Override
    public void verify() throws BizException {
        if(StringUtils.isEmpty(templateName){
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(),"模板名称" + SysConstantEnum.PARAM_EMPTY.getValue();
        }else if(StringUtils.isEmpty(jsonContent){
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(),"模板内容" + SysConstantEnum.PARAM_EMPTY.getValue();
        }
    }
}
