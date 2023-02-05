package com.hu.oneclick.model.domain.dto;

import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.model.base.VerifyParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * sys_order_discount
 * @author masiyi
 */
@ApiModel(value="折扣表")
@Data
public class SysOrderDiscountDto implements Serializable, VerifyParam {

    /**
     * 订阅时长
     */
    @ApiModelProperty(value="订阅时长")
    private String subScription;

    /**
     * 容量大小
     */
    @ApiModelProperty(value="容量大小")
    private String dataStrorage;

    /**
     * apiCall
     */
    @ApiModelProperty(value="apiCall")
    private String apiCall;

    /**
     * 用户类型
     */
    @ApiModelProperty(value="用户类型")
    private String userClass;


    /**
     * 服务周期
     */
    @ApiModelProperty(value="服务周期")
    private String serviceDuration;

    @Override
    public void verify() throws BizException {
        if (StringUtils.isEmpty(subScription)) {
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(), "订阅时长" + SysConstantEnum.PARAM_EMPTY.getValue());
        } else if (StringUtils.isEmpty(dataStrorage)) {
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(), "容量大小" + SysConstantEnum.PARAM_EMPTY.getValue());
        } else if (StringUtils.isEmpty(apiCall)) {
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(), "apiCall" + SysConstantEnum.PARAM_EMPTY.getValue());
        } else if (StringUtils.isEmpty(userClass)) {
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(), "用户类型" + SysConstantEnum.PARAM_EMPTY.getValue());
        }else if (StringUtils.isEmpty(serviceDuration)) {
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(), "服务周期" + SysConstantEnum.PARAM_EMPTY.getValue());
        }
    }

    private static final long serialVersionUID = 115194643216464464L;
}