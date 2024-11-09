package com.hu.oneclick.server.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.dao.TestCaseDao;
import com.hu.oneclick.dao.TestCycleDao;
import com.hu.oneclick.dao.TestCycleJoinTestCaseDao;
import com.hu.oneclick.dao.TestCycleTcDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.entity.TestCase;
import com.hu.oneclick.model.entity.TestCasesExecution;
import com.hu.oneclick.model.entity.TestCycle;
import com.hu.oneclick.model.entity.TestCycleJoinTestCase;
import com.hu.oneclick.model.domain.dto.TestCycleJoinTestCaseDto;
import com.hu.oneclick.model.domain.dto.TestCycleJoinTestCaseSaveDto;
import com.hu.oneclick.model.domain.vo.TestCycleJoinTestCaseVo;
import com.hu.oneclick.server.service.TestCycleJoinTestCaseService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author: jhh
 * @Date: 2023/7/8
 */
@Service
@Slf4j
public class TestCycleJoinTestCaseServiceImpl extends
    ServiceImpl<TestCycleJoinTestCaseDao, TestCycleJoinTestCase>
    implements TestCycleJoinTestCaseService {

  @Resource
  private TestCycleJoinTestCaseDao testCycleJoinTestCaseDao;

  @Resource
  private TestCycleTcDao testCycleTcDao;

  @Resource
  private TestCycleDao testCycleDao;

  @Resource
  private TestCaseDao testCaseDao;

  @Override
  public Boolean saveInstance(TestCycleJoinTestCaseSaveDto dto) {
    final List<Long> result = saveDataWithIdReturn(dto);
    if(!result.isEmpty()){
        UpdateWrapper<TestCycle> updateWrapper = Wrappers.update();
        updateWrapper.eq("id",dto.getTestCycleId());
        var sql = "instance_count=instance_count+" + result.size();
        updateWrapper.setSql(sql);
        testCycleDao.update(new TestCycle(),updateWrapper);
    }
    return true;
  }

  @Override
  public List<Long> saveDataWithIdReturn(TestCycleJoinTestCaseSaveDto dto) {
    List<Long> result = new ArrayList<>();
    TestCycleJoinTestCase joinTestCase;
    for (Long testCaseId : dto.getTestCaseIds()) {
      List<TestCycleJoinTestCase> entityList = this.getByProjectIdAndCycleIdAndCaseId(
          dto.getProjectId(),
          dto.getTestCycleId(), testCaseId);
      if (CollUtil.isNotEmpty(entityList)) {
        // throw new BaseException(StrUtil.format("该测试用例已关联"));
        continue;
      }
      joinTestCase = new TestCycleJoinTestCase();
      joinTestCase.setProjectId(dto.getProjectId());
      joinTestCase.setTestCycleId(dto.getTestCycleId());
      joinTestCase.setTestCaseId(testCaseId);
      this.testCycleJoinTestCaseDao.insert(joinTestCase);
      result.add(joinTestCase.getId());
    }
    return result;
  }

  @Override
  public Resp<Object> strictlySaveInstance(
      final TestCycleJoinTestCaseSaveDto dto) {
    // 验证 testCycleId 必须存在
    final LambdaQueryWrapper<TestCycle> testCycleWrapper = new LambdaQueryWrapper<>();
    final boolean existProject = testCycleDao.exists(
        testCycleWrapper.eq(TestCycle::getProjectId, dto.getProjectId()));
    if (!existProject) {
      throw new BizException(SysConstantEnum.TEST_CYCLE_NOT_EXIST_PROJECT.getCode(),
          SysConstantEnum.TEST_CYCLE_NOT_EXIST_PROJECT.getValue(), HttpStatus.BAD_REQUEST.value());
    }
    final boolean exists = testCycleDao.exists(
        testCycleWrapper
            .eq(TestCycle::getId, dto.getTestCycleId())
            .eq(TestCycle::getProjectId, dto.getProjectId())
    );
    if (!exists) {
      throw new BizException(SysConstantEnum.TEST_CYCLE_NOT_INCLUDE_IN_PROJECT.getCode(),
          SysConstantEnum.TEST_CYCLE_NOT_INCLUDE_IN_PROJECT.getValue(),
          HttpStatus.BAD_REQUEST.value());
    }

    // 验证 testCaseId 都必须存在
    final LambdaQueryWrapper<TestCase> testCaseWrapper = new LambdaQueryWrapper<>();
    final List<Long> testCaseIdList = Arrays.stream(dto.getTestCaseIds())
        .collect(Collectors.toList());
    final List<TestCase> testCases = testCaseDao.selectList(
        testCaseWrapper
            .eq(TestCase::getProjectId, dto.getProjectId())
            .in(TestCase::getId, testCaseIdList)
    );
    if (CollUtil.isEmpty(testCases)) {
      return new Resp.Builder<>().httpBadRequest().buildResult(
          SysConstantEnum.TEST_CASE_NOT_EXIST.getCode(),
          SysConstantEnum.TEST_CASE_NOT_EXIST.getValue(),
          testCaseIdList
      );
    }

    // 到达这里说明全部存在或者部分存在
    if (testCases.size() == testCaseIdList.size()) {
      final List<Long> savedIds = saveDataWithIdReturn(dto);
      List<Map<String, Long>> data = new ArrayList<>();
      for (int i = 0; i < savedIds.size(); i++) {
        data.add(Map.of("id", savedIds.get(i), "testCaseId", testCaseIdList.get(i)));
      }
      return new Resp.Builder<>().buildResult(
          SysConstantEnum.ADD_SUCCESS.getCode(), SysConstantEnum.ADD_SUCCESS.getValue(),
          data
      );
    }

    // 返回错误的 testCaseId 数据
    final List<Long> existTestCaseIdList = testCases.stream().map(TestCase::getId).collect(
        Collectors.toList());
    testCaseIdList.removeAll(existTestCaseIdList);
    return new Resp.Builder<>().httpBadRequest().buildResult(
        SysConstantEnum.TEST_CASE_NOT_EXIST.getCode(),
        SysConstantEnum.TEST_CASE_NOT_EXIST.getValue(),
        testCaseIdList
    );
  }

  private List<TestCycleJoinTestCase> getByProjectIdAndCycleIdAndCaseId(Long projectId,Long testCycleId,Long testCaseId) {
    LambdaQueryWrapper<TestCycleJoinTestCase> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(TestCycleJoinTestCase::getProjectId, projectId)
        .eq(TestCycleJoinTestCase::getTestCycleId, testCycleId)
        .eq(TestCycleJoinTestCase::getTestCaseId, testCaseId);
    return this.list(queryWrapper);
  }

  @Override
  @Transactional
  public void deleteInstance(TestCycleJoinTestCaseSaveDto dto) {
    List<Long> testCasesIds = new ArrayList<>();

    for (Long testCaseId : dto.getTestCaseIds()) {
      // 删除关联的test_cycle_join_test_case表
      this.testCycleJoinTestCaseDao.deleteByParam(dto.getProjectId(), dto.getTestCycleId(),
          testCaseId);
      testCasesIds.add(testCaseId);
    }
    testCasesIds = Arrays.asList(dto.getTestCaseIds());
//        //删除关联的relation表
//        this.relationService.removeBatchByTestCaseIds(testCasesIds);

    // 删除test_cases_execution表
    testCycleTcDao.delete(
        new LambdaQueryWrapper<TestCasesExecution>().in(TestCasesExecution::getTestCaseId,
                testCasesIds).eq(TestCasesExecution::getTestCycleId, dto.getTestCycleId())
            .eq(TestCasesExecution::getProjectId, dto.getProjectId()));

    if(!testCasesIds.isEmpty()){
        UpdateWrapper<TestCycle> updateWrapper = Wrappers.update();
        updateWrapper.eq("id", dto.getTestCycleId());
        var sql = "instance_count=instance_count-" + testCasesIds.size();
        updateWrapper.setSql(sql);
        testCycleDao.update(new TestCycle(),updateWrapper);
    }
  }

  @Override
  public List<Long> getCaseIdListByCycleId(Long testCycleId) {

    return this.testCycleJoinTestCaseDao.getCaseIdListByCycleId(testCycleId);
  }

  @Override
  public int countCycleIdByCaseId(Long testCaseId, Long projectid, Long cycleId) {
    return this.testCycleJoinTestCaseDao.countByTestCaseIdInt(testCaseId, projectid, cycleId);
  }

  @Override
  public TestCycleJoinTestCase getCycleJoinTestCaseByCaseId(Long caseId, Long projectId,
      Long cycleId) {
    return this.testCycleJoinTestCaseDao.getCycleJoinTestCaseByCaseId(caseId, projectId, cycleId);
  }

  /**
   * 更改runCaseStatus
   *
   * @param projectId
   * @param testCycleJoinTestCaseDto
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public Resp runCaseStatusUpdate(Long projectId,
      TestCycleJoinTestCaseDto testCycleJoinTestCaseDto) {

    // 参数校验
    if (testCycleJoinTestCaseDto == null || testCycleJoinTestCaseDto.getTestCycleId() == null
        || testCycleJoinTestCaseDto.getAddedOn() == null
        || testCycleJoinTestCaseDto.getTestCaseId() == null) {
      return new Resp.Builder<>().buildResult("非法参数");
    }

    TestCycleJoinTestCase testCycleJoinTestCase = this.getOne(
        new LambdaQueryWrapper<TestCycleJoinTestCase>()
            .eq(TestCycleJoinTestCase::getTestCycleId, testCycleJoinTestCaseDto.getTestCycleId())
            .eq(TestCycleJoinTestCase::getProjectId, projectId)
            .eq(TestCycleJoinTestCase::getTestCaseId, testCycleJoinTestCaseDto.getTestCaseId()));

    if (testCycleJoinTestCase == null) {
      return new Resp.Builder<>().buildResult("数据不存在");
    }

    testCycleJoinTestCase.setRunStatus(testCycleJoinTestCaseDto.getRunStatus());
    if (testCycleJoinTestCaseDto.getAddedOn()) {
      testCycleJoinTestCase.setCaseRunDuration((int) (testCycleJoinTestCase.getCaseRunDuration()
          + testCycleJoinTestCaseDto.getCaseRunDuration()));
      testCycleJoinTestCase.setCaseTotalPeriod(testCycleJoinTestCase.getCaseTotalPeriod()
          + testCycleJoinTestCaseDto.getCaseTotalPeriod());
      testCycleJoinTestCase.setRunCount(
          testCycleJoinTestCase.getRunCount() + testCycleJoinTestCaseDto.getRunCount());
    } else {
      testCycleJoinTestCase.setCaseRunDuration(
          testCycleJoinTestCaseDto.getCaseRunDuration().intValue());
      testCycleJoinTestCase.setCaseTotalPeriod(testCycleJoinTestCaseDto.getCaseTotalPeriod());
      testCycleJoinTestCase.setRunCount(testCycleJoinTestCaseDto.getRunCount());
    }

    // 更新
    boolean b = this.updateById(testCycleJoinTestCase);
    if (!b) {
      return new Resp.Builder<>().buildResult("更新失败");
    }
    return new Resp.Builder<>().ok();
  }

  @Override
  public TestCycleJoinTestCaseVo removeTCsFromTestCycle(Long projectId, TestCycleJoinTestCaseSaveDto dto) {
    TestCycleJoinTestCaseVo vo = new TestCycleJoinTestCaseVo();
    List<TestCycleJoinTestCase> testCycleJoinTestCases = baseMapper.selectList(
            new LambdaQueryWrapper<TestCycleJoinTestCase>()
                    .eq(TestCycleJoinTestCase::getProjectId, dto.getProjectId())
                    .eq(TestCycleJoinTestCase::getTestCycleId, dto.getTestCycleId())
    );
    List<Long> collect = testCycleJoinTestCases.stream().map(TestCycleJoinTestCase::getTestCaseId).collect(Collectors.toList());
    Long[] testCaseIds = dto.getTestCaseIds();
    List<Long> longs = Arrays.asList(testCaseIds);
    boolean b = collect.removeAll(longs);
    if(b){
      TestCycleJoinTestCaseSaveDto td = new TestCycleJoinTestCaseSaveDto();
      td.setProjectId(dto.getProjectId());
      td.setTestCycleId(dto.getTestCycleId());
      if(!collect.isEmpty()){
        td.setTestCaseIds( collect.toArray(new Long[collect.size()]));
        deleteInstance(td);
      }
      vo.setProjectId(td.getProjectId());
      vo.setTestCycleId(td.getTestCycleId());
      vo.setTestCaseIds(td.getTestCaseIds());
    }
    return vo;
  }



}
