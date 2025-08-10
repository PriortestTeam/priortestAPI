
# 版本缺陷逃逸率分析API文档

## 概述

版本缺陷逃逸率分析API用于分析指定版本引入的缺陷在该版本和后续版本中的发现情况，帮助评估版本质量和测试有效性。

## 核心概念

### 缺陷逃逸率
- **定义**: 在版本发布后才被发现的缺陷占该版本引入的总缺陷的比例
- **公式**: 逃逸率 = (后续版本发现的缺陷数 ÷ 该版本引入的总缺陷数) × 100%
- **业务价值**: 衡量版本测试质量，评估发布风险

### 字段说明
- `introduced_version`: 缺陷引入版本 - 缺陷是在哪个版本中被引入的
- `issue_version`: 缺陷发现版本 - 缺陷是在哪个版本测试中被发现的
- `issue_status`: 缺陷状态 - 0:待修复, 1:修复中, 2:已修复, 3:已关闭
- `is_legacy`: 是否为遗留缺陷 - 引入版本 ≠ 发现版本

### 分析逻辑
对于要分析的版本（如1.0.0.0）：
1. 查找所有 `introduced_version = "1.0.0.0"` 的缺陷
2. 按 `issue_version` 分组统计
3. 计算逃逸率和测试有效性
4. 提供详细缺陷列表和质量评估

## API接口

### 1. 分析版本缺陷逃逸率

**接口地址**: `POST /api/versionQualityReport/escapeAnalysis`

**接口描述**: 分析指定版本引入的缺陷在该版本和后续版本中的发现情况，计算缺陷逃逸率和测试有效性

#### 请求参数 (VersionEscapeAnalysisRequestDto)

| 参数名 | 类型 | 必填 | 描述 | 示例值 |
|--------|------|------|------|--------|
| projectId | String | 是 | 项目ID | "885958494765715456" |
| analysisVersion | String | 是 | 要分析的版本号（引入版本） | "1.0.0.0" |
| startDate | String | 否 | 分析时间范围开始日期 | "2024-01-01" |
| endDate | String | 否 | 分析时间范围结束日期 | "2024-12-31" |

#### 请求示例

```json
{
  "projectId": "885958494765715456",
  "analysisVersion": "1.0.0.0",
  "startDate": "2024-01-01",
  "endDate": "2024-12-31"
}
```

#### 响应参数 (VersionEscapeAnalysisResponseDto)

```json
{
  "code": "200",
  "msg": "调用成功。",
  "httpCode": 200,
  "data": {
    "analysisVersion": "1.0.0.0",
    "projectId": "885958494765715456",
    "analysisTimeRange": "2024-01-01 至 2024-12-31",
    "escapeRateStats": {
      "totalDefectsIntroduced": 10,
      "currentVersionFound": 7,
      "escapedDefects": 3,
      "escapeRate": 30.00,
      "detectionEffectiveness": 70.00,
      "qualityLevel": "需改进"
    },
    "defectDetails": [
      {
        "defectId": "12345",
        "title": "登录功能异常",
        "severity": "严重",
        "priority": "高",
        "introducedVersion": "1.0.0.0",
        "foundVersion": "1.1.0.0",
        "isEscaped": true,
        "escapeDays": 15,
        "fixStatus": "修复中",
        "impactDescription": "该缺陷在版本 1.0.0.0 中引入，但直到版本 1.1.0.0 才被发现，存在逃逸风险"
      },
      {
        "defectId": "12346",
        "title": "数据保存失败",
        "severity": "一般",
        "priority": "中",
        "introducedVersion": "1.0.0.0",
        "foundVersion": "1.0.0.0",
        "isEscaped": false,
        "escapeDays": 0,
        "fixStatus": "已修复",
        "impactDescription": "该缺陷在引入版本内被及时发现，未发生逃逸"
      }
    ],
    "qualityAssessment": {
      "overallQualityLevel": "需改进",
      "riskLevel": "高风险",
      "recommendations": [
        "紧急加强测试覆盖，重点关注回归测试",
        "建立缺陷预防机制，加强代码审查",
        "完善测试用例设计，增加边界条件测试"
      ],
      "keyMetrics": {
        "escapeRate": 30.00,
        "detectionEffectiveness": 70.00,
        "totalDefects": 10.00,
        "foundInVersion": 7.00,
        "escapedDefects": 3.00
      },
      "testCoverageAssessment": "测试覆盖一般，需要加强测试深度",
      "keyFindings": [
        "该版本共引入 10 个缺陷",
        "版本内发现 7 个缺陷，逃逸 3 个缺陷",
        "检测有效性为 70.0%，测试效果有待提升"
      ]
    },
    "discoveryTiming": null,
    "legacyDefectAnalysis": null,
    "versionGroups": [],
    "severityGroups": []
  }
}
```

### 2. 导出逃逸率分析报告

**接口地址**: `POST /api/versionQualityReport/escapeAnalysis/export`

**接口描述**: 导出详细的版本缺陷逃逸率分析报告

#### 请求参数
同 `POST /api/versionQualityReport/escapeAnalysis` 接口

#### 响应参数

```json
{
  "code": "200",
  "msg": "调用成功。",
  "data": "/tmp/escape_analysis_report_1641234567890.xlsx"
}
```

## 数据模型详解

### 逃逸率统计 (EscapeRateStats)

| 字段名 | 类型 | 描述 | 示例值 |
|--------|------|------|--------|
| totalDefectsIntroduced | Integer | 该版本引入的缺陷总数 | 10 |
| currentVersionFound | Integer | 在当前版本内发现的缺陷数 | 7 |
| escapedDefects | Integer | 逃逸到后续版本的缺陷数 | 3 |
| escapeRate | BigDecimal | 缺陷逃逸率（%） | 30.00 |
| detectionEffectiveness | BigDecimal | 检测有效性（%） | 70.00 |
| qualityLevel | String | 质量等级 | "需改进" |

### 缺陷详情 (EscapeDefectDetail)

| 字段名 | 类型 | 描述 | 示例值 |
|--------|------|------|--------|
| defectId | String | 缺陷ID | "12345" |
| title | String | 缺陷标题 | "登录功能异常" |
| severity | String | 严重程度 | "严重" |
| priority | String | 优先级 | "高" |
| introducedVersion | String | 引入版本 | "1.0.0.0" |
| foundVersion | String | 发现版本 | "1.1.0.0" |
| isEscaped | Boolean | 是否逃逸 | true |
| escapeDays | Integer | 逃逸天数 | 15 |
| fixStatus | String | 修复状态 | "修复中" |
| impactDescription | String | 影响描述 | "该缺陷在版本 1.0.0.0 中引入..." |

### 质量评估 (QualityAssessment)

| 字段名 | 类型 | 描述 | 示例值 |
|--------|------|------|--------|
| overallQualityLevel | String | 整体质量等级 | "需改进" |
| riskLevel | String | 风险等级 | "高风险" |
| recommendations | List<String> | 改进建议 | ["加强测试覆盖", "完善回归测试"] |
| keyMetrics | Map<String, BigDecimal> | 关键指标 | 各项指标数值 |
| testCoverageAssessment | String | 测试覆盖评估 | "测试覆盖一般，需要加强测试深度" |
| keyFindings | List<String> | 关键发现 | 分析结论列表 |

## 质量等级标准

| 逃逸率范围 | 质量等级 | 风险等级 | 描述 |
|------------|----------|----------|------|
| ≤ 5% | 优秀 | 低风险 | 测试覆盖充分，质量风险极低 |
| 5% - 15% | 良好 | 中等风险 | 测试质量较好，风险可控 |
| 15% - 30% | 一般 | 高风险 | 测试质量达标，需要关注 |
| > 30% | 需改进 | 极高风险 | 测试覆盖不足，存在质量风险 |

## 修复状态映射

| issue_status | 状态描述 | 说明 |
|--------------|----------|------|
| 0 | 待修复 | 缺陷已发现，等待修复 |
| 1 | 修复中 | 缺陷正在修复过程中 |
| 2 | 已修复 | 缺陷已修复完成 |
| 3 | 已关闭 | 缺陷已关闭（已修复或不是缺陷） |

## 业务场景示例

### 场景1：版本质量评估
- **目标**: 评估1.0.0.0版本的测试质量
- **操作**: 调用分析接口，查看逃逸率和质量等级
- **决策**: 根据逃逸率决定是否需要加强测试

### 场景2：缺陷根因分析
- **目标**: 分析高逃逸率的原因
- **操作**: 查看详细缺陷列表和严重程度分布
- **决策**: 针对性改进测试策略

### 场景3：质量改进跟踪
- **目标**: 跟踪版本间质量改进情况
- **操作**: 对比不同版本的逃逸率趋势
- **决策**: 评估质量改进措施效果

## 数据库查询逻辑

### 主要查询SQL
```sql
-- 查询版本逃逸统计
SELECT 
    COUNT(*) as totalDefectsIntroduced,
    SUM(CASE WHEN introduced_version = issue_version THEN 1 ELSE 0 END) as currentVersionFound,
    SUM(CASE WHEN introduced_version != issue_version THEN 1 ELSE 0 END) as escapedDefects
FROM issue 
WHERE project_id = #{projectId} 
AND introduced_version = #{analysisVersion}

-- 查询版本详细缺陷信息
SELECT 
    id,
    title,
    description,
    severity,
    issue_status,
    priority,
    introduced_version,
    issue_version,
    create_time
FROM issue 
WHERE project_id = #{projectId} 
AND introduced_version = #{analysisVersion}
```

## 注意事项

1. **版本关系**: 确保 `introduced_version` 和 `issue_version` 字段准确
2. **时间范围**: 建议设置合理的分析时间范围，避免数据过多
3. **权限控制**: 需要项目访问权限才能查看分析结果
4. **数据完整性**: 缺陷数据的完整性直接影响分析准确性
5. **状态映射**: issue_status字段为数字类型，需要正确映射到状态描述

## 错误码

| 错误码 | 描述 | 解决方案 |
|--------|------|----------|
| 400 | 请求参数错误 | 检查必填参数和参数格式 |
| 403 | 无项目访问权限 | 确认用户有项目查看权限 |
| 404 | 项目或版本不存在 | 确认项目ID和版本号正确 |
| 500 | 服务器内部错误 | 检查数据库连接和SQL语句 |

## 性能优化建议

1. **索引优化**: 在 `project_id`、`introduced_version`、`issue_version` 字段上建立索引
2. **数据分页**: 对于大量缺陷数据，建议使用分页查询
3. **缓存策略**: 对于相同参数的重复查询，可考虑缓存结果
4. **异步处理**: 对于复杂的分析报告导出，建议使用异步处理

## 版本历史

| 版本 | 更新内容 | 更新时间 |
|------|----------|----------|
| v1.0 | 基础逃逸率分析功能 | 2024-01-15 |
| v1.1 | 增加详细缺陷信息和质量评估 | 2024-02-10 |
| v1.2 | 优化SQL查询性能，修复状态映射问题 | 2024-08-10 |
