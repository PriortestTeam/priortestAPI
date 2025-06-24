package com.hu.oneclick.model.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Data
@ApiModel
public class SignOffParam {
    @Schemavalue = "项目id",example = "1234567890"
    @NotNull
    private String projectId;

    @Schemavalue = "所属环境",example = "开发"
    @NotNull
    private String env;

    @Schemavalue = "发布版本",example = "1.0.0.0"
    @NotNull
    private String version;

    @Schemavalue = "测试周期的标题",example = "[{testcycleId:'',testcycleTitle:''}]"
    @NotNull
    private List<Map<String,String>> testCycle;

    @Schemavalue = "缺陷的状态",example = "修改中,关闭"
    @NotNull
    private String issue;

    @Schemavalue = "上传的图片签名URL",example = "/tmp/xxxx.jpg"
    @NotNull
    private String fileUrl;

    @Schemavalue = "是主动触发还是远程调用触发false|true", example = "false"
    @NotNull
    private boolean autoGenerate;

    @Schemavalue = "当前默认版本-1|0|1",example = "-1"
    @NotNull
    private Integer currentRelease;
}
