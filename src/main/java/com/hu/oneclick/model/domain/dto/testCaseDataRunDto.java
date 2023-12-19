package com.hu.oneclick.model.domain.dto;


import lombok.Data;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Date;

@Data
@Component
public class testCaseDataRunDto {
    private int runCount;
    private String runStatus;
    private Date updateTime;
    private BigInteger createUserId;
}
