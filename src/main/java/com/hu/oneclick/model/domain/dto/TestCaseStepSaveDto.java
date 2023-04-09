package com.hu.oneclick.model.domain.dto;

import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * 测试用例步骤DTO
 *
 * @author xiaohai
 * @date 2023/04/09
 */
@Setter
@Getter
@ApiModel("测试用例步骤DTO")
public class TestCaseStepSaveDto implements Serializable {

    private static final long serialVersionUID = 2326317164248935852L;

    @ApiModelProperty("关联testcase id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @NotNull(message = "关联testcase id不能为空")
    private Long testCaseId;

    @ApiModelProperty("用例步骤集合")
    @Size(min = 1, max = 100, message = "用例步骤集合不能为空")
    private List<TestCaseStepSaveSubDto> steps;

    @ApiModelProperty("自定义字段值")
    private JSONObject customFieldDatas;

}
