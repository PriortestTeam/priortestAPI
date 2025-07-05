
#!/bin/bash

echo "🚀 开始修复所有语法错误..."

# 1. 修复HTML实体编码错误
echo "修复HTML实体编码错误..."
find src/main/java -name "*.java" -type f -exec sed -i 's/&lt;/</g' {} \;
find src/main/java -name "*.java" -type f -exec sed -i 's/&gt;/>/g' {} \;

# 2. 修复注解语法错误
echo "修复注解语法错误..."
find src/main/java -name "*.java" -type f -exec sed -i 's/@Schema(description = "\([^"]*\)")//@Schema(description = "\1")/g' {} \;
find src/main/java -name "*.java" -type f -exec sed -i 's/@Operation(summary = "\([^"]*\)")//@Operation(summary = "\1")/g' {} \;
find src/main/java -name "*.java" -type f -exec sed -i 's/@Tag(name = "\([^"]*\)")//@Tag(name = "\1")/g' {} \;

# 3. 修复缺少的分号
echo "修复缺少的分号..."
find src/main/java -name "*.java" -type f -exec sed -i 's/\(@Schema(description="[^"]*"\)$/\1;/g' {} \;
find src/main/java -name "*.java" -type f -exec sed -i 's/\(@Operation(summary="[^"]*"\)$/\1;/g' {} \;

# 4. 修复不完整的类声明
echo "修复不完整的类声明..."
find src/main/java -name "*.java" -type f -exec sed -i '/^[[:space:]]*$/d' {} \;

# 5. 修复多余的右括号
echo "修复多余的右括号..."
find src/main/java -name "*.java" -type f -exec sed -i 's/^}$/}/g' {} \;

# 6. 修复缺少的括号
echo "修复缺少的括号..."
find src/main/java -name "*.java" -type f -exec sed -i 's/\(@Schema(description="[^"]*"\)$/\1)/g' {} \;
find src/main/java -name "*.java" -type f -exec sed -i 's/\(@Operation(summary="[^"]*"\)$/\1)/g' {} \;

echo "✅ 语法错误修复完成，正在编译测试..."

# 测试编译
mvn clean compile -q
if [ $? -eq 0 ]; then
    echo "✅ 编译成功！启动应用..."
    ONECLICK_PATH=/home/runner/workspace/ mvn spring-boot:run
else
    echo "❌ 仍有编译错误，请检查详细错误信息"
    mvn compile
fi
