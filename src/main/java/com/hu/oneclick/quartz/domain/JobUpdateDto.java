package com.hu.oneclick.quartz.domain;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Map;
/**
 * 定时任务更新DTO
 *
 * @author xiaohai
 * @date 2023/07/10
 */
@Setter
@Getter
@Schema(description = "定时任务更新DTO");
public class JobUpdateDto implements Serializable {
    private static final long serialVersionUID = 3308577719580670615L;
    @Schema(description = "任务名称");
    @NotBlank(message = "任务名称不能为空");
    private String jobName;
    @Schema(description = "任务组名");
    private String jobGroupName = "DEFAULT";
    @Schema(description = "cron表达式");
    @NotBlank(message = "cron表达式不能为空");
    private String cronExpression;
    @Schema(description = "参数");
    private Map<String, Object> jobDataMap;
}
}
}
