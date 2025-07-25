
# 功能执行率报表分析

## 概述

功能执行率是衡量指定功能版本测试执行完整程度的重要指标，通过计算已执行测试用例数与计划测试用例总数的比例来评估测试进度。

## 计算公式

**功能执行率 = (实际执行测试用例数 ÷ 计划测试用例总数) × 100%**

### 术语说明
- **计划测试用例总数**: 指定功能版本下所有测试用例的数量
- **实际执行测试用例数**: 已在测试周期中执行过的测试用例数量（去重）
- **去重逻辑**: 同一个测试用例在多个测试周期中执行，只计算一次

## 数据结构分析

### 核心表结构
1. **test_case表** - 存储测试用例基础信息
2. **test_cycle表** - 存储测试周期信息
3. **test_cycle_join_test_case表** - 存储测试执行记录

### 字段说明
- **test_case.version**: 测试用例所属功能版本
- **test_cycle_join_test_case.run_status**: 执行状态（0=未执行，1=通过，2=失败，3=阻塞）
- **test_cycle_join_test_case.update_time**: 执行时间

## 查询逻辑

### 步骤1：计算计划测试用例总数
```sql
SELECT COUNT(*) as totalPlannedCount
FROM test_case tc
WHERE tc.project_id = #{projectId}
AND tc.version IN (#{functionVersions})
```

### 步骤2：计算实际执行测试用例数（去重）
```sql
SELECT COUNT(DISTINCT tc.id) as actualExecutedCount
FROM test_case tc
INNER JOIN test_cycle_join_test_case tctc ON tc.id = tctc.test_case_id
WHERE tc.project_id = #{projectId}
AND tc.version IN (#{functionVersions})
AND tctc.run_status != 0  -- 排除未执行状态
<if test="startDate != null and endDate != null">
AND tctc.update_time BETWEEN #{startDate} AND #{endDate}
</if>
```

### 步骤3：查询执行详情
```sql
SELECT 
    tc.id as testCaseId,
    tc.title as testCaseTitle,
    tc.version,
    CASE WHEN tctc.test_case_id IS NOT NULL THEN true ELSE false END as isExecuted,
    MAX(tctc.update_time) as lastExecutionTime,
    COUNT(tctc.test_cycle_id) as executionCount,
    JSON_ARRAYAGG(
        CASE WHEN tctc.test_cycle_id IS NOT NULL THEN
            JSON_OBJECT(
                'testCycleId', tctc.test_cycle_id,
                'testCycleTitle', tcycle.title,
                'testCycleEnv', tcycle.env,
                'executionTime', tctc.update_time,
                'executionStatus', tctc.run_status
            )
        END
    ) as executionCycles
FROM test_case tc
LEFT JOIN test_cycle_join_test_case tctc ON tc.id = tctc.test_case_id 
    AND tctc.run_status != 0
LEFT JOIN test_cycle tcycle ON tctc.test_cycle_id = tcycle.id
WHERE tc.project_id = #{projectId}
AND tc.version IN (#{functionVersions})
<if test="startDate != null and endDate != null">
AND (tctc.update_time IS NULL OR tctc.update_time BETWEEN #{startDate} AND #{endDate})
</if>
GROUP BY tc.id, tc.title, tc.version
ORDER BY tc.id
```

## API设计

### 接口路径
`POST /api/versionQualityReport/functionExecutionRate`

### 请求参数
```json
{
    "projectId": 1874424342973054977,
    "functionVersions": ["2.0.0.0", "2.1.0.0"],
    "startDate": "2025-01-01",
    "endDate": "2025-01-31"
}
```

### 响应格式
```json
{
    "success": true,
    "data": {
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
}
```

## 实现要点

### 1. 去重逻辑
- 同一个测试用例在多个测试周期中执行，在 `actualExecutedCount` 中只计算一次
- 但在 `executionDetails` 中会显示所有执行历史

### 2. 执行状态过滤
- 只有 `run_status != 0` 的记录才被认为是"已执行"
- 状态含义：0=未执行，1=通过，2=失败，3=阻塞

### 3. 时间范围过滤
- 可选的 `startDate` 和 `endDate` 参数用于过滤执行时间范围
- 只影响执行记录的筛选，不影响测试用例总数的计算

### 4. 版本处理
- 支持查询多个功能版本的合并统计
- 测试用例通过 `version` 字段与功能版本关联

## 质量评级标准

- **优秀 (90%+)**: 测试执行非常完整
- **良好 (80-89%)**: 测试执行较为完整
- **一般 (70-79%)**: 测试执行基本达标
- **较差 (<70%)**: 测试执行不足，存在风险

## 报表展示

### 指标卡片
- 计划测试用例总数
- 实际执行测试用例数
- 执行率百分比
- 质量等级

### 详细分析
- 测试用例执行详情列表
- 跨周期执行历史
- 未执行测试用例列表
- 执行趋势分析

## 业务价值

1. **进度监控**: 实时了解测试执行进度
2. **质量保障**: 确保测试覆盖的完整性
3. **资源优化**: 识别执行效率瓶颈
4. **风险预警**: 及时发现执行不足的风险点
