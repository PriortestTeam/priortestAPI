
#!/bin/bash

echo "🚀 自动检测和修复编译错误脚本启动..."

MAX_ATTEMPTS=5
attempt=1

while [ $attempt -le $MAX_ATTEMPTS ]; do
    echo "📝 尝试第 $attempt 次编译..."
    
    # 清理并编译
    mvn clean compile -q
    
    if [ $? -eq 0 ]; then
        echo "✅ 编译成功！正在启动应用..."
        ONECLICK_PATH=/home/runner/workspace/ mvn spring-boot:run
        exit 0
    else
        echo "❌ 编译失败，正在分析错误..."
        
        # 获取编译错误信息
        mvn compile 2>&1 | tee /tmp/compile_errors.log
        
        # 修复常见的编译错误
        echo "🔧 正在自动修复常见错误..."
        
        # 1. 修复非法字符（反引号）
        echo "修复非法字符..."
        find src/main/java -name "*.java" -type f -exec sed -i 's/`//g' {} \;
        
        # 2. 修复缺少的导入
        if grep -q "cannot find symbol.*Slf4j" /tmp/compile_errors.log; then
            echo "修复缺少的 @Slf4j 导入..."
            find src/main/java -name "*.java" -type f -exec grep -l "@Slf4j" {} \; | while read file; do
                if ! grep -q "import lombok.extern.slf4j.Slf4j" "$file"; then
                    sed -i '1i import lombok.extern.slf4j.Slf4j;' "$file"
                fi
            done
        fi
        
        # 3. 修复 @Schema 注解语法错误
        if grep -q "Schema" /tmp/compile_errors.log; then
            echo "修复 @Schema 注解..."
            find src/main/java -name "*.java" -type f -exec sed -i 's/@Schema *value *= *"\([^"]*\)"/@Schema(description="\1")/g' {} \;
            find src/main/java -name "*.java" -type f -exec sed -i 's/@Schema *"\([^"]*\)"/@Schema(description="\1")/g' {} \;
        fi
        
        # 4. 修复 @Operation 注解语法错误
        if grep -q "Operation" /tmp/compile_errors.log; then
            echo "修复 @Operation 注解..."
            find src/main/java -name "*.java" -type f -exec sed -i 's/@Operation *value *= *"\([^"]*\)"/@Operation(summary="\1")/g' {} \;
            find src/main/java -name "*.java" -type f -exec sed -i 's/@Operation *"\([^"]*\)"/@Operation(summary="\1")/g' {} \;
        fi
        
        # 5. 修复缺少的分号
        echo "修复缺少的分号..."
        find src/main/java -name "*.java" -type f -exec sed -i 's/\(@[A-Za-z]*([^)]*)\)$/\1;/g' {} \;
        
        # 6. 修复多余的括号
        echo "修复多余的括号..."
        find src/main/java -name "*.java" -type f -exec sed -i 's/))/)/g' {} \;
        
        # 7. 修复类声明前的多余内容
        echo "修复类声明语法..."
        find src/main/java -name "*.java" -type f -exec sed -i '/^[^p]*package/,/^public class/{ /^public class/!{ /package/!{ /import/!{ /^$/!{ /^\/\*/!{ /^ \*/!{ /^ \*\//!{ /^@/!d; }; }; }; }; }; }; }; }' {} \;
        
        echo "第 $attempt 次修复完成，准备重新编译..."
        attempt=$((attempt + 1))
    fi
done

echo "❌ 经过 $MAX_ATTEMPTS 次尝试仍无法修复所有编译错误"
echo "📋 最后的编译错误信息："
cat /tmp/compile_errors.log
exit 1
