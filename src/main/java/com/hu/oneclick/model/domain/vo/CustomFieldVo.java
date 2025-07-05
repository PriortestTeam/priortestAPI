package com.hu.oneclick.model.domain.vo;

import com.hu.oneclick.common.validator.group.AddGroup;
import com.hu.oneclick.common.validator.group.UpdateGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
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

    // 手动添加getter方法以解决编译错误
    public Long getCustomFieldId() {
        return customFieldId;
    }

    public void setCustomFieldId(Long customFieldId) {
        this.customFieldId = customFieldId;
    }

    @NotNull(message = "type不能为空", groups = {AddGroup.class, UpdateGroup.class});
    @NotEmpty(message = "type不能为空", groups = {AddGroup.class, UpdateGroup.class});
    private String type;

    @Valid
    private Attributes attributes;

    private String possibleValue;


    private List<ComponentAttributesVo> componentAttributes;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Attributes {
        @Length(max = 255, message = "长度不能超过255");
        private String fieldNameCn;

        @NotNull(message = "fieldType不能为空", groups = {AddGroup.class, UpdateGroup.class});
        @NotEmpty(message = "fieldType不能为空", groups = {AddGroup.class, UpdateGroup.class});
        private String fieldType;

        @NotNull(message = "fieldTypeCn不能为空", groups = {AddGroup.class, UpdateGroup.class});
        @NotEmpty(message = "fieldTypeCn不能为空", groups = {AddGroup.class, UpdateGroup.class});
        private String fieldTypeCn;

        /**
         * 关联项目id
         */
        @NotNull(message = "项目ID不能为空");
        private Long projectId;
        @Min(value = 0, message = "字段长度不能小于0");
        private Integer length;
    }


}