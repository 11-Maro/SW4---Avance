# START HERE - SITM-MIO Graph Analysis

## 🎯 Welcome!

This project analyzes bus route graphs from Medellín's SITM-MIO transit system. This guide will help you get started.

---

## 📚 Documentation Files

| File | Purpose | Audience |
|---|---|---|
| **README.md** | Project overview and features | Everyone |
| **QUICK_REFERENCE.md** | Essential commands cheat sheet | Developers |
| **DEPLOYMENT_GUIDE.md** | Complete deployment manual | DevOps/Admins |
| **deploy.sh** | Automated SSH deployment script | Everyone |

---

## ⚡ 30-Second Quick Start

```bash
# 1. Build locally (one-time)
mvn clean package -DskipTests

# 2. Run the application
java -jar target/mio-graph-analysis-1.0-SNAPSHOT-jar-with-dependencies.jar

# 3. Expected output: Route analysis with arc sequences
```

---

## 🚀 Deploy to Remote Machine (x104M Example)

### Automated (Recommended)
```bash
chmod +x deploy.sh
./deploy.sh datf x104m.example.com 22
```

### Manual Steps
1. Build locally: `mvn clean package -DskipTests`
2. SSH to remote: `ssh datf@x104m.example.com`
3. Install Java: `sudo apt-get install -y default-jdk maven`
4. Create directory: `mkdir -p ~/mio-analysis/data/MIO`
5. Transfer files from local:
   ```bash
   scp target/mio-graph-analysis-1.0-SNAPSHOT-jar-with-dependencies.jar \
       datf@x104m.example.com:~/mio-analysis/
   scp -r data/MIO/* datf@x104m.example.com:~/mio-analysis/data/MIO/
   ```
6. Run on remote:
   ```bash
   ssh datf@x104m.example.com
   cd ~/mio-analysis
   java -jar mio-graph-analysis-1.0-SNAPSHOT-jar-with-dependencies.jar
   ```

---

## ✅ What's Included

### Source Code
- ✅ Complete Java application (src/main/java)
- ✅ Unit tests (src/test/java)
- ✅ All 6 tests passing

### Build & Deployment
- ✅ Maven configuration (pom.xml)
- ✅ Ready-to-run JAR file (6.2MB with dependencies)
- ✅ Automated deployment script

### Documentation
- ✅ Complete deployment guide
- ✅ Quick reference card
- ✅ Project README
- ✅ This file

### Test Data
- ✅ Sample CSV files (lines-241.csv, stops-241.csv, linestops-241.csv)

---

## 🛠️ System Requirements

- **Java 17+** (or newer)
- **Maven 3.8+**
- **Linux/Unix** (Ubuntu, CentOS, etc.)
- **SSH access** (for remote deployment)
- **~500MB disk space**

### Quick Check
```bash
java -version    # Should show Java 17+
mvn -version     # Should show Maven 3.8+
ssh -V           # Should show OpenSSH version
```

---

## 📋 Installation & Setup

### Install Java & Maven (Ubuntu/Debian)
```bash
sudo apt-get update
sudo apt-get install -y default-jdk maven
```

### Install Java & Maven (CentOS/RHEL)
```bash
sudo yum install -y java-17-openjdk maven
```

---

## 🎯 Your First Steps

### Option A: Run Locally
```bash
# In the SW4---Avance directory
mvn clean package -DskipTests
java -jar target/mio-graph-analysis-1.0-SNAPSHOT-jar-with-dependencies.jar
```

You should see formatted route analysis in the console output.

### Option B: Deploy to Remote
```bash
# From your local machine in the SW4---Avance directory
chmod +x deploy.sh
./deploy.sh your_username remote_host.com 22

# Then SSH to remote_host.com and run:
cd ~/mio-analysis
java -jar mio-graph-analysis-1.0-SNAPSHOT-jar-with-dependencies.jar
```

---

## 📊 Data Files

The application expects three CSV files:

1. **lines-241.csv** - Bus routes
2. **stops-241.csv** - Bus stops
3. **linestops-241.csv** - Route-stop relationships

These should be placed in: `data/MIO/`

---

## 🧪 Run Tests

```bash
# Run all tests
mvn test

# Expected result: All 6 tests pass ✅
```

---

## 🔍 Understanding the Output

When you run the application, you'll see:

1. **Parsing Phase**: Loading CSV data
2. **Building Phase**: Creating graph structures
3. **Analysis Phase**: Formatted route output showing:
   - Route name and ID
   - IDA (outbound) arc sequences
   - VUELTA (return) arc sequences
   - Stop connectivity

Example:
```
RUTA: T01 - Centro - Norte (ID: 1)
  IDA (2 arcos):
    1. Estación Central → Parque Arví
    2. Parque Arví → Universidad
  VUELTA (2 arcos):
    1. Universidad → Parque Arví
    2. Parque Arví → Estación Central
```

---

## ⚠️ Troubleshooting

| Issue | Fix |
|---|---|
| `java: command not found` | `sudo apt-get install -y default-jdk` |
| `mvn: command not found` | `sudo apt-get install -y maven` |
| `Archivo no encontrado` | Check CSV files in `data/MIO/` |
| SSH connection fails | Verify username, host, and network |
| Out of Memory | Use `java -Xmx4g -jar ...` |

**For more help:** See `DEPLOYMENT_GUIDE.md` Troubleshooting section

---

## 📖 Reading Guide

1. **New to the project?** → Read `README.md`
2. **Need to deploy?** → Use `deploy.sh` or `DEPLOYMENT_GUIDE.md`
3. **Quick commands?** → Check `QUICK_REFERENCE.md`
4. **Having problems?** → See `DEPLOYMENT_GUIDE.md` Troubleshooting

---

## 🎓 Project Structure

```
SW4---Avance/
├── src/                          # Source code
│   ├── main/java/...            # Application code
│   └── test/java/...            # Unit tests
├── data/MIO/                     # CSV data files
├── target/                       # Build output
│   └── ...jar-with-dependencies  # Executable JAR
├── pom.xml                       # Maven config
├── README.md                     # Project overview
├── DEPLOYMENT_GUIDE.md           # Full deployment guide
├── QUICK_REFERENCE.md            # Commands cheat sheet
├── deploy.sh                     # SSH deployment script
└── START_HERE.md                 # This file
```

---

## 🚢 Deployment Methods Comparison

| Method | Speed | Automation | Recommended For |
|---|---|---|---|
| **Automated (deploy.sh)** | ⚡ Fast | ✅ Full | Most users |
| **Manual SSH** | 🐢 Slow | ❌ None | Learning |
| **Local execution** | ⚡ Fastest | ✅ Full | Testing |

---

## 📞 Need Help?

1. **Check README.md** - Project overview
2. **Check QUICK_REFERENCE.md** - Common commands
3. **Check DEPLOYMENT_GUIDE.md** - Detailed instructions
4. **Check troubleshooting** - Known issues and fixes

---

## ✨ Features

- ✅ Fast CSV parsing (OpenCSV)
- ✅ Directed graph construction
- ✅ IDA/VUELTA orientation support
- ✅ Statistical analysis
- ✅ Formatted console output
- ✅ Comprehensive logging
- ✅ Full test coverage
- ✅ Remote deployment ready

---

## 📊 Test Results

```
✅ Total Tests: 6
✅ Passed: 6
✅ Failed: 0
✅ Success Rate: 100%
```

**Test Classes:**
- CSVReaderTest (2 tests)
- GraphBuilderTest (4 tests)

---

## 🔧 Common Commands

```bash
# Build
mvn clean package -DskipTests

# Test
mvn test

# Run Locally
java -jar target/mio-graph-analysis-1.0-SNAPSHOT-jar-with-dependencies.jar

# Deploy
./deploy.sh username hostname port

# In Background
nohup java -jar target/...jar > output.log 2>&1 &
```

---

## 🎯 Next Steps

1. **Ensure Java & Maven are installed**
   ```bash
   java -version && mvn -version
   ```

2. **Build the project**
   ```bash
   cd SW4---Avance
   mvn clean package -DskipTests
   ```

3. **Run locally to verify**
   ```bash
   java -jar target/mio-graph-analysis-1.0-SNAPSHOT-jar-with-dependencies.jar
   ```

4. **Deploy to remote (optional)**
   ```bash
   chmod +x deploy.sh
   ./deploy.sh username hostname port
   ```

5. **Check DEPLOYMENT_GUIDE.md for advanced options**

---

## 📝 Version Information

- **Project Version**: 1.0-SNAPSHOT
- **Java Target**: 17+
- **Maven**: 3.8+
- **Status**: ✅ Production Ready
- **Last Updated**: 2025-11-29

---

## 🎓 Learning Resources

Inside this project you'll find:

1. **Model classes** - Understanding data structures
2. **Parser classes** - CSV file processing
3. **GraphBuilder** - Graph construction algorithms
4. **Unit tests** - Test patterns and examples
5. **Documentation** - Complete deployment guides

---

## 🚀 You're Ready!

Everything is set up and ready to go. Choose your path:

**I want to run locally** → Execute `mvn clean package -DskipTests` then the JAR file

**I want to deploy to x104M** → Run `./deploy.sh` with your credentials

**I need detailed help** → Open `DEPLOYMENT_GUIDE.md`

**I need quick commands** → Check `QUICK_REFERENCE.md`

---

**Good luck! 🎉**

For detailed information and advanced options, see the documentation files.
