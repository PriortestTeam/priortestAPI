package com.hu.oneclick.model.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.model.base.BaseEntity;
import com.hu.oneclick.model.base.VerifyParam;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 缺陷(Issue)实体类
 *
 * @author makejava
 * @since 2021-02-17 16:20:43
 */
@Data
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

    @TableField(exist = false)
    private String scope = OneConstant.SCOPE.ONE_ISSUE;
    @TableField(exist = false)
    private String testCaseTitle;
    @TableField(exist = false)
    private String testCycleTitle;

    /**
     * 自定义字段值
     */
    private List<CustomFieldData> customFieldDatas;

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


}