
# 故事覆盖率报表分析

## 概述

故事覆盖率是衡量功能需求测试覆盖完整程度的重要指标，通过计算已覆盖故事数与总故事数的比例来评估测试质量。此报表帮助团队了解需求覆盖的完整性，确保所有功能点都有相应的测试验证。

## 计算公式

**故事覆盖率 = (已覆盖故事数 ÷ 总故事数) × 100%**

### 术语说明
- **故事(User Story)**：功能需求的最小单位
- **已覆盖故事数**：有测试用例验证的功能故事数量
- **总故事数**：本版本计划实现的所有功能故事数量

## 数据结构分析

### 核心表结构
1. **feature表** - 存储主要功能故事
2. **use_case表** - 存储功能故事下的小故事 
3. **test_case表** - 存储测试用例
4. **relation表** - 存储各种关联关系

### 关系类型
- `FEATURE_TO_TEST_CASE` - 故事直接关联测试用例（从故事添加）
- `USE_CASE_TO_TEST_CASE` - 小故事关联测试用例（从小故事添加）
- `TEST_CASE_TO_FEATURE` - 测试用例关联故事（从测试用例添加）
- `TEST_CASE_TO_USE_CASE` - 测试用例关联小故事（从测试用例添加）

## 查找逻辑

### 情况1：主版本Feature分析（始终执行）
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

#### 查找有测试用例覆盖的feature
```sql
SELECT DISTINCT 
  CASE 
    WHEN r.category = 'FEATURE_TO_TEST_CASE' THEN r.object_id
    WHEN r.category = 'TEST_CASE_TO_FEATURE' THEN r.target_id
  END as feature_id
FROM relation r
WHERE r.category IN ('FEATURE_TO_TEST_CASE', 'TEST_CASE_TO_FEATURE')
AND (
  (r.category = 'FEATURE_TO_TEST_CASE' AND r.object_id IN (feature_ids)) OR
  (r.category = 'TEST_CASE_TO_FEATURE' AND r.target_id IN (feature_ids))
)
```

#### 查找有测试用例覆盖的use_case
```sql
SELECT DISTINCT 
  CASE 
    WHEN r.category = 'USE_CASE_TO_TEST_CASE' THEN r.object_id
    WHEN r.category = 'TEST_CASE_TO_USE_CASE' THEN r.target_id
  END as use_case_id
FROM relation r
WHERE r.category IN ('USE_CASE_TO_TEST_CASE', 'TEST_CASE_TO_USE_CASE')
AND (
  (r.category = 'USE_CASE_TO_TEST_CASE' AND r.object_id IN (use_case_ids)) OR
  (r.category = 'TEST_CASE_TO_USE_CASE' AND r.target_id IN (use_case_ids))
)
```

### 情况2：包含版本Use Case补充（当includeVersions有值时）
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

### 版本处理逻辑

**重要原则**：我们有2种场景，每种情况的入参都要说清楚

**场景1：简单版本查询**
- **入参**: `projectId`, `version`
- **处理**: 查询指定版本的Feature，Use Case通过feature_id关联到Feature

**场景2：复杂版本查询（多版本支持）**
- **入参**: `projectId`, `majorVersion`, `includeVersions`
- **处理**: 主版本Feature分析 + 包含版本Use Case补充
- **版本匹配规则**: Use Case版本 = 主版本 OR Use Case版本 ∈ includeVersions

## 实现要点

### 1. 避免重复计算
- 一个feature如果有use_case，就不再计算feature本身
- 只计算use_case的覆盖情况

### 2. 双向关联查找
- 从故事端添加的关联：`FEATURE_TO_TEST_CASE`、`USE_CASE_TO_TEST_CASE`
- 从测试用例端添加的关联：`TEST_CASE_TO_FEATURE`、`TEST_CASE_TO_USE_CASE`
- 查找时需要同时考虑这两种方向的关联

### 3. 版本匹配
- 以feature的版本为主要筛选条件
- use_case通过feature_id间接关联到版本

## API设计

### 接口路径
- `GET /api/versionQualityReport/storyCoverage` - 简单查询
- `POST /api/versionQualityReport/storyCoverage` - 复杂查询（支持多版本）

### GET请求参数
- `projectId`: 项目ID
- `version`: 版本号

### POST请求参数
```json
{
    "projectId": 1874424342973054977,
    "majorVersion": "1.0.0.0",
    "includeVersions": ["1.0.0.0", "2.0.0.0"]
}
```

### 响应格式

#### 简单查询响应格式（GET请求）
```json
{
  "success": true,
  "data": {
    "totalStories": 100,
    "coveredStories": 85,
    "coverageRate": 85.0,
    "details": {
      "featureCount": 50,
      "useCaseCount": 50,
      "coveredFeatures": 40,
      "coveredUseCases": 45
    },
    "featuresDetails": {
      "features": [
        {
          "id": 123,
          "title": "用户登录功能",
          "version": "1.0.0.0",
          "covered": true,
          "hasUseCases": true,
          "useCases": [
            {
              "id": 456,
              "title": "正常登录流程",
              "version": "1.0.0.0",
              "covered": true
            }
          ]
        }
      ]
    }
  }
}
```

#### 复杂查询响应格式（POST请求 - 包含所有可能条件）
```json
{
  "success": true,
  "data": {
    "totalStories": 150,
    "coveredStories": 128,
    "coverageRate": 85.3,
    "queryConditions": {
      "projectId": 1874424342973054977,
      "majorVersion": "2.0.0.0",
      "includeVersions": ["2.0.0.0", "2.1.0.0", "1.9.0.0"]
    },
    "details": {
      "featureCount": 75,
      "useCaseCount": 75,
      "coveredFeatures": 65,
      "coveredUseCases": 63,
      "majorVersionFeatureCount": 60,
      "includeVersionUseCaseCount": 15,
      "versionNotMatchedCount": 12
    },
    "versionAnalysis": {
      "majorVersionAnalysis": {
        "version": "2.0.0.0",
        "featureCount": 60,
        "validUseCaseCount": 45,
        "coveredFeatureCount": 50,
        "coveredUseCaseCount": 38
      },
      "includeVersionsAnalysis": [
        {
          "version": "2.1.0.0",
          "additionalUseCaseCount": 8,
          "coveredCount": 6
        },
        {
          "version": "1.9.0.0",
          "additionalUseCaseCount": 7,
          "coveredCount": 5
        }
      ],
      "versionNotMatchedInfo": [
        {
          "version": "1.8.0.0",
          "useCaseCount": 5,
          "description": "Feature下版本不匹配的Use Case"
        },
        {
          "version": "3.0.0.0",
          "useCaseCount": 7,
          "description": "Feature下版本不匹配的Use Case"
        }
      ]
    },
    "featuresDetails": {
      "features": [
        {
          "id": 123,
          "title": "用户登录功能",
          "version": "2.0.0.0",
          "covered": true,
          "hasUseCases": true,
          "useCaseAnalysis": {
            "totalCount": 5,
            "validCount": 3,
            "coveredCount": 3,
            "versionNotMatchedCount": 2
          },
          "useCases": [
            {
              "id": 456,
              "title": "正常登录流程",
              "version": "2.0.0.0",
              "covered": true,
              "isVersionMatched": true,
              "testCaseCount": 3
            },
            {
              "id": 457,
              "title": "密码错误处理",
              "version": "2.1.0.0",
              "covered": true,
              "isVersionMatched": true,
              "testCaseCount": 2
            },
            {
              "id": 458,
              "title": "账号锁定处理",
              "version": "2.0.0.0",
              "covered": false,
              "isVersionMatched": true,
              "testCaseCount": 0
            }
          ],
          "versionNotMatchedUseCases": [
            {
              "id": 459,
              "title": "旧版本登录逻辑",
              "version": "1.8.0.0",
              "covered": false,
              "isVersionMatched": false,
              "reason": "版本不在includeVersions范围内"
            }
          ]
        },
        {
          "id": 124,
          "title": "商品搜索功能",
          "version": "2.0.0.0",
          "covered": true,
          "hasUseCases": false,
          "directTestCaseCount": 4,
          "useCases": [],
          "versionNotMatchedUseCases": []
        },
        {
          "id": 125,
          "title": "订单处理功能",
          "version": "2.0.0.0",
          "covered": false,
          "hasUseCases": true,
          "useCaseAnalysis": {
            "totalCount": 3,
            "validCount": 2,
            "coveredCount": 0,
            "versionNotMatchedCount": 1
          },
          "useCases": [
            {
              "id": 460,
              "title": "创建订单",
              "version": "2.0.0.0",
              "covered": false,
              "isVersionMatched": true,
              "testCaseCount": 0
            },
            {
              "id": 461,
              "title": "取消订单",
              "version": "2.1.0.0",
              "covered": false,
              "isVersionMatched": true,
              "testCaseCount": 0
            }
          ],
          "versionNotMatchedUseCases": [
            {
              "id": 462,
              "title": "老版本订单流程",
              "version": "1.5.0.0",
              "covered": false,
              "isVersionMatched": false,
              "reason": "版本不在查询范围内"
            }
          ]
        }
      ],
      "additionalIncludeVersionUseCases": [
        {
          "id": 500,
          "title": "新增支付功能",
          "version": "2.1.0.0",
          "featureId": null,
          "covered": true,
          "testCaseCount": 2,
          "source": "includeVersions补充"
        },
        {
          "id": 501,
          "title": "历史数据迁移",
          "version": "1.9.0.0",
          "featureId": null,
          "covered": false,
          "testCaseCount": 0,
          "source": "includeVersions补充"
        }
      ]
    },
    "statistics": {
      "coverageByVersion": [
        {
          "version": "2.0.0.0",
          "totalStories": 105,
          "coveredStories": 90,
          "coverageRate": 85.7
        },
        {
          "version": "2.1.0.0",
          "totalStories": 15,
          "coveredStories": 12,
          "coverageRate": 80.0
        },
        {
          "version": "1.9.0.0",
          "totalStories": 30,
          "coveredStories": 26,
          "coverageRate": 86.7
        }
      ],
      "coverageByType": {
        "featureDirectCoverage": {
          "count": 15,
          "percentage": 20.0,
          "description": "Feature直接关联测试用例"
        },
        "useCaseCoverage": {
          "count": 60,
          "percentage": 80.0,
          "description": "通过Use Case关联测试用例"
        }
      }
    },
    "recommendations": [
      "建议为未覆盖的12个故事添加测试用例",
      "重点关注版本2.1.0.0的覆盖率提升",
      "建议对版本不匹配的Use Case进行版本整理"
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

#### 空数据响应格式
```json
{
  "success": true,
  "data": {
    "totalStories": 0,
    "coveredStories": 0,
    "coverageRate": 0.0,
    "queryConditions": {
      "projectId": 1874424342973054977,
      "majorVersion": "3.0.0.0",
      "includeVersions": []
    },
    "details": {
      "featureCount": 0,
      "useCaseCount": 0,
      "coveredFeatures": 0,
      "coveredUseCases": 0,
      "majorVersionFeatureCount": 0,
      "includeVersionUseCaseCount": 0,
      "versionNotMatchedCount": 0
    },
    "message": "未找到指定版本的功能故事"
  }
}
```

## 质量评级标准

- **优秀 (90%+)**：测试覆盖非常完整
- **良好 (80-89%)**：测试覆盖较为完整
- **一般 (70-79%)**：测试覆盖基本达标
- **较差 (<70%)**：测试覆盖不足，存在风险

## 报表展示

### 指标卡片
- 总故事数
- 已覆盖故事数  
- 覆盖率百分比
- 质量等级

### 详细分析
- Feature覆盖情况
- Use Case覆盖情况
- 未覆盖故事列表
- 改进建议
