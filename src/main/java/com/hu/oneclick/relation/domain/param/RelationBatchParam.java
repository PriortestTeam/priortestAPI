
package com.hu.oneclick.relation.domain.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Setter
@Schema(description = "批量关系操作参数")
public class RelationBatchParam {

    /** 对象id */
    @Schema(description = "对象id")
    @NotBlank(message = "对象ID不能为空")
    private String objectId;

    /** 目标id列表 */
    @Schema(description = "目标id列表")
    @NotEmpty(message = "目标ID列表不能为空")
    private List<String> targetIdList;

    /** 分类 */
    @Schema(description = "分类")
    @NotBlank(message = "分类不能为空")
    private String category;
}
