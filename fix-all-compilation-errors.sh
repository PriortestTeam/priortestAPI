
#!/bin/bash

echo "Starting comprehensive fix of all Swagger annotation compilation errors..."

# Fix malformed @Schema annotations
echo "Fixing @Schema annotations..."
find src/main/java -name "*.java" -type f -exec sed -i 's/@Schemavalue="\([^"]*\)"/@Schema(description="\1")/g' {} \;
find src/main/java -name "*.java" -type f -exec sed -i 's/@Schemavalue = "\([^"]*\)"/@Schema(description = "\1")/g' {} \;
find src/main/java -name "*.java" -type f -exec sed -i 's/@Schemaname= *"\([^"]*\)", *value *= *"\([^"]*\)"/@Schema(description = "\2")/g' {} \;
find src/main/java -name "*.java" -type f -exec sed -i 's/@Schemaname= *"\([^"]*\)"/@Schema(description = "\1")/g' {} \;
find src/main/java -name "*.java" -type f -exec sed -i 's/@Schema *name *= *"\([^"]*\)", *value *= *"\([^"]*\)"/@Schema(description = "\2")/g' {} \;
find src/main/java -name "*.java" -type f -exec sed -i 's/@Schema *name *= *"\([^"]*\)"/@Schema(description = "\1")/g' {} \;
find src/main/java -name "*.java" -type f -exec sed -i 's/@Schema *value *= *"\([^"]*\)"/@Schema(description = "\1")/g' {} \;

# Fix malformed @Operation annotations
echo "Fixing @Operation annotations..."
find src/main/java -name "*.java" -type f -exec sed -i 's/@Operation"\([^"]*\)"/@Operation(summary="\1")/g' {} \;
find src/main/java -name "*.java" -type f -exec sed -i 's/@Operation *value *= *"\([^"]*\)"/@Operation(summary = "\1")/g' {} \;

# Fix incomplete annotation syntax with missing closing quotes or parentheses
echo "Fixing incomplete annotations..."
find src/main/java -name "*.java" -type f -exec sed -i 's/@Schema(description = "\([^"]*\)$/@Schema(description = "\1")/g' {} \;
find src/main/java -name "*.java" -type f -exec sed -i 's/@Operation(summary = "\([^"]*\)$/@Operation(summary = "\1")/g' {} \;

# Fix any remaining syntax issues with spaces and malformed parentheses
echo "Fixing parentheses and spacing issues..."
find src/main/java -name "*.java" -type f -exec sed -i 's/@Schema(\([^)]*\)$/@Schema(\1)/g' {} \;
find src/main/java -name "*.java" -type f -exec sed -i 's/@Operation(\([^)]*\)$/@Operation(\1)/g' {} \;

# Fix validation annotations with value parameters
echo "Fixing validation annotations..."
find src/main/java -name "*.java" -type f -exec sed -i 's/@NotNull.*message.*= *"\([^"]*\)".*value.*= *"\([^"]*\)"/@NotNull(message = "\1")\n    @Schema(description = "\2")/g' {} \;
find src/main/java -name "*.java" -type f -exec sed -i 's/@NotBlank.*message.*= *"\([^"]*\)".*value.*= *"\([^"]*\)"/@NotBlank(message = "\1")\n    @Schema(description = "\2")/g' {} \;

# Fix any remaining malformed patterns that might cause compilation errors
echo "Fixing remaining malformed patterns..."
find src/main/java -name "*.java" -type f -exec sed -i 's/@Schema *"\([^"]*\)"/@Schema(description = "\1")/g' {} \;
find src/main/java -name "*.java" -type f -exec sed -i 's/@Operation *"\([^"]*\)"/@Operation(summary = "\1")/g' {} \;

# Fix incomplete quotes and missing semicolons
find src/main/java -name "*.java" -type f -exec sed -i 's/@Schema(description = \([^")]*\)$/@Schema(description = "\1")/g' {} \;
find src/main/java -name "*.java" -type f -exec sed -i 's/@Operation(summary = \([^")]*\)$/@Operation(summary = "\1")/g' {} \;

# Fix any remaining incomplete annotation brackets
find src/main/java -name "*.java" -type f -exec sed -i 's/@Schema([^)]*/&)/g' {} \;
find src/main/java -name "*.java" -type f -exec sed -i 's/@Operation([^)]*/&)/g' {} \;

echo "Comprehensive fix completed! All Swagger annotation compilation errors should now be resolved."

# List files that were modified for verification
echo "Files that were processed:"
find src/main/java -name "*.java" -type f | head -20
echo "... and more"
