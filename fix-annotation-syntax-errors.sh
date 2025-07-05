
#!/bin/bash

echo "Fixing all annotation syntax errors..."

# Remove any trailing // comments from annotations
find src/main/java -name "*.java" -type f -exec sed -i 's/@Schema(description="\([^"]*\)")\///@Schema(description="\1")/g' {} \;
find src/main/java -name "*.java" -type f -exec sed -i 's/@Operation(summary="\([^"]*\)")\///@Operation(summary="\1")/g' {} \;
find src/main/java -name "*.java" -type f -exec sed -i 's/@Tag(name="\([^"]*\)")\///@Tag(name="\1")/g' {} \;

# Fix any double slashes at end of annotations
find src/main/java -name "*.java" -type f -exec sed -i 's/)\/\/$/)/g' {} \;

# Fix any remaining malformed patterns
find src/main/java -name "*.java" -type f -exec sed -i 's/@Schema(description="\([^"]*\)")\//@Schema(description="\1")/g' {} \;
find src/main/java -name "*.java" -type f -exec sed -i 's/@Operation(summary="\([^"]*\)")\//@Operation(summary="\1")/g' {} \;

# Remove any stray comment markers after annotations
find src/main/java -name "*.java" -type f -exec sed -i 's/^[[:space:]]*\/\/[[:space:]]*$//g' {} \;

echo "Fixed all annotation syntax errors!"
