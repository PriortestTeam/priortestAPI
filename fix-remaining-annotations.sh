
#!/bin/bash

echo "Fixing remaining Swagger annotation issues..."

# Fix @Schemaname= pattern
find src/main/java -name "*.java" -type f -exec sed -i 's/@Schemaname= *"\([^"]*\)", *value *= *"\([^"]*\)"/@Schema(description = "\2")/g' {} \;
find src/main/java -name "*.java" -type f -exec sed -i 's/@Schemaname= *"\([^"]*\)"/@Schema(description = "\1")/g' {} \;

# Fix any remaining @Schema syntax issues
find src/main/java -name "*.java" -type f -exec sed -i 's/@Schema *name *= *"\([^"]*\)", *value *= *"\([^"]*\)"/@Schema(description = "\2")/g' {} \;
find src/main/java -name "*.java" -type f -exec sed -i 's/@Schema *name *= *"\([^"]*\)"/@Schema(description = "\1")/g' {} \;

# Fix validation annotation syntax issues
find src/main/java -name "*.java" -type f -exec sed -i 's/@NotNull.*message.*= *"\([^"]*\)".*value.*= *"\([^"]*\)"/@NotNull(message = "\1")\n    @Schema(description = "\2")/g' {} \;
find src/main/java -name "*.java" -type f -exec sed -i 's/@NotBlank.*message.*= *"\([^"]*\)".*value.*= *"\([^"]*\)"/@NotBlank(message = "\1")\n    @Schema(description = "\2")/g' {} \;

echo "Fixed remaining annotation issues!"
