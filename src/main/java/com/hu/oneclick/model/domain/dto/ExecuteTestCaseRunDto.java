package com.hu.oneclick.model.domain.dto;
import lombok.Data;
import org.springframework.stereotype.Component;
@Data
@Component


public class ExecuteTestCaseRunDto {
    private Long testCaseId;
    private Long testCycleId;
    private String projectId;
    private boolean runCountIndicator;
}
}
}
