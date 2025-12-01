# SITM-MIO Graph Analysis - Quick Reference Card

## One-Line Deployment

```bash
# Complete local build and run
mvn clean package -DskipTests && java -jar target/mio-graph-analysis-1.0-SNAPSHOT-jar-with-dependencies.jar

# Deploy to x104M
./deploy.sh username x104m.example.com 22
```

---

## Essential Commands

### Build
```bash
mvn clean package -DskipTests      # Build without tests (fastest)
mvn clean package                  # Build with tests
mvn clean compile                  # Compile only
```

### Test
```bash
mvn test                           # Run all tests
mvn test -Dtest=GraphBuilderTest   # Run specific test
```

### Run Locally
```bash
java -jar target/mio-graph-analysis-1.0-SNAPSHOT-jar-with-dependencies.jar

# With custom data directory
java -jar target/mio-graph-analysis-1.0-SNAPSHOT-jar-with-dependencies.jar /path/to/data/

# With increased memory
java -Xmx4g -jar target/mio-graph-analysis-1.0-SNAPSHOT-jar-with-dependencies.jar

# In background with logging
nohup java -jar target/mio-graph-analysis-1.0-SNAPSHOT-jar-with-dependencies.jar > output.log 2>&1 &
```

---

## Remote SSH Deployment (x104M Example)

### Automated Method (Recommended)
```bash
chmod +x deploy.sh
./deploy.sh datf x104m.example.com 22
```

### Manual Method

```bash
# 1. SSH to remote
ssh datf@x104m.example.com

# 2. Setup on remote (one-time)
sudo apt-get update
sudo apt-get install -y default-jdk maven
mkdir -p ~/mio-analysis/data/MIO

# 3. Transfer from local machine
scp target/mio-graph-analysis-1.0-SNAPSHOT-jar-with-dependencies.jar \
    datf@x104m.example.com:~/mio-analysis/
scp -r data/MIO/* datf@x104m.example.com:~/mio-analysis/data/MIO/

# 4. Run on remote
ssh datf@x104m.example.com
cd ~/mio-analysis
java -jar mio-graph-analysis-1.0-SNAPSHOT-jar-with-dependencies.jar
```

---

## Directory Structure After Deployment

### Local
```
SW4---Avance/
├── target/
│   └── mio-graph-analysis-1.0-SNAPSHOT-jar-with-dependencies.jar
├── data/MIO/
│   ├── lines-241.csv
│   ├── stops-241.csv
│   └── linestops-241.csv
└── deploy.sh
```

### Remote (~/mio-analysis)
```
~/mio-analysis/
├── mio-graph-analysis-1.0-SNAPSHOT-jar-with-dependencies.jar
├── data/
│   └── MIO/
│       ├── lines-241.csv
│       ├── stops-241.csv
│       └── linestops-241.csv
└── output/  (created by application)
```

---

## System Prerequisites

| System | Command |
|---|---|
| Ubuntu/Debian | `sudo apt-get install -y default-jdk maven` |
| CentOS/RHEL | `sudo yum install -y java-17-openjdk maven` |
| macOS | `brew install openjdk@17 maven` |

---

## Expected Output Example

```
12:56:47.920 [main] INFO  com.icesi.mio.Main - Iniciando análisis de grafos SITM-MIO...
12:56:47.923 [main] INFO  com.icesi.mio.Main - === FASE 1: PARSEO DE DATOS ===
12:56:47.934 [main] INFO  com.icesi.mio.parser.LineParser - Total de rutas parseadas: 3
...
====================================================================================================
GRAFOS DE RUTAS SITM-MIO - ANÁLISIS DE ARCOS
====================================================================================================
...
Total de rutas analizadas: 3
Total de arcos generados: 10
```

---

## Troubleshooting Quick Fixes

| Problem | Solution |
|---|---|
| `java: command not found` | `sudo apt-get install -y default-jdk` |
| `mvn: command not found` | `sudo apt-get install -y maven` |
| `Archivo no encontrado` | `ls -la data/MIO/` and verify files exist |
| Out of Memory | `java -Xmx4g -jar ...` |
| NoClassDefFoundError | Ensure using `jar-with-dependencies` version |
| SSH connection fails | Check username, host, port, and network |
| Permission denied | `chmod 644 data/MIO/*.csv` |

---

## File Locations

| File | Purpose |
|---|---|
| `DEPLOYMENT_GUIDE.md` | Complete deployment documentation |
| `deploy.sh` | Automated SSH deployment script |
| `README.md` | Project overview |
| `pom.xml` | Maven configuration |
| `src/main/java` | Source code |
| `src/test/java` | Unit tests |
| `data/MIO/` | Input CSV data files |
| `target/` | Build output directory |

---

## Test Status

✅ **All tests passing**
```
Tests run: 6, Failures: 0, Errors: 0
├── CSVReaderTest (2 tests)
└── GraphBuilderTest (4 tests)
```

---

## Quick SSH Commands

```bash
# Copy file to remote
scp file.txt username@host:~/destination/

# Copy directory to remote
scp -r directory/ username@host:~/destination/

# Copy file from remote
scp username@host:~/source/file.txt .

# Interactive SSH session
ssh -p 22 username@host

# Remote command execution
ssh username@host "command to run"

# Monitor remote process
ssh username@host "tail -f logfile"
```

---

## Performance Tuning

### For Large Datasets (>1M records)

```bash
java -Xmx4g \
     -XX:+UseParallelGC \
     -XX:ParallelGCThreads=4 \
     -jar mio-graph-analysis-1.0-SNAPSHOT-jar-with-dependencies.jar
```

### Memory Recommendations
- Small data (<10k): 512MB
- Medium data (10k-100k): 1-2GB
- Large data (>100k): 4GB+

---

## Common Development Tasks

```bash
# Clean build artifacts
mvn clean

# View dependency tree
mvn dependency:tree

# Run specific test class
mvn test -Dtest=GraphBuilderTest

# Run tests with verbose output
mvn test -X

# Skip tests during build
mvn package -DskipTests

# Compile and package only (no test execution)
mvn clean compile package -DskipTests
```

---

## SSH Login Examples

```bash
# Standard SSH
ssh username@hostname

# Custom port
ssh -p 2222 username@hostname

# Save connection as alias (add to ~/.ssh/config)
Host x104m
    HostName x104m.example.com
    User datf
    Port 22

# Then use: ssh x104m
```

---

## Environment Setup (Remote Machine)

```bash
# One-time setup script
#!/bin/bash
sudo apt-get update
sudo apt-get install -y default-jdk maven
java -version
mvn -version
mkdir -p ~/mio-analysis/data/MIO
cd ~/mio-analysis
echo "Ready for application transfer"
```

---

## Post-Deployment Verification

```bash
# Check system
java -version          # Verify Java 17+
mvn -version          # Verify Maven 3.8+

# Verify data
ls -la data/MIO/      # Check CSV files exist
wc -l data/MIO/*.csv  # Count records

# Test run
java -jar target/mio-graph-analysis-1.0-SNAPSHOT-jar-with-dependencies.jar

# Check logs
tail -100 output.log  # View recent output
```

---

## Deployment Checklist

- [ ] Build successful: `mvn clean package -DskipTests`
- [ ] JAR created: `target/mio-graph-analysis-1.0-SNAPSHOT-jar-with-dependencies.jar`
- [ ] SSH access verified: `ssh username@host`
- [ ] Java installed on remote: `ssh user@host "java -version"`
- [ ] Data files ready: `ls data/MIO/*.csv`
- [ ] Application runs locally: `java -jar target/...jar`
- [ ] Application deployed to remote
- [ ] Remote application runs without errors
- [ ] Output logs are readable
- [ ] Tests pass: `mvn test`

---

## Emergency Commands

```bash
# Force kill running Java process on remote
ssh user@host "pkill -9 java"

# Check if running
ssh user@host "ps aux | grep java"

# View recent logs
ssh user@host "tail -100 ~/mio-analysis/output.log"

# Restart application
ssh user@host "cd ~/mio-analysis && \
  nohup java -jar mio-graph-analysis-1.0-SNAPSHOT-jar-with-dependencies.jar > output.log 2>&1 &"
```

---

## Version Information

- **Project Version**: 1.0-SNAPSHOT
- **Java Target**: 17+
- **Maven**: 3.8+
- **Build Status**: ✅ SUCCESS
- **Test Status**: ✅ PASSING (6/6)

---

**For detailed information, see DEPLOYMENT_GUIDE.md**
