package com.hu.oneclick.model.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 *TestCasesExecution 实体类
 *
 * @author Johnson
 * @date 2024年01月05日 14:22
 */
@Data
@Component
@TableName(value ="test_cases_execution")
public class TestCasesExecution {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = " GMT+8")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date rerunTime;
    private int runCount;
    private Long testCaseId;
    private Long testCycleId;
    private String projectId;
}
