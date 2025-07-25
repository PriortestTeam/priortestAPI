
package com.hu.oneclick.model.param;

import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "用户故事请求对象")
@Data
public class UserCaseParam implements Serializable {

    @Schema(description = "id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private long id;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "类别")
    private String useCategory;
    
    /**
     * 级别
     */
    @Schema(description = "级别")
    private String level;
    
    /**
     * 等级
     */
    @Schema(description = "等级")
    private String grade;

    /**
     * 流程场景
     */
    @Schema(description = "流程场景")
    private String scenario;
    
    @Schema(description = "故事用例扩展")
    private JSONObject usecaseExpand;

    @Schema(description = "所属故事ID")
    private long featureId;

    @Schema(description = "版本")
    private String version;

    @Schema(description = "备注")
    private String remarks;

    @Schema(description = "创建时间")
    private java.util.Date createTime;
}
