package com.hu.oneclick.model.domain.dto;

import java.io.Serializable;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Author: jhh
 * @Date: 2023/7/8
 */
@Data
public class TestCycleJoinTestCaseSaveDto implements Serializable {

    @NotNull(message = "项目id不能为空", groups = FrontSave.class)
    private Long projectId;

    @NotNull(message = "关联测试周期id不能为空", groups = { FrontSave.class, ApiSave.class })
    private Long testCycleId;

    private Long[] testCaseIds;

    public interface FrontSave {}

    public interface ApiSave {}
}
