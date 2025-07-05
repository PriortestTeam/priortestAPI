
#!/bin/bash

echo "🔧 正在系统性修复Java语法错误..."

# 1. 修复包声明前的空行问题
echo "修复包声明格式..."
find src/main/java -name "*.java" -type f -exec sed -i '1s/^$//' {} \;
find src/main/java -name "*.java" -type f -exec sed -i '/^package/{ N; s/\npackage/package/; }' {} \;

# 2. 修复注解中缺少的右括号
echo "修复注解语法错误..."
find src/main/java -name "*.java" -type f -exec sed -i 's/@Schema(description = "\([^"]*\)"[^)]*$/\@Schema(description = "\1")/g' {} \;
find src/main/java -name "*.java" -type f -exec sed -i 's/@Operation(summary = "\([^"]*\)"[^)]*$/\@Operation(summary = "\1")/g' {} \;
find src/main/java -name "*.java" -type f -exec sed -i 's/@Tag(name = "\([^"]*\)"[^)]*$/\@Tag(name = "\1")/g' {} \;

# 3. 修复分号结尾的注解
find src/main/java -name "*.java" -type f -exec sed -i 's/@Schema(description = "\([^"]*\)");/@Schema(description = "\1")/g' {} \;

# 4. 修复多余的右大括号
echo "修复多余的大括号..."
find src/main/java -name "*.java" -type f -exec sed -i '/^}$/N; s/}\n}$/}/g' {} \;

# 5. 修复类定义后的多余符号
find src/main/java -name "*.java" -type f -exec sed -i '/^public class.*{$/N; s/{\n}$/}/g' {} \;

echo "✅ 语法修复完成，正在测试编译..."
mvn clean compile -q

if [ $? -eq 0 ]; then
    echo "✅ 编译成功！"
else
    echo "❌ 仍有编译错误，需要进一步检查"
fi
