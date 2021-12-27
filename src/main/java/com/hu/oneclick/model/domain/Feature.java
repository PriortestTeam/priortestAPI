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
 * 故事(Feature)实体类
 *
 * @author makejava
 * @since 2021-02-03 13:54:35
 */
@Data
public class Feature extends BaseEntity implements Serializable, VerifyParam {
    private static final long serialVersionUID = 495256750642592776L;

    /**
     * 关联项目id
     */
    private String projectId;
    /**
     * 记录
     */
    private String epic;
    /**
     * 指派给谁
     */
    private String reportTo;
    /**
     * 状态，（1 progress ，0 closed, 2 plan）
     */
    @Transient
    private Integer status;
    /**
     * 版本
     */
    private String version;
    /**
     * 描述
     */
    private String description;
    /**
     * 关闭时间
     */
    private Date closeDate;
    /**
     * 管理人
     */
    private String authorName;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 名称
     */
    private String title;
    /**
     * 关联用户id
     */
    private String userId;

    private Date updateTime;

    private String moudle;

    /**
     * 关联迭代表的title
     */
    @Transient
    private String sprintTitle;


    @Transient
    private String scope = OneConstant.SCOPE.ONE_FEATURE;

    @Transient
    List<Sprint> sprints;

    @Transient
    private String closeDateBegin;
    @Transient
    private String closeDateEnd;
    @Transient
    private String createTimeBegin;
    @Transient
    private String createTimeEnd;

    /**
     * 自定义字段值
     */
    private List<CustomFieldData> customFieldDatas;

    @Override
    public void verify() throws BizException {
        if (StringUtils.isEmpty(projectId)) {
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(), "项目ID" + SysConstantEnum.PARAM_EMPTY.getValue());
        } else if (StringUtils.isEmpty(title)) {
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(), "故事名称" + SysConstantEnum.PARAM_EMPTY.getValue());
        } else if (StringUtils.isEmpty(version)) {
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(), "版本" + SysConstantEnum.PARAM_EMPTY.getValue());
        }
    }


    public void queryListVerify() {
        if (StringUtils.isEmpty(projectId)) {
            throw new BizException(SysConstantEnum.PARAM_EMPTY.getCode(), "项目ID" + SysConstantEnum.PARAM_EMPTY.getValue());
        }
        super.setId(null);
    }

}
