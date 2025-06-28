
package com.hu.oneclick.model.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Data
@Schema(description = "签收参数")
public class SignOffParam {
    @Schema(description = "项目id", example = "1234567890")
    @NotNull
    private String projectId;

    @Schema(description = "所属环境", example = "开发")
    @NotNull
    private String env;

    @Schema(description = "发布版本", example = "1.0.0.0")
    @NotNull
    private String version;

    @Schema(description = "测试周期的标题", example = "[{testcycleId:'',testcycleTitle:''}]")
    @NotNull
    private List<Map<String, String>> testCycle;

    @Schema(description = "缺陷的状态", example = "修改中,关闭")
    @NotNull
    private String issue;

    @Schema(description = "上传的图片签名URL", example = "/tmp/xxxx.jpg")
    @NotNull
    private String fileUrl;

    @Schema(description = "是主动触发还是远程调用触发false|true", example = "false")
    @NotNull
    private boolean autoGenerate;

    @Schema(description = "当前发布")
    @NotNull
    private int currentRelease;
}
