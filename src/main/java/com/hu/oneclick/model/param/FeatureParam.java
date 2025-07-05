package com.hu.oneclick.model.param;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hu.oneclick.model.entity.Feature;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author: jhh
 * @Date: 2023/4/24
 */
@Getter
@Setter
@Schema(description = "故事Param")
public class FeatureParam implements Serializable {
    @Schema(description = "名称")
    private String title;

    @Schema(description = "项目ID")
    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    @Schema(description = "视图ID")
    private String viewId;

    public LambdaQueryWrapper getQueryCondition() {
        LambdaQueryWrapper<Feature> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(this.title), Feature::getTitle, this.title);
        queryWrapper.eq(null != this.projectId, Feature::getProjectId, this.projectId);
        queryWrapper.orderByDesc(Feature::getCreateTime);
        return queryWrapper;
    }
}
package com.hu.oneclick.model.param;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
public class FeatureParam {
    
    private Long id;
    
    private String title;
    
    private Long projectId;
    
    private Date createTime;
    
    public String getTitle() {
        return title;
    }
    
    public Long getProjectId() {
        return projectId;
    }
    
    public Date getCreateTime() {
        return createTime;
    }
}
