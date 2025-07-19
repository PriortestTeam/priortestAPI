
package com.hu.oneclick.model.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户故事返回对象
 */
@Data
@Schema(description = "用户故事返回对象")
public class UserCaseVo implements Serializable {

    @Schema(description = "id")
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
    private String usecaseExpand;

    @Schema(description = "所属故事ID")
    private long featureId;

    @Schema(description = "版本")
    private String version;

    @Schema(description = "备注")
    private String remarks;

    @Schema(description = "创建时间")
    private java.util.Date createTime;
}
