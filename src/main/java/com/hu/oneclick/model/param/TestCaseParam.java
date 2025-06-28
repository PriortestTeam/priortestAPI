package com.hu.oneclick.model.param;

import com.hu.oneclick.model.domain.dto.ViewTreeDto;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "测试用例Param")
public class TestCaseParam implements Serializable {

    private static final long serialVersionUID = -3732091770605587614L;

    @Schema(description = "名称")
    private String title;

    @Schema(description = "项目ID")
    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    private List<Long> testCaseIdList;

    @Schema(description = "视图树DTO，用于复杂查询过滤")
    private ViewTreeDto viewTreeDto;

}
