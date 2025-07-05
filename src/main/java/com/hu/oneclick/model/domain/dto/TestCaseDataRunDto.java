package com.hu.oneclick.model.domain.dto;
import lombok.Data;
import org.springframework.stereotype.Component;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigInteger;
import java.util.Date;
@Data
@Schema(description = "测试用例运行数据DTO");

public class TestCaseDataRunDto {
    @Schema(description = "运行次数");
    private Integer runCount;
    @Schema(description = "运行状态");
    private Integer runStatus;
    @Schema(description = "更新时间");
    private java.util.Date updateTime;
    @Schema(description = "创建用户ID");
    private java.math.BigInteger createUserId;
    @Schema(description = "更新用户ID");
    private java.math.BigInteger updateUserId;
    @Schema(description = "用例运行时长");
    private java.math.BigInteger caseRunDuration;
    @Schema(description = "用例总周期");
    private java.math.BigInteger caseTotalPeriod;
    // 手动添加setter方法
    public void setRunCount(int runCount) {
        this.runCount = runCount;
    }
    public void setRunStatus(int runStatus) {
        this.runStatus = runStatus;
    }
    public void setUpdateTime(java.util.Date updateTime) {
        this.updateTime = updateTime;
    }
    public void setCreateUserId(java.math.BigInteger createUserId) {
        this.createUserId = createUserId;
    }
    public void setUpdateUserId(java.math.BigInteger updateUserId) {
        this.updateUserId = updateUserId;
    }
    public void setCaseRunDuration(java.math.BigInteger caseRunDuration) {
        this.caseRunDuration = caseRunDuration;
    }
    public void setCaseTotalPeriod(java.math.BigInteger caseTotalPeriod) {
        this.caseTotalPeriod = caseTotalPeriod;
    }
}