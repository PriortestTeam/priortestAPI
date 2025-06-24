
#!/bin/bash

echo "Fixing extra parentheses in @Schema annotations..."

# Fix @Schema(description="...")>) pattern - remove extra closing parenthesis
find src/main/java -name "*.java" -type f -exec sed -i 's/@Schema(description="\([^"]*\"))//@Schema(description="\1")/g' {} \;

# Fix other annotation patterns with extra parentheses
find src/main/java -name "*.java" -type f -exec sed -i 's/@Operation(summary="\([^"]*\"))//@Operation(summary="\1")/g' {} \;
find src/main/java -name "*.java" -type f -exec sed -i 's/@Tag(name="\([^"]*\"))//@Tag(name="\1")/g' {} \;

echo "Fixed all extra parentheses in Swagger annotations!"

# Test compilation to verify fixes
echo "Testing compilation..."
mvn compile -q
if [ $? -eq 0 ]; then
    echo "Compilation successful - all annotation syntax errors are fixed!"
else
    echo "There may still be some compilation errors. Please check the output above."
fi
