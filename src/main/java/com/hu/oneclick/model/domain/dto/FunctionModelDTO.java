package com.hu.oneclick.model.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schemaname= "funId", value = "模块id"
    private Integer funId;

    @Schemaname= "btnStr", value = "功能字符串{'funId':40,'btnStr':'1,2,7'}）"
    private String btnStr;


}
