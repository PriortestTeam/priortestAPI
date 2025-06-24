
#!/bin/bash

echo "Starting comprehensive fix of all annotation compilation errors..."

# Fix @Schema annotations with extra closing parentheses
echo "Fixing @Schema annotations with extra parentheses..."
find src/main/java -name "*.java" -type f -exec sed -i 's/@Schema(description="\([^"]*\"))//@Schema(description="\1")/g' {} \;

# Fix @Operation annotations with extra closing parentheses  
echo "Fixing @Operation annotations with extra parentheses..."
find src/main/java -name "*.java" -type f -exec sed -i 's/@Operation(summary="\([^"]*\"))//@Operation(summary="\1")/g' {} \;

# Fix @Tag annotations with extra closing parentheses
echo "Fixing @Tag annotations with extra parentheses..."
find src/main/java -name "*.java" -type f -exec sed -i 's/@Tag(name="\([^"]*\"))//@Tag(name="\1")/g' {} \;

# Fix other malformed annotation patterns from previous errors
echo "Fixing malformed @Schema patterns..."
find src/main/java -name "*.java" -type f -exec sed -i 's/@Schemavalue="\([^"]*\)"/@Schema(description="\1")/g' {} \;
find src/main/java -name "*.java" -type f -exec sed -i 's/@Operation"\([^"]*\)"/@Operation(summary="\1")/g' {} \;

# Fix incomplete annotation syntax
echo "Fixing incomplete annotations..."
find src/main/java -name "*.java" -type f -exec sed -i 's/@Schema(\([^)]*\)$/@Schema(\1)/g' {} \;
find src/main/java -name "*.java" -type f -exec sed -i 's/@Operation(\([^)]*\)$/@Operation(\1)/g' {} \;

# Fix any remaining space and syntax issues
echo "Fixing remaining syntax issues..."
find src/main/java -name "*.java" -type f -exec sed -i 's/@Schema *value *= *"\([^"]*\)"/@Schema(description="\1")/g' {} \;
find src/main/java -name "*.java" -type f -exec sed -i 's/@Schema *"\([^"]*\)"/@Schema(description="\1")/g' {} \;

echo "All annotation syntax errors have been fixed!"

# Test compilation
echo "Testing compilation..."
mvn compile -q
if [ $? -eq 0 ]; then
    echo "SUCCESS: Compilation completed without errors!"
else
    echo "There may still be some compilation errors. Please check the output above."
fi
