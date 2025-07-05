package com.hu.oneclick.model.domain.dto;


import lombok.Data;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Date;

@Data
@Component
public class TestCaseDataRunDto {
    private int runCount;
    private int runStatus;
    private Date updateTime;
    private BigInteger createUserId;
    private BigInteger updateUserId;
    private BigInteger caseRunDuration;
    private BigInteger caseTotalPeriod;
}
