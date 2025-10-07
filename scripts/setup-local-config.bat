@echo off
REM Setup local-config directory for local development
REM This creates the local-config folder outside the project directory
REM with the required .env file

setlocal

echo Setting up local-config directory...

REM Get the parent directory of the current project
for %%I in ("%~dp0.") do set "PROJECT_DIR=%%~fI"
for %%I in ("%PROJECT_DIR%\..") do set "PARENT_DIR=%%~fI"

set "LOCAL_CONFIG_DIR=%PARENT_DIR%\local-config"

REM Check if local-config directory already exists
if exist "%LOCAL_CONFIG_DIR%" (
    echo local-config directory already exists at: %LOCAL_CONFIG_DIR%
    echo.
    choice /C YN /M "Do you want to overwrite the existing .env file?"
    if errorlevel 2 (
        echo Setup cancelled.
        exit /b 0
    )
) else (
    echo Creating local-config directory at: %LOCAL_CONFIG_DIR%
    mkdir "%LOCAL_CONFIG_DIR%"
    if errorlevel 1 (
        echo ERROR: Failed to create local-config directory
        exit /b 1
    )
)

REM Create .env file
echo Creating .env file...
echo # Local Development Environment Configuration > "%LOCAL_CONFIG_DIR%\.env"
echo # This file is imported by application.yaml when running locally >> "%LOCAL_CONFIG_DIR%\.env"
echo. >> "%LOCAL_CONFIG_DIR%\.env"
echo # Active profile - used to determine which Spring profile to activate >> "%LOCAL_CONFIG_DIR%\.env"
echo ENVIRONMENT=local >> "%LOCAL_CONFIG_DIR%\.env"
echo. >> "%LOCAL_CONFIG_DIR%\.env"
echo # Add other local environment variables here as needed >> "%LOCAL_CONFIG_DIR%\.env"
echo # Example: >> "%LOCAL_CONFIG_DIR%\.env"
echo # DB_HOST=localhost >> "%LOCAL_CONFIG_DIR%\.env"
echo # DB_PORT=5432 >> "%LOCAL_CONFIG_DIR%\.env"
echo # API_KEY=your-dev-api-key >> "%LOCAL_CONFIG_DIR%\.env"

if not exist "%LOCAL_CONFIG_DIR%\.env" (
    echo ERROR: Failed to create .env file
    exit /b 1
)

echo.
echo ========================================
echo Setup completed successfully!
echo ========================================
echo.
echo Local config directory: %LOCAL_CONFIG_DIR%
echo.
echo The .env file has been created with default values.
echo You can edit it at: %LOCAL_CONFIG_DIR%\.env
echo.
echo This configuration is automatically loaded when running:
echo   gradlew.bat bootRun --args="--spring.profiles.active=local"
echo.

endlocal
exit /b 0
