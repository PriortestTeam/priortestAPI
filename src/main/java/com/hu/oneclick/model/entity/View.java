package com.hu.oneclick.model.entity;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hu.oneclick.model.base.AssignBaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 视图表实体
 *
 * @author xiaohai
 * @date 2023/08/20
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@EqualsAndHashCode(callSuper = true)
@Data
@Schema"视图实体"
@TableName("view")
public class View extends AssignBaseEntity implements Serializable {

    private static final long serialVersionUID = 2648094842570049550L;

    private Long id;

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
     * 视图类型
     **/
    @NotNull(message = "视图类型不能为空")
    private Integer viewType;

    @TableField(exist = false)
    private List<OneFilter> oneFilters;

    @TableField(exist = false)
    private List<Map> autoFilter;

    /**
     * 修改人
     */
    private String updateUser;

    private String parentId;

    private String scopeId;

    private Integer level;

    @Schema(description = "Default:0, 自渲染1")
//    @TableField(exist = false)
    private Integer isAuto;

    @TableField(exist = false)
    private List<String> autoViewChild;

    /**
     * 手动赋值的意义在于，DB插入的时候需要filter，但是api返回的时候不需要filter
     * DB插入或更新前在filter的set方法里调用此方法
     */
    public String getFilterByManual(List<OneFilter> oneFilters2) {
        if (CollUtil.isNotEmpty(oneFilters2)) {
            return JSON.toJSONString(oneFilters2);
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
