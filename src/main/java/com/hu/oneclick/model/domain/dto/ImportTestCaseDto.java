package com.hu.oneclick.model.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author xwf
 * @date 2021/8/29 17:02
 * 导入测试用例返回参数
 */
@Data
public class ImportTestCaseDto  implements Serializable {

    private List error;


    private List warning;


    private List success;

}
