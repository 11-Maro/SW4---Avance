#!/bin/bash

################################################################################
# SITM-MIO Graph Analysis - Quick Deployment Script
# This script automates the deployment of the application to a remote machine
################################################################################

# Color output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Configuration - MODIFY THESE VALUES
REMOTE_USER="${1:-username}"
REMOTE_HOST="${2:-x104m.example.com}"
REMOTE_PORT="${3:-22}"
PROJECT_DIR="$(pwd)"
REMOTE_APP_DIR="mio-analysis"

echo -e "${YELLOW}========================================${NC}"
echo -e "${YELLOW}SITM-MIO Graph Analysis - Deployment${NC}"
echo -e "${YELLOW}========================================${NC}"
echo ""

# Validate inputs
if [ "$REMOTE_USER" = "username" ] || [ "$REMOTE_HOST" = "x104m.example.com" ]; then
    echo -e "${RED}Error: Please provide remote user and host${NC}"
    echo ""
    echo "Usage: ./deploy.sh <username> <hostname> [port]"
    echo "Example: ./deploy.sh datf x104m.example.com 22"
    exit 1
fi

echo "Configuration:"
echo "  Remote User: $REMOTE_USER"
echo "  Remote Host: $REMOTE_HOST"
echo "  Remote Port: $REMOTE_PORT"
echo "  Remote App Dir: ~/$REMOTE_APP_DIR"
echo ""

# Step 1: Build the project
echo -e "${YELLOW}[1/5] Building project locally...${NC}"
mvn clean package -DskipTests -q

if [ $? -ne 0 ]; then
    echo -e "${RED}✗ Build failed!${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Build successful${NC}"
echo ""

# Step 2: Verify JAR file
echo -e "${YELLOW}[2/5] Verifying JAR file...${NC}"
JAR_FILE="target/mio-graph-analysis-1.0-SNAPSHOT-jar-with-dependencies.jar"
if [ ! -f "$JAR_FILE" ]; then
    echo -e "${RED}✗ JAR file not found: $JAR_FILE${NC}"
    exit 1
fi
JAR_SIZE=$(du -h "$JAR_FILE" | cut -f1)
echo -e "${GREEN}✓ JAR file verified (Size: $JAR_SIZE)${NC}"
echo ""

# Step 3: Test SSH connection
echo -e "${YELLOW}[3/5] Testing SSH connection...${NC}"
ssh -p $REMOTE_PORT -o ConnectTimeout=5 $REMOTE_USER@$REMOTE_HOST "echo 'SSH connection successful'" > /dev/null 2>&1

if [ $? -ne 0 ]; then
    echo -e "${RED}✗ SSH connection failed${NC}"
    echo "Verify:"
    echo "  - Correct username and hostname"
    echo "  - SSH access is enabled"
    echo "  - Network connection is available"
    exit 1
fi
echo -e "${GREEN}✓ SSH connection successful${NC}"
echo ""

# Step 4: Prepare remote directories
echo -e "${YELLOW}[4/5] Preparing remote directories...${NC}"
ssh -p $REMOTE_PORT $REMOTE_USER@$REMOTE_HOST "mkdir -p ~/$REMOTE_APP_DIR/data/MIO ~/$REMOTE_APP_DIR/output"

if [ $? -ne 0 ]; then
    echo -e "${RED}✗ Failed to create remote directories${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Remote directories created${NC}"
echo ""

# Step 5: Transfer files
echo -e "${YELLOW}[5/5] Transferring files to remote...${NC}"

# Transfer JAR
echo "  Transferring JAR..."
scp -P $REMOTE_PORT -q "$JAR_FILE" \
    $REMOTE_USER@$REMOTE_HOST:~/$REMOTE_APP_DIR/

if [ $? -ne 0 ]; then
    echo -e "${RED}✗ Failed to transfer JAR${NC}"
    exit 1
fi
echo -e "  ${GREEN}✓ JAR transferred${NC}"

# Transfer data files if they exist
if [ -d "data/MIO" ]; then
    echo "  Transferring data files..."
    scp -P $REMOTE_PORT -q -r data/MIO/* \
        $REMOTE_USER@$REMOTE_HOST:~/$REMOTE_APP_DIR/data/MIO/ 2>/dev/null
    
    if [ $? -eq 0 ]; then
        echo -e "  ${GREEN}✓ Data files transferred${NC}"
    else
        echo -e "  ${YELLOW}⚠ No data files found (this is OK if data is already on remote)${NC}"
    fi
fi

echo ""
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}✓ Deployment completed successfully!${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""

# Print next steps
echo "Next steps:"
echo "1. SSH into the remote machine:"
echo "   ssh -p $REMOTE_PORT $REMOTE_USER@$REMOTE_HOST"
echo ""
echo "2. Navigate to application directory:"
echo "   cd ~/$REMOTE_APP_DIR"
echo ""
echo "3. Run the application:"
echo "   java -jar mio-graph-analysis-1.0-SNAPSHOT-jar-with-dependencies.jar"
echo ""
echo "4. Or run in background with logging:"
echo "   nohup java -jar mio-graph-analysis-1.0-SNAPSHOT-jar-with-dependencies.jar > output.log 2>&1 &"
echo ""
echo "For more information, see DEPLOYMENT_GUIDE.md"
