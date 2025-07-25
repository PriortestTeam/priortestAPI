
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
    "functionVersions": ["2.0.0.0", "2.1.0.0"],
    "startDate": "2025-01-01",
    "endDate": "2025-01-31"
}
```

### 3. 返回结果设计（规范化命名）
```json
{
    "functionVersions": ["2.0.0.0", "2.1.0.0"],
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
                    "executionStatus": 1
                },
                {
                    "testCycleId": 1002,
                    "testCycleTitle": "集成测试周期", 
                    "testCycleEnv": "test",
                    "executionTime": "2025-01-14 14:30:00",
                    "executionStatus": 2
                },
                {
                    "testCycleId": 1003,
                    "testCycleTitle": "回归测试周期",
                    "testCycleEnv": "prod", 
                    "executionTime": "2025-01-15 10:30:00",
                    "executionStatus": 1
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
- **executionStatus**: 执行状态（0=未执行，1=通过，2=失败，3=阻塞）

## 计算公式

**功能执行率 = (实际执行数 ÷ 计划数) × 100%**

### 术语说明
- **计划数(totalPlannedCount)**: 该功能版本下所有测试用例总数
- **实际执行数(actualExecutedCount)**: 该功能版本下已执行的测试用例数（去重）
- **去重**: 一个测试用例在多个测试周期中执行，只计算一次

## Use Case与Feature版本关系

### 项目中的版本关系模式
在当前项目中，Use Case和Feature存在以下版本关系：

1. **Feature有版本**: Feature表中有`version`字段，表示Feature所属的功能版本
2. **Use Case有独立版本**: Use Case表中有`version`字段，表示Use Case自身的版本
3. **关联关系**: Use Case通过`feature_id`关联到Feature，但Use Case的版本独立于Feature版本
4. **版本可能不同**: 一个Feature（如v2.0.0）下的Use Case可能有不同版本（v2.0.0、v2.1.0等）

### 查询逻辑说明

#### 情况1：主版本Feature分析（始终执行）
**目的**: 获取指定主版本Feature下符合版本条件的Use Case

**查询步骤**：
1. 查询主版本的所有Feature（`feature.version = majorVersion`）
2. 查询这些Feature下的所有Use Case（通过`feature_id`关联）
3. 对Use Case进行版本过滤：
   - 版本 = 主版本 → 包含
   - 版本 ∈ includeVersions（如果有值）→ 包含
   - 其他版本 → 排除到`versionNotMatchedInfo`

**主版本Feature分析的场景处理**：

**1. 有Use Case的Feature**：
- 使用 `selectByFeatureIdsWithVersionFilter` 查询Feature下的Use Cases
- 支持版本过滤：主版本 + includeVersions中的版本
- 对于每个符合版本条件的Use Case，查询其关联的测试用例

**2. 无Use Case的Feature**：
- 当Feature下没有Use Case或Use Case版本都不匹配时
- 查询Feature直接关联的测试用例

**3. 版本不匹配信息**：
- 使用 `selectVersionNotMatchedByFeatureIds` 查询Feature下版本不匹配的Use Cases
- 这些Use Cases既不是主版本，也不在includeVersions中

#### 情况2：包含版本Use Case补充（当includeVersions有值时）
**目的**: 获取纯粹属于includeVersions但不属于主版本Feature的Use Case

**查询步骤**：
1. 查询指定版本的Use Case（`use_case.version IN includeVersions`）
2. 排除情况1中已统计的有效Use Case（避免重复）
3. 这样获得了纯粹属于includeVersions但不属于主版本Feature的Use Case

**逻辑优化说明**：
- 情况1现在已包含版本过滤逻辑，会检查Use Case版本是否匹配includeVersions
- 情况2专门处理增量Use Case，确保去重后的纯增量统计
- 最终逻辑简化为：主版本Feature分析（包含版本过滤）+ 包含版本Use Case补充（去重后的纯增量）

**设计保证**：
- 主版本Feature的完整覆盖分析
- Use Case版本的灵活过滤
- 版本不匹配情况的透明记录
- 支持后续去重逻辑，避免重复计算

## 数据结构分析

### 核心表结构
1. **feature表** - 存储主要功能特性，有version字段
2. **use_case表** - 存储功能用例，有独立的version字段和feature_id关联字段
3. **test_case表** - 存储测试用例
4. **test_cycle表** - 存储测试周期信息
5. **test_cycle_join_test_case表** - 存储测试执行记录

### 关系说明
- Feature → Use Case: 一对多关系（通过feature_id）
- Use Case → Test Case: 多对多关系（通过关联表或字段）
- Test Case → Execution: 一对多关系（一个用例可在多个周期执行）

## 核心计算逻辑

### 5. 核心计算逻辑
- **totalPlannedCount**: 从test_case表统计指定版本的测试用例总数
- **actualExecutedCount**: 从test_cycle_join_test_case表统计已执行的测试用例数（去重）
- **executionRate**: (actualExecutedCount ÷ totalPlannedCount) × 100%

### 6. 数据来源表
- **test_case**: 测试用例基础信息
- **test_cycle**: 测试周期信息（获取title和env）
- **test_cycle_join_test_case**: 测试执行记录（获取执行状态和时间）

### 1. 计划数统计
- 从`test_case`表统计指定功能版本的测试用例总数
- 需要考虑Feature和Use Case的版本匹配关系

### 2. 实际执行数统计（去重）
- 从`test_cycle_join_test_case`表统计已执行的测试用例
- **去重逻辑**: 同一个测试用例在多个测试周期中执行，只计算一次
- 使用`DISTINCT test_case_id`或Set去重

### 3. 执行率计算
```sql
执行率 = (actualExecutedCount ÷ totalPlannedCount) × 100%
```

## SQL查询示例

### 计划数查询
```sql
-- 情况1：主版本Feature下的符合条件Use Case
SELECT COUNT(DISTINCT tc.id) as planned_count
FROM test_case tc
JOIN use_case uc ON tc.use_case_id = uc.id
JOIN feature f ON uc.feature_id = f.id
WHERE f.project_id = #{projectId}
  AND f.version = #{majorVersion}
  AND (uc.version = #{majorVersion} 
       OR uc.version IN (#{includeVersions}))

-- 情况2：补充的includeVersions Use Case
SELECT COUNT(DISTINCT tc.id) as additional_count
FROM test_case tc
JOIN use_case uc ON tc.use_case_id = uc.id
WHERE uc.project_id = #{projectId}
  AND uc.version IN (#{includeVersions})
  AND uc.id NOT IN (已统计的Use Case IDs)
```

### 实际执行数查询（去重）
```sql
SELECT COUNT(DISTINCT tcjtc.test_case_id) as executed_count
FROM test_cycle_join_test_case tcjtc
WHERE tcjtc.test_case_id IN (计划的测试用例IDs)
  AND tcjtc.execution_time BETWEEN #{startDate} AND #{endDate}
```

### 详细执行历史查询
```sql
SELECT 
    tc.id as testCaseId,
    tc.title as testCaseTitle,
    uc.version,
    COUNT(tcjtc.id) as executionCount,
    MAX(tcjtc.execution_time) as lastExecutionTime
FROM test_case tc
LEFT JOIN use_case uc ON tc.use_case_id = uc.id
LEFT JOIN test_cycle_join_test_case tcjtc ON tc.id = tcjtc.test_case_id
WHERE tc.id IN (计划的测试用例IDs)
GROUP BY tc.id, tc.title, uc.version
```

## 业务价值

### 核心问题解答
- **"2.0.0.0新功能本身测试得怎么样？"**
- **"跨版本的功能测试完成度如何？"**
- **"哪些测试用例还没有执行？"**
- **"测试用例在不同环境的执行情况如何？"**

### 质量评级标准
- **优秀 (95%+)**: 测试执行非常充分
- **良好 (85-94%)**: 测试执行较为充分
- **一般 (70-84%)**: 测试执行基本达标
- **较差 (<70%)**: 测试执行不足，存在风险

## 实现要点

### 1. 版本匹配逻辑
- Feature版本作为主要筛选条件
- Use Case版本进行二次过滤
- 支持多版本组合查询

### 2. 去重策略
- 使用Set集合自动去重
- SQL层面使用DISTINCT
- 记录详细的执行历史

### 3. 性能优化
- 合理使用索引
- 分步查询避免复杂JOIN
- 缓存常用查询结果

### 4. 数据完整性
- 处理空数据情况
- 版本不匹配的Use Case记录到versionNotMatchedInfo
- 提供详细的执行轨迹信息

### 5. 详细执行信息
- 提供每个测试用例的执行历史
- 记录测试周期环境信息
- 支持多周期执行状态追踪
