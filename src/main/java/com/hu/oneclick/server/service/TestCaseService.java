package com.hu.oneclick.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.entity.Feature;
import com.hu.oneclick.model.entity.TestCase;
import com.hu.oneclick.model.entity.TestCycle;
import com.hu.oneclick.model.domain.dto.*;
import com.hu.oneclick.model.param.TestCaseParam;
import java.util.List;
import java.util.Map;

import com.hu.oneclick.model.domain.vo.IssueStatusVo;
import org.springframework.web.multipart.MultipartFile;
import com.github.pagehelper.PageInfo;

/**
 * @author qingyang
 */
public interface TestCaseService extends IService<TestCase> {

  Resp<List<LeftJoinDto>> queryTitles(String projectId, String title);

  Resp<TestCase> queryById(Long id);

  Resp<List<TestCase>> queryList(TestCaseDto testCase);

  Resp<String> insert(TestCase testCase);

  Resp<String> update(TestCase testCase);

  Resp<String> delete(String id);

  Resp<Feature> queryTestNeedByFeatureId(String featureId);

  /**
   * excel导入测试用例
   *
   * @param file
   * @param param
   * @return
   */
  Resp<ImportTestCaseDto> importTestCase(MultipartFile file, String param);

  /**
   * 添加测试用例
   *
   * @param testCycleDto
   * @Param: [testCase]
   * @return: com.hu.oneclick.model.base.Resp<java.lang.String>
   * @Author: MaSiyi
   * @Date: 2021/12/1
   */
  Resp<String> addTestCase(TestCycleDto testCycleDto);

  /**
   * 更新action
   *
   * @Param: [testCaseId]
   * @return: com.hu.oneclick.model.base.Resp<java.lang.String>
   * @Author: MaSiyi
   * @Date: 2021/12/1
   */
  Resp<List<TestCase>> updateAction(List<String> testCaseId, String actionType, String testCycleId);

  List<TestCase> list(TestCaseParam param);

  /**
   * 支持视图过滤的测试用例列表查询
   * 支持 viewId 参数格式
   *
   * @param param 查询参数，包含视图过滤条件
   * @return 测试用例列表
   */
  List<TestCase> listWithViewFilter(TestCaseParam param);

  /**
   * 使用BeanSearcher框架的视图过滤查询
   * 与BeanSearchController使用相同的查询逻辑
   *
   * @param viewId 视图ID
   * @param projectId 项目ID
   * @return 测试用例列表
   */
  List<Map<String, Object>> listWithBeanSearcher(String viewId, String projectId);

  TestCase save(TestCaseSaveDto dto);

  TestCase update(TestCaseSaveDto dto);

  TestCase info(Long id);

  void clone(List<Long> ids);

  List<TestCase> listExtend(TestCaseParam tmpParam);

  List<TestCaseBisDto> getTestCaseAllByCycleId(Long testCycleId);

  /**
   * 根据CaseId、projectId查找
   *
   * @param projectId
   * @param testCaseId
   * @return
   */
  TestCase getByIdAndProjectId(Long projectId, Long testCaseId);

  List<TestCase> testCaseSearch(Long projectId, String title);

  Resp<Map> removeAndChild(Long id);

  TestCase queryByProjectIdAndExteranlId(Long projectId, String exteranlId);

    IssueStatusVo retrieveIssueStatusAsPerIssueId(Long projectId, Long issueId);

  TestCycle saveTestCycle(Long projectId, TestCycleSaveDto dto);

  /**
   * 通过字段名、字段值、scopeName、scopeId 查询测试用例，分页返回
   * @param fieldNameEn 字段名
   * @param value 字段值
   * @param scopeName 作用域名
   * @param scopeId 作用域ID
   * @param pageNum 页码（从1开始）
   * @param pageSize 每页数量
   * @return 分页结果
   */
  PageInfo<TestCase> queryByFieldAndValue(String fieldNameEn, String value, String scopeName, String scopeId, int pageNum, int pageSize);

}
