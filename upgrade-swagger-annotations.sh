
#!/bin/bash

echo "Starting Swagger 2 to OpenAPI 3 annotation upgrade..."

# Find all Java files in the project
find src/main/java -name "*.java" -type f | while read file; do
    echo "Processing: $file"
    
    # Update imports
    sed -i 's/import io\.swagger\.annotations\.Api;/import io.swagger.v3.oas.annotations.tags.Tag;/g' "$file"
    sed -i 's/import io\.swagger\.annotations\.ApiOperation;/import io.swagger.v3.oas.annotations.Operation;/g' "$file"
    sed -i 's/import io\.swagger\.annotations\.ApiParam;/import io.swagger.v3.oas.annotations.Parameter;/g' "$file"
    sed -i 's/import io\.swagger\.annotations\.ApiModel;/import io.swagger.v3.oas.annotations.media.Schema;/g' "$file"
    sed -i 's/import io\.swagger\.annotations\.ApiModelProperty;/import io.swagger.v3.oas.annotations.media.Schema;/g' "$file"
    sed -i 's/import io\.swagger\.annotations\.ApiResponse;/import io.swagger.v3.oas.annotations.responses.ApiResponse;/g' "$file"
    sed -i 's/import io\.swagger\.annotations\.ApiResponses;/import io.swagger.v3.oas.annotations.responses.ApiResponses;/g' "$file"
    
    # Update annotations - Controllers
    sed -i 's/@Api(tags = "\([^"]*\)")/@Tag(name = "\1", description = "\1相关接口")/g' "$file"
    sed -i 's/@Api(\([^)]*\))/@Tag\1/g' "$file"
    sed -i 's/@ApiOperation(value = "\([^"]*\)", notes = "\([^"]*\)")/@Operation(summary = "\1", description = "\2")/g' "$file"
    sed -i 's/@ApiOperation(value = "\([^"]*\)")/@Operation(summary = "\1")/g' "$file"
    sed -i 's/@ApiOperation(\([^)]*\))/@Operation\1/g' "$file"
    
    # Update annotations - DTOs/Models
    sed -i 's/@ApiModel(description = "\([^"]*\)")/@Schema(description = "\1")/g' "$file"
    sed -i 's/@ApiModel(\([^)]*\))/@Schema\1/g' "$file"
    sed -i 's/@ApiModelProperty(value = "\([^"]*\)", required = \([^,)]*\))/@Schema(description = "\1", required = \2)/g' "$file"
    sed -i 's/@ApiModelProperty(value = "\([^"]*\)")/@Schema(description = "\1")/g' "$file"
    sed -i 's/@ApiModelProperty("\([^"]*\)")/@Schema(description = "\1")/g' "$file"
    sed -i 's/@ApiModelProperty(\([^)]*\))/@Schema\1/g' "$file"
    
    # Update parameter annotations
    sed -i 's/@ApiParam(value = "\([^"]*\)", required = \([^,)]*\))/@Parameter(description = "\1", required = \2)/g' "$file"
    sed -i 's/@ApiParam(value = "\([^"]*\)")/@Parameter(description = "\1")/g' "$file"
    sed -i 's/@ApiParam(\([^)]*\))/@Parameter\1/g' "$file"
done

echo "Annotation upgrade completed!"
echo "Please review the changes and test your application."
