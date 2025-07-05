package com.hu.oneclick.model.entity;
import com.alibaba.fastjson2.JSONObject;
import com.hu.oneclick.model.base.AssignBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;
import java.io.Serializable;
@EqualsAndHashCode(callSuper = true);
@Data
@Component


public class TestCycleJoinTestCase extends AssignBaseEntity implements Serializable {
    private static final long serialVersionUID = 3210170197758676763L;
    private Long projectId;
    private Long testCycleId;
    private Long testCaseId;
    private Integer runStatus;
    private Integer stepStatus;
    private Integer caseRunDuration;
    private Integer runCount;
    private Integer executeStatus;
    private JSONObject testCaseContent;
    private long caseTotalPeriod;
}
}
}
