package com.hu.oneclick.model.domain.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 测试用例Param
 *
 * @author xiaohai
 * @date 2023/03/06
 */
@Setter
@Getter
@ApiModel("测试用例Param")
public class TestCaseParam implements Serializable {

    private static final long serialVersionUID = -3732091770605587614L;

    @ApiModelProperty("名称")
    private String title;

    @ApiModelProperty("项目ID")
    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    private List<Long> testCaseIdList;

}
