#!/bin/bash

# Check if correct number of arguments provided
if [ $# -ne 3 ]; then
    echo "Usage: $0 <root_directory> <old_package_name> <new_package_name>"
    echo
    echo "Example: $0 src com.skeletor com.archetype"
    echo
    echo "Arguments:"
    echo "  root_directory    - Directory to search for Java/Groovy files"
    echo "  old_package_name  - Current package name to replace"
    echo "  new_package_name  - New package name to use"
    exit 1
fi

ROOT_DIR="$1"
OLD_PACKAGE="$2"
NEW_PACKAGE="$3"

# Validate root directory exists
if [ ! -d "$ROOT_DIR" ]; then
    echo "ERROR: Root directory '$ROOT_DIR' does not exist"
    exit 1
fi

echo "========================================"
echo "Java Package Renaming Script"
echo "========================================"
echo "Root directory: $ROOT_DIR"
echo "Old package:    $OLD_PACKAGE"
echo "New package:    $NEW_PACKAGE"
echo "========================================"
echo

# Escape dots for regex (sed uses different escaping than PowerShell)
OLD_PACKAGE_ESCAPED=$(echo "$OLD_PACKAGE" | sed 's/\./\\./g')
NEW_PACKAGE_ESCAPED="$NEW_PACKAGE"

echo "Replacing package and import statements in Java files..."

JAVA_COUNT=0
while IFS= read -r -d '' file; do
    echo "Processing: $file"
    
    # Use sed for in-place editing with backup
    sed -i.bak \
        -e "s/package ${OLD_PACKAGE_ESCAPED}/package ${NEW_PACKAGE_ESCAPED}/g" \
        -e "s/import ${OLD_PACKAGE_ESCAPED}/import ${NEW_PACKAGE_ESCAPED}/g" \
        -e "s/${OLD_PACKAGE_ESCAPED}\./${NEW_PACKAGE_ESCAPED}./g" \
        "$file"
    
    # Remove backup file
    rm -f "${file}.bak"
    
    ((JAVA_COUNT++))
done < <(find "$ROOT_DIR" -name "*.java" -type f -print0)

echo
echo "Replacing package and import statements in Groovy files..."

GROOVY_COUNT=0
while IFS= read -r -d '' file; do
    echo "Processing: $file"
    
    # Use sed for in-place editing with backup
    sed -i.bak \
        -e "s/package ${OLD_PACKAGE_ESCAPED}/package ${NEW_PACKAGE_ESCAPED}/g" \
        -e "s/import ${OLD_PACKAGE_ESCAPED}/import ${NEW_PACKAGE_ESCAPED}/g" \
        -e "s/${OLD_PACKAGE_ESCAPED}\./${NEW_PACKAGE_ESCAPED}./g" \
        "$file"
    
    # Remove backup file
    rm -f "${file}.bak"
    
    ((GROOVY_COUNT++))
done < <(find "$ROOT_DIR" -name "*.groovy" -type f -print0)

echo
echo "========================================"
echo "Package renaming completed!"
echo "========================================"
echo "Java files processed:   $JAVA_COUNT"
echo "Groovy files processed: $GROOVY_COUNT"
echo
echo "Don't forget to:"
echo "1. Rename the actual directory structure if needed"
echo "2. Update build.gradle or pom.xml files"
echo "3. Update configuration files that reference the old package"
echo "========================================"

exit 0
