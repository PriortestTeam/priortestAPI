
# 时区转换功能使用指南

## 概述
本文档描述了系统中时区转换功能的实现和使用方法，帮助开发者在其他模块中应用相同的时区转换逻辑。

## 功能架构

### 1. 时区上下文工具类 (TimezoneContext)
**位置**: `src/main/java/com/hu/oneclick/common/util/TimezoneContext.java`

**功能**: 使用ThreadLocal存储当前线程的用户时区信息

```java
// 设置用户时区
TimezoneContext.setUserTimezone("Asia/Shanghai");

// 获取用户时区
String userTimezone = TimezoneContext.getUserTimezone();

// 清理ThreadLocal
TimezoneContext.clear();
```

### 2. 时区拦截器 (TimezoneInterceptor)
**位置**: `src/main/java/com/hu/oneclick/config/TimezoneInterceptor.java`

**功能**: 自动从HTTP请求头或参数中提取时区信息并存储到ThreadLocal

**请求头格式**: `X-User-Timezone: Asia/Shanghai`
**请求参数格式**: `?timezone=Asia/Shanghai`

### 3. Web MVC配置
**位置**: `src/main/java/com/hu/oneclick/config/WebMvcConfig.java`

**配置**: 拦截所有`/api/**`路径的请求进行时区处理

## 时区转换实现

### 1. 前端到后端的时区转换
将用户本地时间转换为UTC时间存储到数据库：

```java
/**
 * 转换所有日期字段到UTC
 */
private void convertDatesToUTC(Issue issue, String userTimezone) {
    if (userTimezone == null || userTimezone.isEmpty()) {
        System.out.println("=== 没有用户时区信息，跳过UTC转换 ===");
        return;
    }

    try {
        TimeZone userTZ = TimeZone.getTimeZone(userTimezone);

        // 转换各个时间字段
        if (issue.getCreateTime() != null) {
            Date utcTime = convertLocalTimeToUTC(issue.getCreateTime(), userTZ);
            issue.setCreateTime(utcTime);
        }

        if (issue.getCloseDate() != null) {
            Date utcTime = convertLocalTimeToUTC(issue.getCloseDate(), userTZ);
            issue.setCloseDate(utcTime);
        }

        if (issue.getPlanFixDate() != null) {
            Date utcTime = convertLocalTimeToUTC(issue.getPlanFixDate(), userTZ);
            issue.setPlanFixDate(utcTime);
        }

    } catch (Exception e) {
        System.out.println("=== UTC转换失败: " + e.getMessage() + " ===");
    }
}

/**
 * 将用户本地时间转换为UTC时间
 */
private Date convertLocalTimeToUTC(Date localTime, TimeZone userTZ) {
    Calendar localCalendar = Calendar.getInstance(userTZ);
    localCalendar.setTime(localTime);

    Calendar utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    utcCalendar.setTimeInMillis(localCalendar.getTimeInMillis());

    return utcCalendar.getTime();
}
```

### 2. 数据库到前端的时区转换
从数据库查询的UTC时间转换为用户本地时间：

```java
/**
 * 将UTC时间转换为用户本地时间
 */
private Date convertUTCToLocalTime(Date utcTime, TimeZone userTZ) {
    Calendar utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    utcCalendar.setTime(utcTime);

    Calendar localCalendar = Calendar.getInstance(userTZ);
    localCalendar.setTimeInMillis(utcCalendar.getTimeInMillis());

    return localCalendar.getTime();
}
```

## 已实现的API

### Issue模块
- ✅ `/api/issue/save` - 新增时转换时区
- ✅ `/api/issue/update` - 更新时转换时区  
- ✅ `/api/issue/list` - 查询时计算duration（基于UTC）
- ❌ `/api/issue/clone` - 仅复制数据，不涉及时区
- ❌ `/api/issue/info/{id}` - 仅查询详情，不做时区转换

## 在其他模块中应用时区转换

### 步骤1: 在Service层获取用户时区
```java
// 获取用户时区
String userTimezone = TimezoneContext.getUserTimezone();
```

### 步骤2: 在保存/更新操作中转换时区
```java
public void saveEntity(YourEntity entity) {
    String userTimezone = TimezoneContext.getUserTimezone();
    
    // 转换日期字段到UTC
    convertDatesToUTC(entity, userTimezone);
    
    // 保存到数据库
    this.baseMapper.insert(entity);
}
```

### 步骤3: 在查询操作中处理时区（如果需要）
```java
public List<YourEntity> queryEntities() {
    List<YourEntity> entities = this.baseMapper.selectList(null);
    
    // 如果需要将UTC时间转换回用户本地时间
    String userTimezone = TimezoneContext.getUserTimezone();
    entities.forEach(entity -> {
        convertUTCToLocalTime(entity, userTimezone);
    });
    
    return entities;
}
```

## 前端调用要求

前端在调用需要时区转换的API时，必须在请求头中包含用户时区信息：

```javascript
// 方式1: 请求头
fetch('/api/your-module/save', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json',
        'X-User-Timezone': 'Asia/Shanghai'  // 必须包含
    },
    body: JSON.stringify(data)
});

// 方式2: 请求参数
fetch('/api/your-module/save?timezone=Asia/Shanghai', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json'
    },
    body: JSON.stringify(data)
});
```

## 注意事项

1. **数据库存储**: 统一使用UTC时间存储到数据库
2. **前端传递**: 前端必须传递用户时区信息
3. **向后兼容**: 当没有时区信息时，跳过时区转换，避免系统崩溃
4. **异常处理**: 包含完整的异常处理逻辑
5. **调试日志**: 添加详细的日志输出便于调试

## 支持的时区格式
- `Asia/Shanghai` (推荐)
- `UTC`
- `GMT+8`
- `America/New_York`
- 等标准时区ID

## 扩展指南

### 新增模块时区转换步骤：
1. 在Service层导入 `TimezoneContext`
2. 实现 `convertDatesToUTC()` 方法
3. 在保存/更新方法中调用时区转换
4. 确保前端传递时区信息
5. 添加必要的调试日志

### 模板代码：
```java
// 在需要时区转换的Service类中添加
private void convertDatesToUTC(YourEntity entity, String userTimezone) {
    if (userTimezone == null || userTimezone.isEmpty()) {
        System.out.println("=== 没有用户时区信息，跳过UTC转换 ===");
        return;
    }

    try {
        TimeZone userTZ = TimeZone.getTimeZone(userTimezone);
        
        // 转换你的时间字段
        if (entity.getYourDateField() != null) {
            Date utcTime = convertLocalTimeToUTC(entity.getYourDateField(), userTZ);
            entity.setYourDateField(utcTime);
        }
        
    } catch (Exception e) {
        System.out.println("=== UTC转换失败: " + e.getMessage() + " ===");
    }
}
```

## 版本历史
- v1.0 - 2025-07-21: 初始版本，实现Issue模块时区转换功能
