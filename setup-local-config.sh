#!/bin/bash

# Setup local-config directory for local development
# This creates the local-config folder outside the project directory
# with the required .env file

set -e  # Exit on any error

echo "Setting up local-config directory..."

# Get the parent directory of the current project
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd "$SCRIPT_DIR" && pwd)"
PARENT_DIR="$(dirname "$PROJECT_DIR")"

LOCAL_CONFIG_DIR="$PARENT_DIR/local-config"

# Check if local-config directory already exists
if [ -d "$LOCAL_CONFIG_DIR" ]; then
    echo "local-config directory already exists at: $LOCAL_CONFIG_DIR"
    echo
    read -p "Do you want to overwrite the existing .env file? (y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo "Setup cancelled."
        exit 0
    fi
else
    echo "Creating local-config directory at: $LOCAL_CONFIG_DIR"
    if ! mkdir -p "$LOCAL_CONFIG_DIR"; then
        echo "ERROR: Failed to create local-config directory"
        exit 1
    fi
fi

# Create .env file
echo "Creating .env file..."
cat > "$LOCAL_CONFIG_DIR/.env" << 'EOF'
# Local Development Environment Configuration
# This file is imported by application.yaml when running locally

# Active profile - used to determine which Spring profile to activate
ENVIRONMENT=local

# Add other local environment variables here as needed
# Example:
# DB_HOST=localhost
# DB_PORT=5432
# API_KEY=your-dev-api-key
EOF

if [ ! -f "$LOCAL_CONFIG_DIR/.env" ]; then
    echo "ERROR: Failed to create .env file"
    exit 1
fi

echo
echo "========================================"
echo "Setup completed successfully!"
echo "========================================"
echo
echo "Local config directory: $LOCAL_CONFIG_DIR"
echo
echo "The .env file has been created with default values."
echo "You can edit it at: $LOCAL_CONFIG_DIR/.env"
echo
echo "This configuration is automatically loaded when running:"
echo "  ./gradlew bootRun --args=\"--spring.profiles.active=local\""
echo

exit 0
