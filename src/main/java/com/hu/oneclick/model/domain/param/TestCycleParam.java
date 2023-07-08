package com.hu.oneclick.model.domain.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel("测试周期Param")
public class TestCycleParam implements Serializable {

    private static final long serialVersionUID = 5856652375484820133L;

    @ApiModelProperty("名称")
    private String title;

    @ApiModelProperty("项目ID")
    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    @ApiModelProperty("项目周期ID")
    private Long testCycleId;

}
