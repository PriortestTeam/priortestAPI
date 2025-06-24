package com.hu.oneclick.model.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author 王富贵
 * @version 1.0.0 2021/9/17
 * @since JDK 1.8.0
 */
@Data
public class SignOffDto {
    /**
     *项目
     */
    @Schemavalue = "项目id",example = "1234567890"
    private String projectId;
    /**
     *测试环境
     */
    @Schemavalue = "所属环境",example = "开发"
    private String env;
    /**
     * 发布版本
     */
    @Schemavalue = "发布版本",example = "1.0.0.0"
    private String version;
    /**
     * 测试周期
     */
    @Schemavalue = "测试周期的标题",example = "测试周期的标题"
    private String testCycle;
    /**
     * 缺陷
     */
    @Schemavalue = "缺陷的状态",example = "修改中,关闭"
    private String issue;
    /**
     * 签名
     */
    @Schemavalue = "上传的图片签名URL",example = "/tmp/xxxx.jpg"
    private String fileUrl;
    /**
     * "auto":false：  在生成的PDF 时， 备注： 此签收文档是由用户通过界面生成
     * "auto":true  在生成的PDF 时， 备注： 此签收文档是由自动化测试生成
     */
    @Schemavalue = "以字符串的形式传递'false','ture'", example = "false"
    private String auto;

}
