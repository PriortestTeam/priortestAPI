
#!/bin/bash

echo "Fixing all remaining malformed Swagger annotations..."

# Fix all @Schemavalue= patterns
find src/main/java -name "*.java" -type f -exec sed -i 's/@Schemavalue="\([^"]*\)"/@Schema(description="\1")/g' {} \;
find src/main/java -name "*.java" -type f -exec sed -i 's/@Schemavalue = "\([^"]*\)"/@Schema(description = "\1")/g' {} \;

# Fix all @Operation" patterns  
find src/main/java -name "*.java" -type f -exec sed -i 's/@Operation"\([^"]*\)"/@Operation(summary="\1")/g' {} \;

# Fix any remaining @Schema syntax issues
find src/main/java -name "*.java" -type f -exec sed -i 's/@Schema *value *= *"\([^"]*\)"/@Schema(description = "\1")/g' {} \;

# Fix any space issues in annotations
find src/main/java -name "*.java" -type f -exec sed -i 's/@Schema(\([^)]*\))/@Schema(\1)/g' {} \;
find src/main/java -name "*.java" -type f -exec sed -i 's/@Operation(\([^)]*\))/@Operation(\1)/g' {} \;

echo "Fixed all remaining malformed annotations!"
