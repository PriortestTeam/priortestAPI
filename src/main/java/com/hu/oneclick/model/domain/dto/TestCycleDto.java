package com.hu.oneclick.model.domain.dto;

import com.hu.oneclick.model.domain.TestCase;
import com.hu.oneclick.model.domain.TestCycle;
import lombok.Data;

import java.util.List;

/**
 * @author qingyang
 */
@Data
public class TestCycleDto extends TestCycle {

    private ViewTreeDto viewTreeDto;

    private String filter;

    private List<TestCase> testCases;

}
