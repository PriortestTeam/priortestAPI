package com.hu.oneclick.model.domain.vo;

import com.hu.oneclick.common.validator.group.AddGroup;
import com.hu.oneclick.common.validator.group.UpdateGroup;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.List;

/**
 * @ClassName CustomFieldsVO.java
 * @Description
 * @Author Vince
 * @CreateTime 2022年12月13日 22:00:00
 */
@Data
public class CustomFieldVo {


    /**
     * 主键
     */
    @NotNull(message = "修改必须指定id", groups = {UpdateGroup.class}) //修改时校验规则
    @Null(message = "新增不能指定id", groups = {AddGroup.class}) //新增时的校验规则
    private Long customFieldId;

    @NotNull(message = "type不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @NotEmpty(message = "type不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String type;

    @Valid
    private Attributes attributes;

    private String possibleValue;


    private List<ComponentAttributesVo> componentAttributes;


    @Data
    public class Attributes {
        @Length(max = 255,message = "长度不能超过255")
        private String fieldNameCn;

        @NotNull(message = "fieldType不能为空", groups = {AddGroup.class, UpdateGroup.class})
        @NotEmpty(message = "fieldType不能为空", groups = {AddGroup.class, UpdateGroup.class})
        private String fieldType;

        @NotNull(message = "fieldTypeCn不能为空", groups = {AddGroup.class, UpdateGroup.class})
        @NotEmpty(message = "fieldTypeCn不能为空", groups = {AddGroup.class, UpdateGroup.class})
        private String fieldTypeCn;

        /**
         * 关联项目id
         */
        @NotNull(message = "项目ID不能为空")
        private Long projectId;
        @Min(value = 0,message = "字段长度不能小于0")
        private Integer length;
    }


}
