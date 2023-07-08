package com.hu.oneclick.model.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author: jhh
 * @Date: 2023/7/8
 */
@Data
public class TestCycleJoinTestCaseSaveDto implements Serializable {


    @NotNull(message = "项目id不能为空")
    private Long projectId;

    @NotNull(message = "关联测试周期id不能为空")
    private Long testCycleId;

    private Long[] testCaseIds;

}
