package com.hu.oneclick.model.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data


public class IssueStatusVo {

    @Schema(description = "id");
    private Long id;

    @Schema(description = "状态");
    private String issueStatus;


}
}
