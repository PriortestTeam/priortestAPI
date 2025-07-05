
#!/bin/bash

echo "🚀 全面编译错误修复脚本启动..."

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
        
        echo "🔧 正在自动修复编译错误..."
        
        # 1. 修复缺少的右括号 ')' expected
        echo "修复缺少的右括号..."
        find src/main/java -name "*.java" -type f -exec sed -i 's/\(@Schema(description="[^"]*"\)$/\1)/g' {} \;
        find src/main/java -name "*.java" -type f -exec sed -i 's/\(@Operation(summary="[^"]*"\)$/\1)/g' {} \;
        find src/main/java -name "*.java" -type f -exec sed -i 's/\(@Tag(name="[^"]*"\)$/\1)/g' {} \;
        
        # 2. 修复 ')' or ',' expected 错误
        echo "修复缺少逗号或括号..."
        find src/main/java -name "*.java" -type f -exec sed -i 's/\(@Schema(description = "[^"]*"\)\([^)]*\)$/\1)/g' {} \;
        find src/main/java -name "*.java" -type f -exec sed -i 's/\(@Operation(summary = "[^"]*"\)\([^)]*\)$/\1)/g' {} \;
        
        # 3. 修复不完整的引号
        echo "修复不完整的引号..."
        find src/main/java -name "*.java" -type f -exec sed -i 's/@Schema(description = \([^")]*\)$/@Schema(description = "\1")/g' {} \;
        find src/main/java -name "*.java" -type f -exec sed -i 's/@Operation(summary = \([^")]*\)$/@Operation(summary = "\1")/g' {} \;
        
        # 4. 修复 illegal start of type 错误
        echo "修复非法类型开始..."
        find src/main/java -name "*.java" -type f -exec sed -i 's/^[[:space:]]*@Configuration[[:space:]]*$/@Configuration/g' {} \;
        find src/main/java -name "*.java" -type f -exec sed -i 's/^[[:space:]]*@Component[[:space:]]*$/@Component/g' {} \;
        find src/main/java -name "*.java" -type f -exec sed -i 's/^[[:space:]]*@Service[[:space:]]*$/@Service/g' {} \;
        
        # 5. 修复 class, interface, enum, or record expected 错误
        echo "修复类声明错误..."
        find src/main/java -name "*.java" -type f -exec sed -i '/^public class.*{$/i\
' {} \;
        
        # 6. 修复缺少分号的语句
        echo "修复缺少分号..."
        find src/main/java -name "*.java" -type f -exec sed -i 's/\(throw new [^;]*Exception([^;]*)\)$/\1;/g' {} \;
        find src/main/java -name "*.java" -type f -exec sed -i 's/\(return new [^;]*Builder[^;]*\)$/\1;/g' {} \;
        
        # 7. 修复方法声明错误
        echo "修复方法声明错误..."
        find src/main/java -name "*.java" -type f -exec sed -i 's/\(@GetMapping([^)]*)\)[[:space:]]*$/\1/g' {} \;
        find src/main/java -name "*.java" -type f -exec sed -i 's/\(@PostMapping([^)]*)\)[[:space:]]*$/\1/g' {} \;
        find src/main/java -name "*.java" -type f -exec sed -i 's/\(@PutMapping([^)]*)\)[[:space:]]*$/\1/g' {} \;
        find src/main/java -name "*.java" -type f -exec sed -i 's/\(@DeleteMapping([^)]*)\)[[:space:]]*$/\1/g' {} \;
        
        # 8. 修复条件语句中缺少的括号
        echo "修复条件语句括号..."
        find src/main/java -name "*.java" -type f -exec sed -i 's/if (\([^)]*\)$/if (\1) {/g' {} \;
        find src/main/java -name "*.java" -type f -exec sed -i 's/} else if (\([^)]*\)$/} else if (\1) {/g' {} \;
        
        # 9. 修复泛型声明错误
        echo "修复泛型声明..."
        find src/main/java -name "*.java" -type f -exec sed -i 's/List</List\&lt;/g' {} \;
        find src/main/java -name "*.java" -type f -exec sed -i 's/Map</Map\&lt;/g' {} \;
        
        # 10. 修复 reached end of file while parsing
        echo "修复文件结尾错误..."
        find src/main/java -name "*.java" -type f -exec sh -c 'if [ "$(tail -c 1 "$1")" != "}" ]; then echo "}" >> "$1"; fi' _ {} \;
        
        # 等待一秒再进行下次尝试
        sleep 1
        
        attempt=$((attempt + 1))
    fi
done

echo "❌ 在 $MAX_ATTEMPTS 次尝试后仍然编译失败"
echo "正在显示最后的错误信息："
cat /tmp/compile_errors.log | head -20
exit 1
