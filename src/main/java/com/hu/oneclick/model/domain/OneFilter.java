package com.hu.oneclick.model.domain;

import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.common.util.DateUtil;
import com.hu.oneclick.model.base.VerifyParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.DateUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * @author qingyang
 */
public class OneFilter implements VerifyParam,Serializable {
    /**
     * 类型，
     */
    private String type;

    private String fieldName;

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

    /**f
     * 日期类型filter 使用
     */
    private String beginDate;
    private String endDate;

    @Override
    public void verify() throws BizException {
        if (StringUtils.isEmpty(this.type)){
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(),"类型" + SysConstantEnum.PARAM_EMPTY.getValue());
        }else if (StringUtils.isEmpty(this.andOr)){
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(),"条件" + SysConstantEnum.PARAM_EMPTY.getValue());
        }

        if (StringUtils.isEmpty(this.sourceVal)){
            return;
        }


        switch (type){
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
    private void fString(){
        //初始化字段长度
        int length = 30;
        String[] split = this.sourceVal.split(OneConstant.COMMON.ARRAY_CONVERTER_STRING_DELIMITER);
        if (split.length >= 2){
            int i;
            try {
                i = Integer.parseInt(split[0]);
            }catch (Exception e){
                throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(), "参数格式不正确。");
            }
            length = i > length ? 30 : i;
            this.textVal = this.sourceVal.substring((split[0] + OneConstant.COMMON.ARRAY_CONVERTER_STRING_DELIMITER).length());
        }
        if (this.textVal.length() > length){
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(), "文本内容不得超过" + length + "个字符。");
        }
        setSourceValIsNull();
    }
    /**
     * 数值
     */
    private void fInteger(){
        try {
            this.intVal = Integer.parseInt(this.sourceVal);
        }catch (Exception e){
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(), "参数格式不正确。");
        }
        setSourceValIsNull();
    }

    /**
     * 时间
     */
    private void fDateTime(){
        try {
            String[] split = this.sourceVal.split(OneConstant.COMMON.ARRAY_CONVERTER_STRING_DELIMITER);
            this.beginDate = DateUtil.format(DateUtil.parseDate(split[0]));
            this.endDate = DateUtil.format(DateUtil.parseDate(split[1]));
        }catch (Exception e){
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(), "时间格式不正确。");
        }
        setSourceValIsNull();
    }




    private void setSourceValIsNull(){
            this.sourceVal = null;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getAndOr() {
        return andOr;
    }

    public void setAndOr(String andOr) {
        this.andOr = andOr;
    }

    public String getSourceVal() {
        return sourceVal;
    }

    public void setSourceVal(String sourceVal) {
        this.sourceVal = sourceVal;
    }

    public Integer getIntVal() {
        return intVal;
    }

    public void setIntVal(Integer intVal) {
        this.intVal = intVal;
    }

    public String getTextVal() {
        return textVal;
    }

    public void setTextVal(String textVal) {
        this.textVal = textVal;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
