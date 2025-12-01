# SITM-MIO Graph Analysis - Deployment & Usage Guide

## Table of Contents
1. [Overview](#overview)
2. [Prerequisites](#prerequisites)
3. [Local Setup](#local-setup)
4. [Remote Deployment via SSH](#remote-deployment-via-ssh)
5. [Running the Application](#running-the-application)
6. [Data Format Specification](#data-format-specification)
7. [Troubleshooting](#troubleshooting)

---

## Overview

**SITM-MIO Graph Analysis** is a Java application that analyzes bus route graphs for Medellín's transit system (MIO). It:
- Parses CSV files containing route, stop, and line-stop data
- Builds directed graphs with IDA (outbound) and VUELTA (return) orientations
- Generates statistical analysis and formatted console output
- Provides comprehensive logging via SLF4J/Logback

**Technology Stack:**
- Java 17+
- Maven 3.8+
- OpenCSV, JGraphT, SLF4J/Logback

---

## Prerequisites

### System Requirements
- **Operating System**: Linux/Unix (Ubuntu 20.04+, CentOS 7+, etc.)
- **Java**: Java 17 or higher
- **Maven**: 3.8.0 or higher
- **Disk Space**: ~500MB for dependencies and artifacts
- **Network**: SSH access (for remote deployment)

### Check Your System
```bash
# Check Java version (should be 17+)
java -version

# Check Maven (should be 3.8+)
mvn -version
```

---

## Local Setup

### 1. Install Java and Maven

#### Ubuntu/Debian:
```bash
sudo apt-get update
sudo apt-get install -y default-jdk maven
```

#### CentOS/RHEL:
```bash
sudo yum install -y java-17-openjdk java-17-openjdk-devel maven
```

#### macOS:
```bash
brew install openjdk@17 maven
```

### 2. Clone or Download the Project
```bash
# If from zip file
unzip SW4---Avance.zip
cd SW4---Avance

# Or if from git
git clone <repository-url>
cd SW4---Avance
```

### 3. Build the Project
```bash
# Build with all dependencies included
mvn clean package -DskipTests

# Output: target/mio-graph-analysis-1.0-SNAPSHOT-jar-with-dependencies.jar
```

### 4. Verify Build
```bash
ls -lh target/mio-graph-analysis-1.0-SNAPSHOT-jar-with-dependencies.jar
```

---

## Remote Deployment via SSH

### 1. Prepare Your CSV Data Files

Create three CSV files matching the required format (see [Data Format Specification](#data-format-specification)):
- `lines-241.csv` - Route definitions
- `stops-241.csv` - Bus stop information
- `linestops-241.csv` - Route-stop relationships

### 2. Connect to Remote Machine

```bash
# SSH into your remote machine (e.g., x104M)
ssh username@x104m.example.com
# or with custom port
ssh -p 2222 username@x104m.example.com
```

### 3. Install Prerequisites on Remote

```bash
# Update package manager
sudo apt-get update

# Install Java 17+ and Maven
sudo apt-get install -y default-jdk maven

# Verify installation
java -version
mvn -version
```

### 4. Transfer Project to Remote

From your **local machine**:

```bash
# Copy entire project directory
scp -r SW4---Avance/ username@x104m.example.com:~/

# Or copy just the JAR and data
scp target/mio-graph-analysis-1.0-SNAPSHOT-jar-with-dependencies.jar \
    username@x104m.example.com:~/mio-analysis/

scp -r data/MIO/ username@x104m.example.com:~/mio-analysis/data/
```

### 5. Prepare Remote Directory Structure

From your **remote machine** (via SSH):

```bash
# Create application directory
mkdir -p ~/mio-analysis/data/MIO
cd ~/mio-analysis

# Create output directory for results
mkdir -p output/
```

### 6. Upload CSV Data Files

From your **local machine**:

```bash
# Transfer your CSV files to remote
scp your-data/lines-241.csv username@x104m.example.com:~/mio-analysis/data/MIO/
scp your-data/stops-241.csv username@x104m.example.com:~/mio-analysis/data/MIO/
scp your-data/linestops-241.csv username@x104m.example.com:~/mio-analysis/data/MIO/
```

---

## Running the Application

### Option 1: Run with Default Data Location

```bash
# Assumes data files are in: data/MIO/
cd ~/mio-analysis
java -jar mio-graph-analysis-1.0-SNAPSHOT-jar-with-dependencies.jar
```

### Option 2: Run with Custom Data Location

```bash
# Specify custom data directory as argument
java -jar mio-graph-analysis-1.0-SNAPSHOT-jar-with-dependencies.jar /path/to/data/

# Example:
java -jar mio-graph-analysis-1.0-SNAPSHOT-jar-with-dependencies.jar ~/mio-data/
```

### Expected Output

```
12:56:47.920 [main] INFO  com.icesi.mio.Main - Iniciando análisis de grafos SITM-MIO...
12:56:47.922 [main] INFO  com.icesi.mio.Main - Archivo encontrado: data/MIO/lines-241.csv
...
====================================================================================================
GRAFOS DE RUTAS SITM-MIO - ANÁLISIS DE ARCOS
====================================================================================================
...
RESUMEN GENERAL
Total de rutas analizadas: N
Total de arcos generados: M
Total de paradas (con duplicados entre rutas): P
```

### Run in Background (with Output Logging)

```bash
# Run in background and save logs
nohup java -jar mio-graph-analysis-1.0-SNAPSHOT-jar-with-dependencies.jar > analysis_output.log 2>&1 &

# Check logs
tail -f analysis_output.log
```

### Run with Custom Logging Configuration

Create `logback.xml` in the same directory:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <appender name="FILE" class="ch.qos.logback.core.FileAppender">
        <file>mio-analysis.log</file>
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
    </root>
</configuration>
```

---

## Data Format Specification

### File 1: lines-241.csv (Routes)

**Header:**
```
LINEID,PLANVERSIONID,SHORTNAME,DESCRIPTION,PLANVERSIONID,ACTIVATIONDATE,CREATIONDATE
```

**Example:**
```
1,241,T01,Centro - Norte,241,2024-01-15 10:30:00.000,2024-01-14 09:00:00.000
2,241,T02,Oriente - Occidente,241,2024-01-16 10:30:00.000,2024-01-15 09:00:00.000
```

**Field Descriptions:**
- `LINEID`: Unique route identifier (integer)
- `PLANVERSIONID`: Plan version identifier (integer)
- `SHORTNAME`: Short route code (string, e.g., "T01")
- `DESCRIPTION`: Full route name (string)
- `ACTIVATIONDATE`: ISO date-time format (yyyy-MM-dd HH:mm:ss.SSS)
- `CREATIONDATE`: ISO date-time format

---

### File 2: stops-241.csv (Bus Stops)

**Header:**
```
STOPID,PLANVERSIONID,SHORTNAME,LONGNAME,GPSX,GPSY,DECIMALLONG,DECIMALLAT
```

**Example:**
```
101,241,P1,Estación Central,1000000,1000000,-75.5123,3.4567
102,241,P2,Parque Arví,1010000,1005000,-75.5087,3.4589
```

**Field Descriptions:**
- `STOPID`: Unique stop identifier (integer)
- `PLANVERSIONID`: Plan version identifier (integer)
- `SHORTNAME`: Short stop code (string)
- `LONGNAME`: Full stop name (string)
- `GPSX`: GPS X coordinate (long integer)
- `GPSY`: GPS Y coordinate (long integer)
- `DECIMALLONG`: Decimal longitude (double)
- `DECIMALLAT`: Decimal latitude (double)

---

### File 3: linestops-241.csv (Route-Stop Relationships)

**Header:**
```
LINESTOPID,STOPSEQUENCE,ORIENTATION,LINEID,STOPID,PLANVERSIONID,LINEVARIANT,LINEVARIANTTYPE
```

**Example:**
```
1,1,0,1,101,241,VAR-A,Normal
2,2,0,1,102,241,VAR-A,Normal
3,3,0,1,103,241,VAR-A,Normal
4,1,1,1,103,241,VAR-A,Normal
5,2,1,1,102,241,VAR-A,Normal
6,3,1,1,101,241,VAR-A,Normal
```

**Field Descriptions:**
- `LINESTOPID`: Unique identifier for this relationship (integer)
- `STOPSEQUENCE`: Order of stop in route (integer, 1-based)
- `ORIENTATION`: 0 = IDA (outbound), 1 = VUELTA (return) (integer)
- `LINEID`: Route identifier (integer, must match lines-241.csv)
- `STOPID`: Stop identifier (integer, must match stops-241.csv)
- `PLANVERSIONID`: Plan version identifier (integer)
- `LINEVARIANT`: Route variant name (string, optional)
- `LINEVARIANTTYPE`: Variant type (string, optional)

---

## Troubleshooting

### Issue: "java: command not found"

**Solution:**
```bash
# Install Java
sudo apt-get install -y default-jdk

# Or check if it's installed in a non-standard location
which java
```

### Issue: "mvn: command not found"

**Solution:**
```bash
# Install Maven
sudo apt-get install -y maven

# Or add to PATH if already installed
export PATH="/opt/maven/bin:$PATH"
```

### Issue: "Archivo no encontrado" (File not found)

**Solution:**
```bash
# Verify data files exist and are readable
ls -la data/MIO/

# Ensure correct permissions
chmod 644 data/MIO/*.csv

# Run from correct directory
pwd
cd ~/mio-analysis
```

### Issue: "NoClassDefFoundError: org/slf4j/LoggerFactory"

**Solution:**
```bash
# Ensure you're using the jar-with-dependencies version
ls target/*jar-with-dependencies*

# Verify the JAR exists
java -jar mio-graph-analysis-1.0-SNAPSHOT-jar-with-dependencies.jar
```

### Issue: Out of Memory

**Solution:**
```bash
# Increase heap memory
java -Xmx2g -Xms512m -jar mio-graph-analysis-1.0-SNAPSHOT-jar-with-dependencies.jar

# Or for large datasets
java -Xmx4g -jar mio-graph-analysis-1.0-SNAPSHOT-jar-with-dependencies.jar
```

### Issue: Permission Denied

**Solution:**
```bash
# Make JAR executable (usually not needed, but just in case)
chmod +x mio-graph-analysis-1.0-SNAPSHOT-jar-with-dependencies.jar

# Check ownership
ls -l mio-graph-analysis-1.0-SNAPSHOT-jar-with-dependencies.jar

# Change if needed
chown $USER:$USER mio-graph-analysis-1.0-SNAPSHOT-jar-with-dependencies.jar
```

---

## Testing the Installation

### Quick Verification

```bash
# 1. Check Java
java -version

# 2. Verify JAR file
jar tf mio-graph-analysis-1.0-SNAPSHOT-jar-with-dependencies.jar | head -20

# 3. Test with included test data
java -jar mio-graph-analysis-1.0-SNAPSHOT-jar-with-dependencies.jar

# 4. Check output
ls -la output/
```

### Run Unit Tests

```bash
# On local machine or after cloning full project
mvn clean test

# Expected output:
# Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
# BUILD SUCCESS
```

---

## Deployment Checklist

- [ ] Java 17+ installed and working
- [ ] Maven 3.8+ installed and working
- [ ] Project cloned or downloaded locally
- [ ] Project builds successfully (`mvn clean package`)
- [ ] JAR file created: `mio-graph-analysis-1.0-SNAPSHOT-jar-with-dependencies.jar`
- [ ] SSH access to remote machine verified
- [ ] Required system space available (~500MB)
- [ ] CSV data files prepared in correct format
- [ ] Data files transferred to remote machine
- [ ] Application runs without errors
- [ ] Output logs are readable and meaningful

---

## Quick SSH Deployment Script

Save this as `deploy.sh` on your local machine:

```bash
#!/bin/bash

# Configuration
REMOTE_USER="username"
REMOTE_HOST="x104m.example.com"
REMOTE_PORT="22"
PROJECT_DIR="SW4---Avance"
REMOTE_APP_DIR="mio-analysis"

echo "Building project locally..."
cd $PROJECT_DIR
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "Build failed!"
    exit 1
fi

echo "Connecting to remote: $REMOTE_USER@$REMOTE_HOST"

# Create remote directories
ssh -p $REMOTE_PORT $REMOTE_USER@$REMOTE_HOST "mkdir -p ~/$REMOTE_APP_DIR/data/MIO"

# Transfer JAR
echo "Transferring JAR file..."
scp -P $REMOTE_PORT target/mio-graph-analysis-1.0-SNAPSHOT-jar-with-dependencies.jar \
    $REMOTE_USER@$REMOTE_HOST:~/$REMOTE_APP_DIR/

# Transfer data
echo "Transferring data files..."
scp -P $REMOTE_PORT -r data/MIO/* \
    $REMOTE_USER@$REMOTE_HOST:~/$REMOTE_APP_DIR/data/MIO/

echo "Deployment complete!"
echo "To run on remote:"
echo "ssh -p $REMOTE_PORT $REMOTE_USER@$REMOTE_HOST"
echo "cd ~/$REMOTE_APP_DIR"
echo "java -jar mio-graph-analysis-1.0-SNAPSHOT-jar-with-dependencies.jar"
```

**Usage:**
```bash
chmod +x deploy.sh
./deploy.sh
```

---

## Support & Debugging

### Generate Debug Report

```bash
#!/bin/bash
# Save as debug_info.sh

echo "=== System Information ==="
uname -a
echo ""

echo "=== Java Version ==="
java -version
echo ""

echo "=== Maven Version ==="
mvn -version
echo ""

echo "=== Data Files ==="
ls -lh data/MIO/
echo ""

echo "=== Disk Space ==="
df -h
echo ""

echo "=== Memory Usage ==="
free -h
```

---

## Performance Optimization

For large datasets (>1M records):

```bash
# Increase heap memory and use parallel garbage collection
java -Xmx4g \
     -XX:+UseParallelGC \
     -XX:ParallelGCThreads=4 \
     -jar mio-graph-analysis-1.0-SNAPSHOT-jar-with-dependencies.jar
```

---

## Next Steps

1. Prepare your CSV data files following the format specification
2. Follow the deployment steps for your target environment
3. Run the application and verify output
4. Check logs for any issues
5. Adjust memory settings if needed for large datasets

For issues or questions, refer to the troubleshooting section or check application logs.
