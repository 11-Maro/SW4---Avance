# Implementación del Subsistema de Cálculo de Velocidades Promedio

## Resumen

Se ha implementado completamente el subsistema para automatizar el cálculo de la velocidad promedio de todos los arcos del grafo que representa las rutas del sistema SITM-MIO.

## Funcionalidades Implementadas

### 1. Procesamiento de Datos Históricos ✅

- **Archivo**: `datagrams4history.csv` (24GB)
- **Procesador**: `StreamingFullFileProcessor`
- **Funcionalidad**: 
  - Lee el archivo CSV línea por línea (streaming) para manejar archivos grandes sin cargar todo en memoria
  - Calcula velocidades entre puntos GPS consecutivos del mismo bus
  - Asigna cada par de puntos al arco más cercano usando `ArcMatcher`
  - Acumula velocidades promedio por arco

### 2. Procesamiento de Datos de Streaming (BONUS) ✅

- **Archivo**: `datagrams4streaming.csv` (170MB)
- **Procesador**: `StreamingFullFileProcessor`
- **Funcionalidad**:
  - Similar al procesamiento histórico
  - Los resultados de streaming tienen prioridad sobre los históricos
  - Permite actualización en tiempo "real" de las velocidades

### 3. Cálculo de Velocidades

El sistema calcula velocidades usando:
- **Fórmula de Haversine**: Para calcular la distancia entre dos puntos GPS
- **Tiempo delta**: Diferencia entre timestamps de datagramas consecutivos
- **Filtros**:
  - Velocidades válidas: 0 < velocidad < 200 km/h
  - Distancia máxima del punto al arco: 200 metros
  - Validación de lineId y coordenadas

### 4. Visualización de Resultados

- **GraphPrinter mejorado**: Muestra arcos con sus velocidades promedio
- **Estadísticas**:
  - Total de arcos con velocidad calculada
  - Velocidad promedio general
  - Velocidad mínima y máxima
  - Porcentaje de arcos con datos

## Archivos Modificados/Creados

### Modificados:
1. **Main.java**: 
   - Integra procesamiento de datos históricos y streaming
   - Orquesta todo el flujo de trabajo
   - Maneja errores y logging

2. **GraphPrinter.java**:
   - Nuevo método `printAllGraphsWithSpeeds()` para mostrar velocidades
   - Estadísticas mejoradas con información de velocidades
   - Formato mejorado de salida

### Utilizados (ya existían):
- `StreamingFullFileProcessor.java`: Procesador de archivos grandes
- `ArcMatcher.java`: Asigna puntos GPS a arcos
- `ArcIndexBuilder.java`: Construye índice de arcos
- `PartialResult.java`: Almacena resultados parciales

## Flujo de Ejecución

1. **FASE 1**: Parseo de datos (lines, stops, linestops)
2. **FASE 2**: Construcción de grafos de rutas
3. **FASE 3**: Cálculo de velocidades promedio usando datos históricos
4. **FASE 4**: Actualización con datos de streaming (BONUS)
5. **FASE 5**: Visualización de resultados con velocidades

## Ejecución

```bash
# Compilar
mvn clean package -DskipTests

# Ejecutar
java -Xmx8G -jar target/mio-graph-analysis-1.0-SNAPSHOT-jar-with-dependencies.jar

# O con Maven
mvn exec:java -Dexec.mainClass="com.icesi.mio.Main"
```

## Notas Importantes

- El archivo histórico (24GB) puede tardar varias horas en procesarse completamente
- El procesamiento es streaming, por lo que no carga todo el archivo en memoria
- El programa muestra progreso cada 1 millón de filas procesadas
- Los resultados se muestran en consola con formato tabular
- El log completo se guarda en `output/execution.log`

## Estado Actual

✅ Compilación exitosa
✅ Procesamiento de datos históricos en curso
✅ Procesamiento de streaming implementado
✅ Visualización de resultados implementada
✅ Manejo de errores implementado

El programa está ejecutándose y procesando los datos. Una vez complete el procesamiento del archivo histórico (24GB), continuará con el streaming y mostrará los resultados finales.


