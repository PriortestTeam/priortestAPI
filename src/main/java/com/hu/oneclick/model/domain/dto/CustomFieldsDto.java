package com.hu.oneclick.model.domain.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//@Getter
//@Setter
//@AllArgsConstructor
//@NoArgsConstructor
@Data
public class CustomFieldsDto {

    /**
     * fieldType 为集合中的任何一项，则 possibleValue 中不能包含 parentListId 值
     */
    public static final List<String> NOT_PARENT_LIST_ID = new ArrayList<String>() {{
        this.add("multiList");
        this.add("dropDown");
        this.add("userList");
        this.add("number");
    }};

    /**
     * fieldType 为集合中的任何一项，则 possibleValue 中必须包含 parentListId 值
     */
    public static final List<String> NEED_PARENT_LIST_ID = new ArrayList<String>() {{
        this.add("linkedDropDown");
    }};

    private Long customFieldId;

    private String possibleValue;

    private String fieldType;

    private Date updateTime;

    private Long modifyUserId;

    private String projectId;
    
    private String type;
}
