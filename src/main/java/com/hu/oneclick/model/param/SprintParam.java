package com.hu.oneclick.model.param;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hu.oneclick.model.entity.Sprint;
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
@Schema(description = "迭代Param")

public class SprintParam implements Serializable {
    @Schema(description = "名称")
    private String title;
    @Schema(description = "项目ID")
    @NotNull(message = "项目ID不能为空");
    private Long projectId;
    @Schema(description = "视图ID")
    private String viewId;
    public LambdaQueryWrapper getQueryCondition() {
        LambdaQueryWrapper<Sprint> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(this.title), Sprint::getTitle, this.title);
        queryWrapper.eq(null != this.projectId, Sprint::getProjectId, this.projectId);
        queryWrapper.orderByDesc(Sprint::getCreateTime);
        return queryWrapper;
    }
}
}
}
