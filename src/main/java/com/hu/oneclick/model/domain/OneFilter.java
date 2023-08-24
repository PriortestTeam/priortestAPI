package com.hu.oneclick.model.domain;

import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.common.util.DateUtil;
import com.hu.oneclick.model.base.VerifyParam;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * @author qingyang todo
 */
@Data
public class OneFilter implements VerifyParam, Serializable {
    /**
     * 类型，
     */
    private String type;

    private String fieldName;

    private String fieldNameEn;

    private String fieldType;

    /**
     * and || or
     */
    private String andOr;

    /**
     * 根据type，类型确定sourceVal 值
     */
    private String sourceVal;

    /**
     * 数值类型使用
     */
    private Integer intVal;

    /**
     * 字符串类型使用
     */
    private String textVal;

    /**
     * f
     * 日期类型filter 使用
     */
    private String beginDate;

    private String endDate;

    /**
     * 条件
     * 中间的条件：
     * Is 等于
     * IsNot 不等于
     * IsEmpty 为空
     * IsNotEmpty 不为空
     * MoreThan 大于
     * LessThan 小于
     * Include 包含
     * Exclude 不包含
     *
     * @Author: MaSiyi
     * @Date: 2021/12/22
     */
    private String condition;


    /** 字段类型 系统字段 sys 用户字段 user
     * @Param:
     * @return:
     * @Author: MaSiyi
     * @Date: 2021/12/31
     */
    private String customType;


    @Override
    public void verify() throws BizException {
        if (StringUtils.isEmpty(this.type)) {
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(), "类型" + SysConstantEnum.PARAM_EMPTY.getValue());
        } else if (StringUtils.isEmpty(this.andOr)) {
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(), "条件" + SysConstantEnum.PARAM_EMPTY.getValue());
        }

        if (StringUtils.isEmpty(this.sourceVal)) {
            return;
        }


        switch (type) {
            case "fString":
                fString();
                break;
            case "fInteger":
                fInteger();
                break;
            case "fDateTime":
                fDateTime();
                break;
            default:
                break;
        }
    }

    /**
     * 字符串
     */
    private void fString() {
        //初始化字段长度
        int length = 30;
        String[] split = this.sourceVal.split(OneConstant.COMMON.ARRAY_CONVERTER_STRING_DELIMITER);
        if (split.length >= 2) {
            int i;
            try {
                i = Integer.parseInt(split[0]);
            } catch (Exception e) {
                throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(), "参数格式不正确。");
            }
            length = i > length ? 30 : i;
            this.textVal = this.sourceVal.substring((split[0] + OneConstant.COMMON.ARRAY_CONVERTER_STRING_DELIMITER).length());
        }
        if (this.textVal.length() > length) {
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(), "文本内容不得超过" + length + "个字符。");
        }
        setSourceValIsNull();
    }

    /**
     * 数值
     */
    private void fInteger() {
        try {
            this.intVal = Integer.parseInt(this.sourceVal);
        } catch (Exception e) {
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(), "参数格式不正确。");
        }
        setSourceValIsNull();
    }

    /**
     * 时间
     */
    private void fDateTime() {
        try {
            String[] split = this.sourceVal.split(OneConstant.COMMON.ARRAY_CONVERTER_STRING_DELIMITER);
            this.beginDate = DateUtil.format(DateUtil.parseDate(split[0]));
            this.endDate = DateUtil.format(DateUtil.parseDate(split[1]));
        } catch (Exception e) {
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(), "时间格式不正确。");
        }
        setSourceValIsNull();
    }


    private void setSourceValIsNull() {
        this.sourceVal = null;
    }


}
