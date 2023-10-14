package com.hu.oneclick.model.domain;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hu.oneclick.model.base.AssignBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 视图表实体
 *
 * @author xiaohai
 * @date 2023/08/20
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("视图实体")
@TableName("view")
public class View extends AssignBaseEntity implements Serializable {

    private static final long serialVersionUID = 2648094842570049550L;

    /**
     * 关联项目id
     */
    private String projectId;
    /**
     * view 名称
     */
    private String title;
    /**
     * 使用区域
     */
    private String scopeName;
    /**
     * 公开还是私有,默认 0 公开
     */
    private Integer isPrivate;
    /**
     * 创建人
     */
    private String creater;
    /**
     * {},自定义json 对象
     */
    private String filter;
    /**
     * sql
     */
    @TableField("`sql`")
    private String sql;

    @TableField(exist = false)
    private List<OneFilter> oneFilters;

    /**
     * 修改人
     */
    private String updateUser;

    private String parentId;

    private String scopeId;

    private Integer level;

    @ApiModelProperty(value = "Default:0, 自渲染1")
    @TableField(exist = false)
    private Integer isAuto;

    public String getFilter() {
        if (CollUtil.isNotEmpty(oneFilters)) {
            return JSON.toJSONString(oneFilters);
        }
        return filter;
    }

    public List<OneFilter> getOneFilters() {
        if (StrUtil.isNotBlank(filter)) {
            return JSON.parseArray(filter, OneFilter.class);
        }
        return oneFilters;
    }
}
