package com.hu.oneclick.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.hu.oneclick.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;

/**
 *TestCasesExecution 实体类
 *
 * @author Johnson
 * @date 2024年01月05日 14:22
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Component
@TableName(value ="test_cases_execution")
public class TestCasesExecution extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -2641443521395154160L;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = " GMT+8")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date rerunTime;
    private int runCount;
    private Long testCaseId;
    private Long testCycleId;
    private String projectId;
    private int runFlag;
//    private String testCaseStepId;
}
