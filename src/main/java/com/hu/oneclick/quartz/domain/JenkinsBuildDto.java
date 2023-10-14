package com.hu.oneclick.quartz.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Map;

/**
 * Jenkins构建DTO
 *
 * @author xiaohai
 * @date 2023/07/11
 */
@Setter
@Getter
@ApiModel("Jenkins构建DTO")
public class JenkinsBuildDto implements Serializable {

    private static final long serialVersionUID = 3308577719580670615L;

    @ApiModelProperty(value = "任务名称")
    @NotBlank(message = "任务名称不能为空")
    private String jobName;

    @ApiModelProperty(value = "参数")
    private Map<String, String> param;

}
