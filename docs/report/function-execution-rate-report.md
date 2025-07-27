# 功能执行率报表分析

## 概述

功能执行率是衡量指定功能版本测试完成度的重要指标，通过计算已执行测试用例数与计划测试用例数的比例来评估测试进度。此报表帮助团队了解特定功能版本的测试覆盖情况。

## 完整需求规范

### 1. 接口功能
查询指定功能版本的测试执行率，包括：
- 计划测试用例总数统计
- 实际执行测试用例数统计（去重）
- 执行率计算
- 详细的执行历史信息
- 执行状态分类统计
- 测试周期执行详情

### 2. 请求参数
```json
{
    "projectId": 885958494765715456,
    "majorVersion": "1.0.0.0",
    "includeVersions": ["1.0.0.0"],
    "testCycleIds": [1001, 1002, 1003]
}
```

#### 参数说明
- **projectId**: 项目ID（必填）
- **majorVersion**: 主版本号，用于过滤测试用例版本（必填）
- **includeVersions**: 包含的版本列表，用于过滤测试周期版本（必填）
- **testCycleIds**: 测试周期ID数组（可选）
  - 为空或null：统计所有测试周期中的执行记录
  - 有值：只统计指定测试周期中的执行记录，支持多个周期

### 3. 返回结果设计
```json
{
    "versions": ["1.0.0.0"],
    "totalPlannedCount": 150,
    "actualExecutedCount": 120,
    "executionRate": 80.0,
    "queryConditions": {
        "projectId": 885958494765715456,
        "majorVersion": "1.0.0.0",
        "includeVersions": ["1.0.0.0"],
        "testCycleIds": [1001, 1002, 1003]
    },
    "executionSummary": {
        "passCount": 45,
        "failCount": 25,
        "blockedCount": 30,
        "skippedCount": 10,
        "notExecutedCount": 40
    },
    "cycleExecutionDetails": [
        {
            "testCaseId": 123,
            "testCaseTitle": "登录功能测试",
            "version": "1.0.0.0",
            "testCycleId": 1001,
            "testCycleTitle": "测试周期-Win",
            "testCycleEnv": "dev",
            "testCycleVersion": "1.0.0.0",
            "executionTime": "2025-01-15 10:30:00",
            "executionStatus": 1,
            "runCount": 3,
            "runCaseId": 5001
        }
    ]
}
```

### 4. 执行状态码定义 (run_status)
系统中的测试执行状态使用数字编码，具体定义如下：

| 状态码 | 状态名称 | 英文标识 | 分类 | 说明 |
|--------|----------|----------|------|------|
| 1 | 通过 | PASS | EXECUTED | 测试执行成功，结果符合预期 |
| 2 | 失败 | FAIL | EXECUTED | 测试执行失败，发现缺陷或问题 |
| 3 | 跳过 | SKIP | EXECUTED | 测试被主动跳过，未执行 |
| 4 | 阻塞 | BLOCKED | EXECUTED | 测试被阻塞，无法继续执行 |
| 5 | 未执行 | NO_RUN | NOT_EXECUTED | 测试尚未开始执行 |
| 6 | 未完成 | NOT_COMPLETED | NOT_EXECUTED | 测试开始但未完成执行 |

#### 执行状态分类逻辑
- **已执行状态**: 状态码为 1(PASS)、2(FAIL)、3(SKIP)、4(BLOCKED) 的测试用例被认为是"已执行"
- **未执行状态**: 状态码为 5(NO_RUN)、6(NOT_COMPLETED) 或 null 的测试用例被认为是"未执行"

## 核心实现逻辑

### 1. 计划数统计逻辑
**目标**: 统计指定项目和主版本下的测试用例总数

**SQL实现**:
```sql
SELECT COUNT(*)
FROM test_case
WHERE project_id = #{projectId}
  AND version = #{majorVersion}
```

**Java实现**:
```java
Integer totalPlannedCount = testCaseDao.countPlannedTestCasesByVersions(projectId, majorVersionStr);
```

### 2. 已执行数统计逻辑（去重）
**目标**: 统计已执行的测试用例数，同一用例在多个周期中执行只计算一次

**SQL实现**:
```sql
SELECT COUNT(DISTINCT tc.id)
FROM test_case tc
JOIN test_cycle_join_test_case tcjtc ON tc.id = tcjtc.test_case_id
JOIN test_cycle tcycle ON tcjtc.test_cycle_id = tcycle.id
WHERE tc.project_id = #{projectId}
AND tc.version = #{majorVersion}
AND tcycle.version IN ('1.0.0.0', '1.1.0.0', ...)
AND tcjtc.run_status IN (1, 2, 3, 4)  -- 只统计已执行状态
[AND tcjtc.test_cycle_id IN (1001, 1002, ...)] -- 可选的周期过滤
```

**关键点**:
- 使用 `DISTINCT tc.id` 确保同一用例只计算一次
- 必须 JOIN `test_cycle` 表进行版本过滤
- 使用 `INNER JOIN` 而不是 `LEFT JOIN` 避免空值问题
- 只统计 `run_status IN (1, 2, 3, 4)` 的已执行记录

### 3. 执行率计算逻辑

**计算公式**:
```java
BigDecimal executionRate = BigDecimal.ZERO;
if (totalPlannedCount != null && totalPlannedCount > 0) {
    executionRate = new BigDecimal(actualExecutedCount)
        .divide(new BigDecimal(totalPlannedCount), 2, RoundingMode.HALF_UP)
        .multiply(new BigDecimal(100));
}
```

**关键点**:
- 避免除零错误
- 使用 `BigDecimal` 确保精度
- `HALF_UP` 舍入模式
- 保留2位小数

### 4. 详细执行历史查询逻辑

**目标**: 获取所有测试用例的执行详情，包括未执行的用例

**SQL实现**:
```sql
SELECT 
    tc.id as testCaseId,
    tc.title as testCaseTitle,
    tc.version as version,
    tcycle.id as testCycleId,
    tcycle.title as testCycleTitle,
    tcycle.env as testCycleEnv,
    tcycle.version as testCycleVersion,
    tcjtc.update_time as executionTime,
    tcjtc.run_status as executionStatus,
    tcjtc.run_count as runCount,
    tcjtc.id as runCaseId
FROM test_case tc
LEFT JOIN test_cycle_join_test_case tcjtc ON tc.id = tcjtc.test_case_id
INNER JOIN test_cycle tcycle ON tcjtc.test_cycle_id = tcycle.id 
WHERE tc.project_id = #{projectId}
  AND tc.version = #{majorVersion}
  AND tcycle.version IN ('1.0.0.0', '1.1.0.0', ...)
  [AND tcjtc.test_cycle_id IN (1001, 1002, ...)] -- 可选
ORDER BY tc.id, tcjtc.update_time DESC
```

**关键修复**:
- 使用 `INNER JOIN test_cycle` 而不是 `LEFT JOIN`
- 将版本条件放在 `WHERE` 子句中，不在 `ON` 子句中
- 这样避免了返回不匹配版本的NULL记录

### 5. 执行状态分类统计逻辑

**目标**: 统计各种执行状态的数量

**Java实现**:
```java
private ExecutionSummaryDto buildExecutionSummary(List<Map<String, Object>> executionDetailMaps) {
    ExecutionSummaryDto summary = new ExecutionSummaryDto();

    // 按状态分组统计
    Map<StatusCategory, Long> statusCounts = executionDetailMaps.stream()
        .collect(Collectors.groupingBy(
            map -> getStatusCategory(map.get("executionStatus")),
            Collectors.counting()
        ));

    // 设置各状态数量
    summary.setPassCount(statusCounts.getOrDefault(StatusCategory.PASS, 0L).intValue());
    summary.setFailCount(statusCounts.getOrDefault(StatusCategory.FAIL, 0L).intValue());
    summary.setBlockedCount(statusCounts.getOrDefault(StatusCategory.BLOCKED, 0L).intValue());
    summary.setSkippedCount(statusCounts.getOrDefault(StatusCategory.SKIP, 0L).intValue());
    summary.setNotExecutedCount(statusCounts.getOrDefault(StatusCategory.NOT_EXECUTED, 0L).intValue());

    return summary;
}
```

**状态分类映射**:
```java
private StatusCategory getStatusCategory(Object statusObj) {
    if (statusObj == null) {
        return StatusCategory.NOT_EXECUTED;
    }

    Integer status;
    if (statusObj instanceof BigInteger) {
        status = ((BigInteger) statusObj).intValue();
    } else if (statusObj instanceof Integer) {
        status = (Integer) statusObj;
    } else {
        return StatusCategory.NOT_EXECUTED;
    }

    switch (status) {
        case 1: return StatusCategory.PASS;
        case 2: return StatusCategory.FAIL;
        case 3: return StatusCategory.SKIP;
        case 4: return StatusCategory.BLOCKED;
        case 5:
        case 6:
        default: return StatusCategory.NOT_EXECUTED;
    }
}
```

### 6. 测试周期执行详情构建逻辑

**目标**: 构建按测试用例分组的执行详情列表

**Java实现**:
```java
private List<CycleExecutionDetailDto> buildCycleExecutionDetails(List<Map<String, Object>> executionDetailMaps) {
    return executionDetailMaps.stream()
        .map(this::mapToCycleExecutionDetail)
        .collect(Collectors.toList());
}

private CycleExecutionDetailDto mapToCycleExecutionDetail(Map<String, Object> map) {
    CycleExecutionDetailDto detail = new CycleExecutionDetailDto();

    // 基本信息映射
    detail.setTestCaseId(getLongValue(map.get("testCaseId")));
    detail.setTestCaseTitle(getStringValue(map.get("testCaseTitle")));
    detail.setVersion(getStringValue(map.get("version")));

    // 测试周期信息
    detail.setTestCycleId(getLongValue(map.get("testCycleId")));
    detail.setTestCycleTitle(getStringValue(map.get("testCycleTitle")));
    detail.setTestCycleEnv(getStringValue(map.get("testCycleEnv")));
    detail.setTestCycleVersion(getStringValue(map.get("testCycleVersion")));

    // 执行信息
    detail.setExecutionTime(getStringValue(map.get("executionTime")));
    detail.setExecutionStatus(getIntegerValue(map.get("executionStatus")));
    detail.setRunCount(getIntegerValue(map.get("runCount")));
    detail.setRunCaseId(getLongValue(map.get("runCaseId")));

    return detail;
}
```

### 7. 数据类型转换处理

**问题**: MyBatis查询结果中的数值可能是 `BigInteger` 类型

**解决方案**:
```java
private Long getLongValue(Object obj) {
    if (obj == null) return null;
    if (obj instanceof BigInteger) {
        return ((BigInteger) obj).longValue();
    } else if (obj instanceof Long) {
        return (Long) obj;
    } else if (obj instanceof Integer) {
        return ((Integer) obj).longValue();
    }
    return null;
}

private Integer getIntegerValue(Object obj) {
    if (obj == null) return null;
    if (obj instanceof BigInteger) {
        return ((BigInteger) obj).intValue();
    } else if (obj instanceof Integer) {
        return (Integer) obj;
    } else if (obj instanceof Long) {
        return ((Long) obj).intValue();
    }
    return null;
}

private String getStringValue(Object obj) {
    return obj != null ? obj.toString() : null;
}
```

## 核心SQL查询分析

### 1. 修复前的问题SQL
```sql
-- 问题SQL：LEFT JOIN + 条件在ON子句中
LEFT JOIN test_cycle tcycle ON tcjtc.test_cycle_id = tcycle.id 
    AND tcycle.version IN ('1.0.0.0')
```

**问题**:
- `LEFT JOIN` 会返回左表所有记录，即使右表不匹配
- 条件在 `ON` 子句中只影响JOIN结果，不过滤最终结果
- 导致返回版本不匹配的NULL记录

### 2. 修复后的正确SQL
```sql
-- 正确SQL：INNER JOIN + 条件在WHERE子句中
INNER JOIN test_cycle tcycle ON tcjtc.test_cycle_id = tcycle.id 
WHERE tc.project_id = #{projectId}
  AND tc.version = #{majorVersion}
  AND tcycle.version IN ('1.0.0.0')
```

**优势**:
- `INNER JOIN` 只返回两表都匹配的记录
- 条件在 `WHERE` 子句中进行最终过滤
- 避免NULL值和错误统计

### 3. 完整的查询执行详情SQL
```xml
<select id="queryExecutionDetails" resultType="java.util.Map">
    SELECT 
        tc.id as testCaseId,
        tc.title as testCaseTitle,
        tc.version as version,
        tcycle.id as testCycleId,
        tcycle.title as testCycleTitle,
        tcycle.env as testCycleEnv,
        tcycle.version as testCycleVersion,
        tcjtc.update_time as executionTime,
        tcjtc.run_status as executionStatus,
        tcjtc.run_count as runCount,
        tcjtc.id as runCaseId
    FROM test_case tc
    LEFT JOIN test_cycle_join_test_case tcjtc ON tc.id = tcjtc.test_case_id
    INNER JOIN test_cycle tcycle ON tcjtc.test_cycle_id = tcycle.id 
    WHERE tc.project_id = #{projectId}
      AND tc.version = #{majorVersion}
      AND tcycle.version IN 
      <foreach collection="includeVersions" item="version" open="(" close=")" separator=",">
          #{version}
      </foreach>
      <if test="testCycleIds != null and testCycleIds.size() > 0">
        AND tcjtc.test_cycle_id IN 
        <foreach collection="testCycleIds" item="testCycleId" open="(" close=")" separator=",">
            #{testCycleId}
        </foreach>
      </if>
    ORDER BY tc.id, tcjtc.update_time DESC
</select>
```

## 业务场景与数据流

### 1. 典型查询场景

**场景1: 查询项目整体执行情况**
```json
{
    "projectId": 885958494765715456,
    "majorVersion": "1.0.0.0",
    "includeVersions": ["1.0.0.0"]
}
```

**场景2: 查询特定测试周期执行情况**
```json
{
    "projectId": 885958494765715456,
    "majorVersion": "1.0.0.0",
    "includeVersions": ["1.0.0.0"],
    "testCycleIds": [1001, 1002]
}
```

### 2. 数据流处理过程

1. **参数验证** → 检查必填字段
2. **计划数查询** → 统计测试用例总数
3. **已执行数查询** → 统计去重后的执行数
4. **执行率计算** → 计算百分比
5. **详情查询** → 获取执行历史
6. **状态统计** → 分类统计各状态数量
7. **详情构建** → 构建响应对象
8. **结果返回** → 返回完整报表

### 3. 去重逻辑说明

**问题**: 同一测试用例可能在多个测试周期中执行
**解决**: 使用 `COUNT(DISTINCT tc.id)` 确保同一用例只计算一次
**场景**:
- 用例A在"开发测试"周期执行 → 状态为PASS
- 用例A在"集成测试"周期执行 → 状态为FAIL
- 最终统计: 用例A算作1个已执行用例

## 性能优化建议

### 1. 数据库索引
```sql
-- 建议创建的索引
CREATE INDEX idx_test_case_project_version ON test_case(project_id, version);
CREATE INDEX idx_tcjtc_case_cycle ON test_cycle_join_test_case(test_case_id, test_cycle_id);
CREATE INDEX idx_tcjtc_status ON test_cycle_join_test_case(run_status);
CREATE INDEX idx_test_cycle_version ON test_cycle(version);
```

### 2. 查询优化
- 使用合适的JOIN类型
- 避免不必要的LEFT JOIN
- 合理使用WHERE条件过滤
- 分页查询大数据量

### 3. 缓存策略
- 对于不经常变化的版本数据可以使用缓存
- 缓存计划数统计结果
- 定期刷新缓存

## 错误处理与边界情况

### 1. 空数据处理
```java
// 处理空的计划数
Integer totalPlannedCount = testCaseDao.countPlannedTestCasesByVersions(projectId, majorVersionStr);
if (totalPlannedCount == null) {
    totalPlannedCount = 0;
}

// 处理空的执行数
Integer actualExecutedCount = testCaseDao.countExecutedTestCasesByVersionsAndCycles(...);
if (actualExecutedCount == null) {
    actualExecutedCount = 0;
}
```

### 2. 除零错误处理
```java
BigDecimal executionRate = BigDecimal.ZERO;
if (totalPlannedCount != null && totalPlannedCount > 0) {
    executionRate = new BigDecimal(actualExecutedCount)
        .divide(new BigDecimal(totalPlannedCount), 2, RoundingMode.HALF_UP)
        .multiply(new BigDecimal(100));
}
```

### 3. 数据类型转换异常处理
```java
private Integer getIntegerValue(Object obj) {
    if (obj == null) return null;
    try {
        if (obj instanceof BigInteger) {
            return ((BigInteger) obj).intValue();
        } else if (obj instanceof Integer) {
            return (Integer) obj;
        } else if (obj instanceof Long) {
            return ((Long) obj).intValue();
        }
    } catch (Exception e) {
        logger.warn("转换整数值失败: {}", obj, e);
    }
    return null;
}
```

## API接口规范

### 1. 接口路径
```
POST /versionQualityReport/functionExecutionRate
```

### 2. 请求头
```
Content-Type: application/json
Authorization: Bearer <token>
```

### 3. 成功响应格式
```json
{
    "success": true,
    "code": 200,
    "message": "查询成功",
    "data": {
        "versions": ["1.0.0.0"],
        "totalPlannedCount": 150,
        "actualExecutedCount": 120,
        "executionRate": 80.00,
        "queryConditions": {...},
        "executionSummary": {...},
        "cycleExecutionDetails": [...]
    }
}
```

### 4. 错误响应格式
```json
{
    "success": false,
    "code": 400,
    "message": "项目ID不能为空",
    "data": null
}
```

## 日志记录规范

### 1. 关键节点日志
```java
logger.info("查询参数 - 项目ID：{}，主版本：{}，包含版本：{}，测试周期：{}", 
           projectId, majorVersionStr, includeVersions, testCycleIds);

logger.info("SQL查询计划数 - 使用版本：{}，结果：{}", majorVersionStr, totalPlannedCount);

logger.info("SQL查询执行数 - 参数：版本={}，周期={}，结果：{}", 
           majorVersionStr, testCycleIds, actualExecutedCount);

logger.info("最终响应数据 - 计划：{}，执行：{}，执行率：{}%，周期详情数量：{}", 
           totalPlannedCount, actualExecutedCount, executionRate, cycleDetailsSize);
```

### 2. 异常日志
```java
try {
    // 业务逻辑
} catch (Exception e) {
    logger.error("获取功能执行率报表失败，项目ID：{}，版本：{}，原因：{}", 
                projectId, majorVersionStr, e.getMessage(), e);
    throw new RuntimeException("查询功能执行率失败", e);
}
```

## 测试验证方案

### 1. 单元测试用例
- 测试计划数统计正确性
- 测试执行数去重逻辑
- 测试执行率计算精度
- 测试状态分类统计
- 测试边界条件处理

### 2. 集成测试用例
- 测试完整查询流程
- 测试不同参数组合
- 测试大数据量性能
- 测试并发访问

### 3. 数据验证
- 手工验证统计数据准确性
- 对比数据库直接查询结果
- 验证去重逻辑有效性

## 质量评级标准

### 1. 执行率评级
- **优秀 (≥95%)**: 测试执行非常充分，质量风险极低
- **良好 (85-94%)**: 测试执行较为充分，质量风险较低  
- **一般 (70-84%)**: 测试执行基本达标，存在一定质量风险
- **较差 (<70%)**: 测试执行不足，存在较高质量风险

### 2. 状态分布分析
- **PASS比例**: 反映功能质量稳定性
- **FAIL比例**: 反映当前存在的问题数量
- **BLOCKED比例**: 反映测试环境或依赖问题
- **SKIP比例**: 反映测试策略的合理性
- **未执行比例**: 反映测试覆盖完整性

## 实现要点总结

### 1. 核心技术点
- MyBatis动态SQL构建
- 数据类型安全转换
- BigDecimal精确计算
- 流式数据处理
- 异常安全处理

### 2. 关键业务逻辑
- 测试用例去重统计
- 执行状态分类映射
- 版本过滤条件处理
- 测试周期可选过滤

### 3. 数据完整性保证
- NULL值安全处理
- 除零错误避免
- 类型转换异常处理
- 边界条件验证

通过以上完整的实现逻辑和技术细节，确保功能执行率报表的准确性、性能和可维护性。