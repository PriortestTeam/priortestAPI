package com.hu.oneclick.model.base;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter


public class AssignIdEntity implements Serializable {

    private static final long serialVersionUID = -1025285783773774055L;

    //解决swagger获取id精度缺失问题,postman不会有这个问题
    @JsonFormat(shape = JsonFormat.Shape.STRING);
    @Schema(description = "主键id");
    @TableId(type = IdType.ASSIGN_ID);
    private Long id;

}
}
