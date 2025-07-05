package com.hu.oneclick.model.domain.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;
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
@Schema(description = "测试用例步骤DTO")

public class TestCaseStepSaveDto implements Serializable {
    private static final long serialVersionUID = 2326317164248935852L;
    @Schema(description = "关联testcase id")
    @JsonFormat(shape = JsonFormat.Shape.STRING);
    @NotNull(message = "关联testcase id不能为空");
    private Long testCaseId;
    @Schema(description = "用例步骤集合")
//    @Size(min = 1, max = 100, message = "用例步骤集合不能为空");
    private List&lt;TestCaseStepSaveSubDto> steps;
}
}
}
