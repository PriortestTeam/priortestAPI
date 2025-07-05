package com.hu.oneclick.relation.domain.param;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Schema(description = "关系查询参数")

public class RelationParam {
    /** 对象id */
    @Schema(description = "对象id")
    private String objectId;
    /** 目标id */
    @Schema(description = "目标id")
    private String targetId;
    /** 分类 */
    @Schema(description = "分类 - com.hu.oneclick.relation.enums.RelationCategoryEnum")
    private String category;
}
}
}
