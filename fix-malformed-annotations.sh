
#!/bin/bash

echo "Fixing malformed Swagger annotations..."

# Fix @Schema annotations with extra closing parentheses
find src/main/java -name "*.java" -type f -exec sed -i 's/@Schema(description="\([^"]*\"))//@Schema(description="\1")/g' {} \;

# Fix @Operation annotations with extra closing parentheses  
find src/main/java -name "*.java" -type f -exec sed -i 's/@Operation(summary="\([^"]*\"))//@Operation(summary="\1")/g' {} \;

# Fix @Tag annotations with extra closing parentheses
find src/main/java -name "*.java" -type f -exec sed -i 's/@Tag(name="\([^"]*\"))//@Tag(name="\1")/g' {} \;

# Fix any remaining double closing parentheses in annotations
find src/main/java -name "*.java" -type f -exec sed -i 's/description="\([^"]*\"))/description="\1")/g' {} \;
find src/main/java -name "*.java" -type f -exec sed -i 's/summary="\([^"]*\"))/summary="\1")/g' {} \;
find src/main/java -name "*.java" -type f -exec sed -i 's/name="\([^"]*\"))/name="\1")/g' {} \;

# Fix incomplete @Schema patterns that may have been created
find src/main/java -name "*.java" -type f -exec sed -i 's/@Schema *$/\@Schema(description="")/g' {} \;
find src/main/java -name "*.java" -type f -exec sed -i 's/@Operation *$/\@Operation(summary="")/g' {} \;

echo "Fixed malformed annotations!"
