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

### 2. 请求参数
```json
{
    "projectId": 1874424342973054977,
    "versions": ["2.0.0.0", "2.1.0.0"],
    "startDate": "2025-01-01",  // 可选，开始日期，为空则不限制开始时间
    "endDate": "2025-01-31"     // 可选，结束日期，为空则不限制结束时间
}
```

#### 参数说明
- **projectId**: 项目ID（必填）
- **versions**: 功能版本号数组（必填，可以是1个或多个版本）
- **startDate**: 开始日期，格式YYYY-MM-DD（可选）
  - 为空或null：不限制开始时间，查询所有历史执行记录
  - 有值：只统计该日期之后的执行记录
- **endDate**: 结束日期，格式YYYY-MM-DD（可选）
  - 为空或null：不限制结束时间，查询到当前时间的所有执行记录
  - 有值：只统计该日期之前的执行记录

### 3. 返回结果设计
```json
{
    "versions": ["2.0.0.0", "2.1.0.0"],
    "totalPlannedCount": 150,
    "actualExecutedCount": 120,
    "executionRate": 80.0,
    "executionDetails": [
        {
            "testCaseId": 123,
            "testCaseTitle": "登录功能测试",
            "version": "2.0.0.0",
            "isExecuted": true,
            "lastExecutionTime": "2025-01-15 10:30:00",
            "executionCount": 3,
            "executionCycles": [
                {
                    "testCycleId": 1001,
                    "testCycleTitle": "开发测试周期",
                    "testCycleEnv": "dev",
                    "executionTime": "2025-01-13 09:00:00",
                    "executionStatus": 1,
                    "executionStatusText": "PASS"
                },
                {
                    "testCycleId": 1002,
                    "testCycleTitle": "集成测试周期", 
                    "testCycleEnv": "test",
                    "executionTime": "2025-01-14 14:30:00",
                    "executionStatus": 2,
                    "executionStatusText": "FAIL"
                },
                {
                    "testCycleId": 1003,
                    "testCycleTitle": "回归测试周期",
                    "testCycleEnv": "prod", 
                    "executionTime": "2025-01-15 10:30:00",
                    "executionStatus": 1,
                    "executionStatusText": "PASS"
                }
            ]
        }
    ]
}
```

### 4. 字段说明
- **testCycleId**: 测试周期ID
- **testCycleTitle**: 测试周期标题名称
- **testCycleEnv**: 测试周期环境（dev/test/prod等）
- **executionTime**: 在该周期中的执行时间
- **executionStatus**: 执行状态，详见下方状态码定义

### 5. 执行状态码定义 (run_status)
系统中的测试执行状态使用数字编码，具体定义如下：

| 状态码 | 状态名称 | 英文标识 | 说明 |
|--------|----------|----------|------|
| 0 | 无效/N/A | INVALID | 测试数据无效或不适用 |
| 1 | 通过 | PASS | 测试执行成功，结果符合预期 |
| 2 | 失败 | FAIL | 测试执行失败，发现缺陷或问题 |
| 3 | 跳过 | SKIP | 测试被主动跳过，未执行 |
| 4 | 阻塞 | BLOCKED | 测试被阻塞，无法继续执行 |
| 5 | 未执行 | NO_RUN | 测试尚未开始执行 |
| 6 | 未完成 | NOT_COMPLETED | 测试开始但未完成执行 |

### 6. 执行状态说明
- **计算执行率时的处理**: 只有状态码为 1(PASS)、2(FAIL)、3(SKIP)、4(BLOCKED) 的测试用例才被认为是"已执行"
- **未执行状态**: 状态码为 0(INVALID)、5(NO_RUN)、6(NOT_COMPLETED) 的测试用例被认为是"未执行"
- **去重原则**: 同一个测试用例在多个测试周期中执行时，以最新的执行状态为准

## 计算公式

**功能执行率 = (实际执行数 ÷ 计划数) × 100%**

### 术语说明
- **计划数(totalPlannedCount)**: 该功能版本下所有测试用例总数
- **实际执行数(actualExecutedCount)**: 该功能版本下已执行的测试用例数（去重）
- **去重**: 一个测试用例在多个测试周期中执行，只计算一次

## 核心计算逻辑

### 1. 计划数统计
```sql
SELECT COUNT(DISTINCT tc.id) as planned_count
FROM test_case tc
WHERE tc.version IN (#{versions})
  AND tc.project_id = #{projectId}
```

### 2. 实际执行数统计（去重）
```sql
SELECT COUNT(DISTINCT tcjtc.test_case_id) as executed_count
FROM test_cycle_join_test_case tcjtc
JOIN test_case tc ON tcjtc.test_case_id = tc.id
WHERE tc.version IN (#{versions})
  AND tc.project_id = #{projectId}
  AND tcjtc.run_status IN (1, 2, 3, 4)  -- 只统计已执行状态：PASS, FAIL, SKIP, BLOCKED
  <if test="startDate != null and startDate != ''">
    AND tcjtc.execution_time >= #{startDate}
  </if>
  <if test="endDate != null and endDate != ''">
    AND tcjtc.execution_time <= #{endDate}
  </if>
```

### 3. 详细执行历史查询（支持可选时间过滤）
```sql
SELECT 
    tc.id as testCaseId,
    tc.title as testCaseTitle,
    tc.version,
    COUNT(tcjtc.id) as executionCount,
    MAX(tcjtc.execution_time) as lastExecutionTime
FROM test_case tc
LEFT JOIN test_cycle_join_test_case tcjtc ON tc.id = tcjtc.test_case_id
    <if test="startDate != null and startDate != ''">
      AND tcjtc.execution_time >= #{startDate}
    </if>
    <if test="endDate != null and endDate != ''">
      AND tcjtc.execution_time <= #{endDate}
    </if>
WHERE tc.version IN (#{versions})
  AND tc.project_id = #{projectId}
GROUP BY tc.id, tc.title, tc.version
```

## 数据结构分析

### 核心表结构
1. **test_case表** - 存储测试用例，有version字段和project_id字段
2. **test_cycle表** - 存储测试周期信息
3. **test_cycle_join_test_case表** - 存储测试执行记录

### 关系说明
- Test Case → Execution: 一对多关系（一个用例可在多个周期执行）

## API设计

### 接口路径
- `POST /api/versionQualityReport/functionExecutionRate`

### 请求参数
```json
{
    "projectId": 1874424342973054977,
    "versions": ["2.0.0.0", "2.1.0.0"],
    "startDate": "2025-01-01",  // 可选参数
    "endDate": "2025-01-31"     // 可选参数
}
```

#### 可选参数示例
**不带时间限制的查询**:
```json
{
    "projectId": 1874424342973054977,
    "versions": ["2.0.0.0"]
}
```

**只有开始时间的查询**:
```json
{
    "projectId": 1874424342973054977,
    "versions": ["2.0.0.0"],
    "startDate": "2025-01-15"
}
```

**只有结束时间的查询**:
```json
{
    "projectId": 1874424342973054977,
    "versions": ["2.0.0.0"],
    "endDate": "2025-01-31"
}
```

### 响应格式

#### 成功响应格式
```json
{
  "success": true,
  "data": {
    "versions": ["2.0.0.0", "2.1.0.0"],
    "totalPlannedCount": 150,
    "actualExecutedCount": 120,
    "executionRate": 80.0,
    "queryConditions": {
      "projectId": 1874424342973054977,
      "versions": ["2.0.0.0", "2.1.0.0"],
      "startDate": "2025-01-01",
      "endDate": "2025-01-31"
    },
    "executionSummary": {
      "totalTestCases": 150,
      "executedTestCases": 120,
      "notExecutedTestCases": 30,
      "executionCycles": 5
    },
    "executionDetails": [
      {
        "testCaseId": 123,
        "testCaseTitle": "登录功能测试",
        "version": "2.0.0.0",
        "isExecuted": true,
        "lastExecutionTime": "2025-01-15 10:30:00",
        "executionCount": 3,
        "executionStatus": 1,
        "executionCycles": [
          {
            "testCycleId": 1001,
            "testCycleTitle": "开发测试周期",
            "testCycleEnv": "dev",
            "executionTime": "2025-01-13 09:00:00",
            "executionStatus": 1
          },
          {
            "testCycleId": 1002,
            "testCycleTitle": "集成测试周期", 
            "testCycleEnv": "test",
            "executionTime": "2025-01-14 14:30:00",
            "executionStatus": 2
          }
        ]
      },
      {
        "testCaseId": 124,
        "testCaseTitle": "密码重置功能",
        "version": "2.0.0.0",
        "isExecuted": false,
        "lastExecutionTime": null,
        "executionCount": 0,
        "executionStatus": 0,
        "executionCycles": []
      }
    ]
  }
}
```

#### 错误响应格式
```json
{
  "success": false,
  "code": "INVALID_PARAMETER",
  "message": "项目ID不能为空",
  "data": null
}
```

#### 无数据响应格式
```json
{
  "success": true,
  "data": {
    "versions": ["3.0.0.0"],
    "totalPlannedCount": 0,
    "actualExecutedCount": 0,
    "executionRate": 0.0,
    "queryConditions": {
      "projectId": 1874424342973054977,
      "versions": ["3.0.0.0"],
      "startDate": null,
      "endDate": null
    },
    "message": "未找到指定版本的测试用例"
  }
}
```

## 时间参数使用场景

### 1. 不传时间参数（startDate和endDate都为空）
**使用场景**: 查看功能版本的整体测试执行情况
- 统计所有历史执行记录
- 适用于整体质量评估和长期趋势分析

### 2. 只传startDate（endDate为空）
**使用场景**: 查看某个时间点之后的测试执行情况
- 统计指定日期之后到当前时间的执行记录
- 适用于版本发布后的回归测试分析

### 3. 只传endDate（startDate为空）
**使用场景**: 查看某个时间点之前的测试执行情况
- 统计从历史开始到指定日期的执行记录
- 适用于里程碑节点的质量分析

### 4. 同时传startDate和endDate
**使用场景**: 查看特定时间段的测试执行情况
- 统计指定时间窗口内的执行记录
- 适用于阶段性质量报告和周期性分析

## 业务价值

### 核心问题解答
- **"2.0.0.0新功能测试执行得怎么样？"**
- **"跨版本的功能测试完成度如何？"**
- **"哪些测试用例还没有执行？"**
- **"测试用例在不同环境的执行情况如何？"**

### 质量评级标准
- **优秀 (95%+)**: 测试执行非常充分，质量风险极低
- **良好 (85-94%)**: 测试执行较为充分，质量风险较低
- **一般 (70-84%)**: 测试执行基本达标，存在一定质量风险
- **较差 (<70%)**: 测试执行不足，存在较高质量风险

### 执行状态分布分析
除了整体执行率，还应关注执行状态的分布：
- **PASS比例**: 反映功能质量稳定性
- **FAIL比例**: 反映当前存在的问题数量
- **BLOCKED比例**: 反映测试环境或依赖问题
- **SKIP比例**: 反映测试策略的合理性

## 实现要点

### 1. 去重策略
- 使用Set集合自动去重
- SQL层面使用DISTINCT
- 记录详细的执行历史

### 2. 性能优化
- 合理使用索引
- 分步查询避免复杂JOIN
- 缓存常用查询结果

### 3. 数据完整性
- 处理空数据情况
- 提供详细的执行轨迹信息

### 4. 详细执行信息
- 提供每个测试用例的执行历史
- 记录测试周期环境信息
- 支持多周期执行状态追踪