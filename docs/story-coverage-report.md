
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
