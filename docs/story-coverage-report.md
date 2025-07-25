
# 故事覆盖率报表分析

## 概述

故事覆盖率是衡量功能需求测试覆盖完整程度的重要指标，通过计算已覆盖故事数与总故事数的比例来评估测试质量。

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

### 步骤1：计算总故事数

**重要原则**：避免重复计算
- 如果feature有小故事(use_case)：只计算use_case，不计算feature
- 如果feature没有小故事：计算feature本身

```sql
-- 1. 查询指定版本的所有feature
SELECT f.id, f.title 
FROM feature f 
WHERE f.version = '指定版本' AND f.project_id = ?

-- 2. 对每个feature，检查是否有use_case
SELECT COUNT(*) as usecase_count
FROM use_case uc 
WHERE uc.feature_id = feature_id

-- 3. 计算逻辑：
-- 如果usecase_count > 0：计算该feature下的所有use_case
-- 如果usecase_count = 0：计算该feature本身
```

### 步骤2：计算已覆盖故事数

需要同时考虑双向关联关系：

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

### 步骤3：版本处理逻辑

- **feature版本**：以用户提供的版本为准
- **use_case版本**：use_case有自己的版本，但通过feature_id关联到指定版本的feature

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
`GET /api/versionQualityReport/storyCoverage`

### 请求参数
- `projectId`: 项目ID
- `version`: 版本号

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
