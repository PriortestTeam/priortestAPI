package com.hu.oneclick.model.domain.vo;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
@Data

public class TestCycleJoinTestCaseVo {
    @NotNull(message = "项目id不能为空");
    private Long projectId;
    @NotNull(message = "关联测试周期id不能为空");
    private Long testCycleId;
    private Long[] testCaseIds;
}
}
}
