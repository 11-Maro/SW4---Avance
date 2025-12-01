# SITM-MIO Graph Analysis

Proyecto de análisis de grafos de rutas del Sistema Integrado de Transporte Masivo (SITM-MIO) de Cali, Colombia.

## 📋 Descripción

Este proyecto construye grafos dirigidos a partir de los datos de rutas, paradas y relaciones parada-ruta del MIO. Cada ruta genera dos grafos:
- **IDA** (orientación 0): Secuencia de paradas en sentido de ida
- **VUELTA** (orientación 1): Secuencia de paradas en sentido de vuelta

Los **nodos** son las paradas y las **aristas** son los arcos entre paradas consecutivas.

## 👥 Integrantes

- Nicolás Gongora
- Manuel Rojas
- Alejandro Troya
  
## 🏗️ Estructura del Proyecto

```
SW4---Avance/
├── src/main/java/com/icesi/mio/
│   ├── Main.java                  # Punto de entrada
│   ├── model/                     # Modelos de dominio
│   │   ├── Line.java
│   │   ├── Stop.java
│   │   ├── LineStop.java
│   │   ├── Arc.java
│   │   └── RouteGraph.java
│   ├── parser/                    # Parsers CSV
│   │   ├── CSVReader.java
│   │   ├── LineParser.java
│   │   ├── StopParser.java
│   │   └── LineStopParser.java
│   ├── graph/                     # Lógica de grafos
│   │   ├── GraphBuilder.java
│   │   └── GraphPrinter.java
│   └── util/
│       └── Constants.java
├── data/MIO/                      # Archivos CSV (gitignored)
├── output/                        # Salidas y logs
└── pom.xml                        # Configuración Maven
```

## 🚀 Requisitos

- **Java 17** o superior
- **Maven 3.8+**
- Archivos CSV del MIO:
  - `lines-241.csv` ✅
  - `stops-241.csv` ✅
  - `linestops-241.csv` ✅

## ⚡ Quick Start

### Build & Run Locally
```bash
# Build with all dependencies
mvn clean package -DskipTests

# Run application
java -jar target/mio-graph-analysis-1.0-SNAPSHOT-jar-with-dependencies.jar
```

### Deploy to Remote Machine (SSH)
```bash
# Make script executable
chmod +x deploy.sh

# Deploy (e.g., to x104M)
./deploy.sh username x104m.example.com 22
```

## 📦 Instalación

1. Clonar el repositorio:
```bash
git clone <repository-url>
cd SW4---Avance
```

2. Descargar dependencias y compilar:
```bash
mvn clean compile
```

3. Empaquetar (opcional):
```bash
mvn package
```

## ▶️ Ejecución

### Opción 1: Con Maven
```bash
mvn exec:java -Dexec.mainClass="com.icesi.mio.Main"
```

### Opción 2: JAR compilado
```bash
mvn package
java -jar target/mio-graph-analysis-1.0-SNAPSHOT.jar
```

### Opción 3: Especificar directorio de datos
```bash
mvn exec:java -Dexec.mainClass="com.icesi.mio.Main" -Dexec.args="data/MIO/"
```

## 📊 Salida

El programa genera:

1. **Consola**: Lista ordenada de arcos por ruta (IDA y VUELTA)
2. **Log**: Archivo `output/mio-analysis.log` con detalles del procesamiento

### Ejemplo de salida:

```
============================================================================================
RUTA: T31 - Terminal Paso del Comercio - Universidades (ID: 131)
--------------------------------------------------------------------------------------------

  IDA (45 arcos):
  ------------------------------------------------------------------------------------------
  SEQ   PARADA ORIGEN                  ID         -> PARADA DESTINO                ID        
  ------------------------------------------------------------------------------------------
  1     Terminal Paso del Comercio     511409     -> Kr 109 con Cl 42              511412    
  2     Kr 109 con Cl 42               511412     -> Cl 42 entre Kr109 y 112       511413    
  ...

  VUELTA (43 arcos):
  ...

RESUMEN: 88 paradas, 88 arcos (IDA: 45, VUELTA: 43)
```

## 🧪 Testing

```bash
mvn test
```

## 📚 Dependencias

- **OpenCSV 5.9**: Parsing de archivos CSV
- **JGraphT 1.5.2**: Estructuras de datos para grafos
- **SLF4J + Logback**: Logging
- **JUnit 5**: Testing

## 🛠️ Comandos Útiles

```bash
# Limpiar build anterior
mvn clean

# Compilar sin tests
mvn compile -DskipTests

# Ver árbol de dependencias
mvn dependency:tree

# Ejecutar tests específicos
mvn test -Dtest=GraphBuilderTest
```

## 📖 Documentation

- **DEPLOYMENT_GUIDE.md** - Complete deployment guide with SSH instructions and troubleshooting
- **deploy.sh** - Automated SSH deployment script

## 📁 Archivos de Datos

Los archivos CSV deben seguir el formato:

### lines-241.csv
```csv
LINEID,PLANVERSIONID,SHORTNAME,DESCRIPTION,ACTIVATIONDATE
131,241,T31,Terminal Paso del Comercio - Universidades,2018-05-15 00:00:00.000
```

### stops-241.csv
```csv
STOPID,PLANVERSIONID,SHORTNAME,LONGNAME,GPS_X,GPS_Y,DECIMALLONG,DECIMALLATIT
511409,241,K109C421,Kr 109 con Cl 42,-763106233,32130942,-76.51839806,3.358595
```

### linestops-241.csv
```csv
LINESTOPID,STOPSEQUENCE,ORIENTATION,LINEID,STOPID,PLANVERSIONID,LINEVARIANT,LINEVARIANTTYPE
1452640,34,0,2515,1446824,131,,
```



## 🎯 Entregables

### ✅ A. Código en Java
Código que construye el grafo de paradas y arcos, mostrando la lista ordenada de arcos en secuencia (ida y vuelta) por ruta en consola.

### 🎨 B. Visualización Gráfica (BONUS - Pendiente)
Implementación de visualización gráfica usando Java2D exportando a JPG.

---

**Fecha**: Noviembre 2025
