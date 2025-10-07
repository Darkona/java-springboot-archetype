@echo off
setlocal enabledelayedexpansion

REM Check if correct number of arguments provided
if "%~3"=="" (
    echo Usage: %0 ^<root_directory^> ^<old_package_name^> ^<new_package_name^>
    echo.
    echo Example: %0 src com.skeletor com.archetype
    echo.
    echo Arguments:
    echo   root_directory    - Directory to search for Java/Groovy files
    echo   old_package_name  - Current package name to replace
    echo   new_package_name  - New package name to use
    exit /b 1
)

set "ROOT_DIR=%~1"
set "OLD_PACKAGE=%~2"
set "NEW_PACKAGE=%~3"

REM Validate root directory exists
if not exist "%ROOT_DIR%" (
    echo ERROR: Root directory "%ROOT_DIR%" does not exist
    exit /b 1
)

echo ========================================
echo Java Package Renaming Script
echo ========================================
echo Root directory: %ROOT_DIR%
echo Old package:    %OLD_PACKAGE%
echo New package:    %NEW_PACKAGE%
echo ========================================
echo.

REM Escape dots for regex
set "OLD_PACKAGE_ESCAPED=%OLD_PACKAGE:.=\.%"
set "NEW_PACKAGE_ESCAPED=%NEW_PACKAGE%"

echo Replacing package and import statements in Java files...

set "JAVA_COUNT=0"
for /r "%ROOT_DIR%" %%f in (*.java) do (
    echo Processing: %%f
    powershell -ExecutionPolicy Bypass -Command "(Get-Content '%%f') -replace 'package %OLD_PACKAGE_ESCAPED%', 'package %NEW_PACKAGE_ESCAPED%' -replace 'import %OLD_PACKAGE_ESCAPED%', 'import %NEW_PACKAGE_ESCAPED%' -replace '%OLD_PACKAGE_ESCAPED%\.', '%NEW_PACKAGE_ESCAPED%.' | Set-Content '%%f' -Encoding UTF8"
    set /a JAVA_COUNT+=1
)

echo.
echo Replacing package and import statements in Groovy files...

set "GROOVY_COUNT=0"
for /r "%ROOT_DIR%" %%f in (*.groovy) do (
    echo Processing: %%f
    powershell -ExecutionPolicy Bypass -Command "(Get-Content '%%f') -replace 'package %OLD_PACKAGE_ESCAPED%', 'package %NEW_PACKAGE_ESCAPED%' -replace 'import %OLD_PACKAGE_ESCAPED%', 'import %NEW_PACKAGE_ESCAPED%' -replace '%OLD_PACKAGE_ESCAPED%\.', '%NEW_PACKAGE_ESCAPED%.' | Set-Content '%%f' -Encoding UTF8"
    set /a GROOVY_COUNT+=1
)

echo.
echo ========================================
echo Package renaming completed!
echo ========================================
echo Java files processed:   !JAVA_COUNT!
echo Groovy files processed: !GROOVY_COUNT!
echo.
echo Don't forget to:
echo 1. Rename the actual directory structure if needed
echo 2. Update build.gradle or pom.xml files
echo 3. Update configuration files that reference the old package
echo ========================================

endlocal
exit /b 0
