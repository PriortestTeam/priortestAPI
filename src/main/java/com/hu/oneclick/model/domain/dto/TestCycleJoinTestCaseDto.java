package com.hu.oneclick.model.domain.dto;
import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import java.io.Serializable;
/**
 * ClassName: TestCycleJoinTestCaseDto
 * Package: com.hu.oneclick.model.domain.dto
 * Description:
 *
 * @Author 高级开发工程师-尹骁
 * @Create 2024/3/18 17:33
 * @Version 1.0
 */
@Data


public class TestCycleJoinTestCaseDto implements Serializable {
    private Long projectId;
    private Long testCycleId;
    private Long testCaseId;
    private Integer runStatus;
    private Integer stepStatus;
    private Long caseRunDuration;
    private Integer runCount;
    private Integer executeStatus;
    private JSONObject testCaseContent;
    private Long caseTotalPeriod;
    private Boolean addedOn;
}
}
}
