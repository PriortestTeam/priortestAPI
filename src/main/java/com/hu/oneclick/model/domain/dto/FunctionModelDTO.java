package com.hu.oneclick.model.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author houaixia
 * 权限设置DTO
 */
@Data
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
public class FunctionModelDTO  implements Serializable {

    @ApiModelProperty(name= "funId", value = "模块id")
    private Integer funId;

    @ApiModelProperty(name= "btnStr", value = "功能字符串{'funId':40,'btnStr':'1,2,7'}）")
    private String btnStr;


}
