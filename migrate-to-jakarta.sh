
#!/bin/bash

echo "开始Jakarta EE迁移..."

# 查找所有Java文件并替换包名
find src/main/java -name "*.java" -type f | while read file; do
    echo "处理文件: $file"
    
    # 替换常用的javax包为jakarta包
    sed -i 's/import javax\.servlet\./import jakarta.servlet./g' "$file"
    sed -i 's/import javax\.persistence\./import jakarta.persistence./g' "$file"
    sed -i 's/import javax\.validation\./import jakarta.validation./g' "$file"
    sed -i 's/import javax\.annotation\./import jakarta.annotation./g' "$file"
    sed -i 's/import javax\.inject\./import jakarta.inject./g' "$file"
    sed -i 's/import javax\.transaction\./import jakarta.transaction./g' "$file"
    sed -i 's/import javax\.xml\.bind\./import jakarta.xml.bind./g' "$file"
    
    # 替换类引用
    sed -i 's/javax\.servlet\./jakarta.servlet./g' "$file"
    sed -i 's/javax\.persistence\./jakarta.persistence./g' "$file"
    sed -i 's/javax\.validation\./jakarta.validation./g' "$file"
    sed -i 's/javax\.annotation\./jakarta.annotation./g' "$file"
done

echo "Jakarta EE迁移完成!"
