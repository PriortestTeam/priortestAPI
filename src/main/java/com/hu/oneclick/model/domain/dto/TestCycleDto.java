package com.hu.oneclick.model.domain.dto;

import com.hu.oneclick.model.entity.TestCase;
import com.hu.oneclick.model.entity.TestCycle;
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
