
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
- `is_legacy`: 是否为遗留缺陷 - 引入版本 ≠ 发现版本
- `found_after_release`: 是否发布后发现

### 分析逻辑
对于要分析的版本（如0.9.0.0）：
1. 查找所有 `introduced_version = "0.9.0.0"` 的缺陷
2. 按 `issue_version` 分组统计
3. 计算逃逸率和测试有效性

## API接口

### 1. 分析版本缺陷逃逸率

**接口地址**: `POST /api/versionEscapeAnalysis/analyze`

**接口描述**: 分析指定版本引入的缺陷在该版本和后续版本中的发现情况，计算缺陷逃逸率和测试有效性

#### 请求参数 (VersionEscapeAnalysisRequestDto)

| 参数名 | 类型 | 必填 | 描述 | 示例值 |
|--------|------|------|------|--------|
| projectId | String | 是 | 项目ID | "1874424342973054977" |
| analysisVersion | String | 是 | 要分析的版本号（引入版本） | "0.9.0.0" |
| startDate | String | 否 | 分析时间范围开始日期 | "2024-01-01" |
| endDate | String | 否 | 分析时间范围结束日期 | "2024-12-31" |
| includeLegacyAnalysis | Boolean | 否 | 是否包含遗留缺陷分析 | true |
| groupBySeverity | Boolean | 否 | 是否按严重程度分组 | true |
| groupByFoundVersion | Boolean | 否 | 是否按发现版本分组 | true |
| statusFilter | List<String> | 否 | 缺陷状态过滤列表 | ["OPEN", "RESOLVED"] |
| severityFilter | List<String> | 否 | 严重程度过滤列表 | ["HIGH", "MEDIUM"] |
| includeDetailedDefects | Boolean | 否 | 是否包含详细缺陷列表 | true |

#### 响应参数 (VersionEscapeAnalysisResponseDto)

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "analysisVersion": "0.9.0.0",
    "projectId": "1874424342973054977",
    "analysisTimeRange": "2024-01-01 ~ 2024-12-31",
    "escapeRateStats": {
      "totalDefectsIntroduced": 20,
      "currentVersionFound": 12,
      "escapedDefects": 8,
      "escapeRate": 40.00,
      "detectionEffectiveness": 60.00,
      "qualityLevel": "需改进"
    },
    "discoveryTiming": {
      "inVersionCount": 12,
      "inVersionPercentage": 60.00,
      "escapedCount": 8,
      "escapedPercentage": 40.00,
      "description": "版本内发现12个缺陷，逃逸8个缺陷"
    },
    "legacyDefectAnalysis": {
      "totalLegacyDefects": 8,
      "legacyDefectRate": 40.00,
      "preReleaseLegacyFound": 3,
      "postReleaseLegacyFound": 5,
      "legacyEscapeRate": 62.50,
      "sourceVersions": [
        {
          "sourceVersion": "0.8.0.0",
          "count": 5,
          "impactDescription": "0.8.0.0版本遗留5个缺陷到当前版本"
        }
      ]
    },
    "versionGroups": [
      {
        "foundVersion": "0.9.0.0",
        "count": 12,
        "escapeDays": 0,
        "severityDistribution": {
          "HIGH": 3,
          "MEDIUM": 6,
          "LOW": 3
        },
        "isEscaped": false,
        "impactDescription": "版本内发现，测试有效"
      },
      {
        "foundVersion": "1.0.0.0",
        "count": 5,
        "escapeDays": 30,
        "severityDistribution": {
          "HIGH": 2,
          "MEDIUM": 2,
          "LOW": 1
        },
        "isEscaped": true,
        "impactDescription": "逃逸到1.0.0.0版本才发现，质量风险"
      }
    ],
    "severityGroups": [
      {
        "severity": "HIGH",
        "totalCount": 5,
        "inVersionCount": 3,
        "escapedCount": 2,
        "escapeRate": 40.00,
        "riskLevel": "高风险"
      }
    ],
    "defectDetails": [
      {
        "defectId": "BUG001",
        "title": "登录页面响应缓慢",
        "severity": "HIGH",
        "priority": "HIGH",
        "introducedVersion": "0.9.0.0",
        "foundVersion": "1.0.0.0",
        "foundAfterRelease": true,
        "isLegacy": true,
        "escapeDays": 30,
        "status": "OPEN",
        "description": "逃逸缺陷，发布后30天才发现"
      }
    ],
    "qualityAssessment": {
      "overallQualityLevel": "需改进",
      "riskLevel": "高风险",
      "recommendations": [
        "加强高严重程度缺陷的测试覆盖",
        "完善发布前的回归测试",
        "建立缺陷预防机制"
      ],
      "keyMetrics": {
        "escapeRate": 40.00,
        "highSeverityEscapeRate": 40.00,
        "legacyDefectRate": 40.00
      }
    }
  }
}
```

### 2. 快速分析版本逃逸率

**接口地址**: `GET /api/versionEscapeAnalysis/quick/{projectId}/{analysisVersion}`

**接口描述**: 通过URL参数快速分析指定版本的缺陷逃逸率

#### 请求参数

| 参数名 | 类型 | 位置 | 必填 | 描述 |
|--------|------|------|------|------|
| projectId | String | Path | 是 | 项目ID |
| analysisVersion | String | Path | 是 | 要分析的版本号 |
| includeLegacy | Boolean | Query | 否 | 是否包含遗留缺陷分析，默认true |
| groupBySeverity | Boolean | Query | 否 | 是否按严重程度分组，默认true |

#### 响应参数
同 `POST /api/versionEscapeAnalysis/analyze` 接口

### 3. 获取逃逸率趋势

**接口地址**: `GET /api/versionEscapeAnalysis/trend/{projectId}`

**接口描述**: 获取多个版本的逃逸率趋势对比数据

#### 请求参数

| 参数名 | 类型 | 位置 | 必填 | 描述 |
|--------|------|------|------|------|
| projectId | String | Path | 是 | 项目ID |
| versions | List<String> | Query | 是 | 版本列表，用逗号分隔 |

#### 响应示例

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "trendData": [
      {
        "version": "0.8.0.0",
        "escapeRate": 35.5,
        "qualityLevel": "需改进",
        "totalDefects": 18,
        "escapedDefects": 6
      },
      {
        "version": "0.9.0.0",
        "escapeRate": 40.0,
        "qualityLevel": "需改进",
        "totalDefects": 20,
        "escapedDefects": 8
      },
      {
        "version": "1.0.0.0",
        "escapeRate": 25.0,
        "qualityLevel": "一般",
        "totalDefects": 16,
        "escapedDefects": 4
      }
    ],
    "summary": {
      "averageEscapeRate": 33.5,
      "trend": "波动",
      "bestVersion": "1.0.0.0",
      "worstVersion": "0.9.0.0"
    }
  }
}
```

### 4. 导出逃逸率分析报告

**接口地址**: `POST /api/versionEscapeAnalysis/export`

**接口描述**: 导出详细的版本缺陷逃逸率分析报告

#### 请求参数
同 `POST /api/versionEscapeAnalysis/analyze` 接口

#### 响应参数

```json
{
  "code": 200,
  "msg": "success",
  "data": "report_download_url_or_file_path"
}
```

## 质量等级标准

| 逃逸率范围 | 质量等级 | 描述 |
|------------|----------|------|
| < 10% | 优秀 | 测试覆盖充分，质量风险极低 |
| 10% - 20% | 良好 | 测试质量较好，风险可控 |
| 20% - 30% | 一般 | 测试质量达标，需要关注 |
| > 30% | 需改进 | 测试覆盖不足，存在质量风险 |

## 业务场景示例

### 场景1：版本质量评估
- **目标**: 评估0.9.0.0版本的测试质量
- **操作**: 调用分析接口，查看逃逸率和质量等级
- **决策**: 根据逃逸率决定是否需要加强测试

### 场景2：版本对比分析
- **目标**: 对比最近几个版本的质量趋势
- **操作**: 调用趋势接口，获取多版本对比数据
- **决策**: 识别质量改进或恶化趋势

### 场景3：缺陷根因分析
- **目标**: 分析高逃逸率的原因
- **操作**: 查看详细缺陷列表和严重程度分布
- **决策**: 针对性改进测试策略

## 注意事项

1. **版本关系**: 确保 `introduced_version` 和 `issue_version` 字段准确
2. **时间范围**: 建议设置合理的分析时间范围，避免数据过多
3. **权限控制**: 需要项目访问权限才能查看分析结果
4. **数据完整性**: 缺陷数据的完整性直接影响分析准确性

## 错误码

| 错误码 | 描述 | 解决方案 |
|--------|------|----------|
| 400 | 请求参数错误 | 检查必填参数和参数格式 |
| 403 | 无项目访问权限 | 确认用户有项目查看权限 |
| 404 | 项目或版本不存在 | 确认项目ID和版本号正确 |
| 500 | 服务器内部错误 | 联系技术支持 |
