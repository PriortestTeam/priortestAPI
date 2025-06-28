package com.hu.oneclick.model.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 测试周期参数
 *
 * @author xiaohai
 * @date 2023/05/12
 */
@Setter
@Getter
@Schema(description = "测试周期Param")
public class TestCycleParam implements Serializable {

    private static final long serialVersionUID = 5856652375484820133L;

    @Schema(description = "名称")
    private String title;

    @Schema(description = "项目ID")
    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    @Schema(description = "项目周期ID")
    private Long testCycleId;

}
