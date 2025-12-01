# Comandos para hacer Commit

## Si git no está instalado:
```bash
sudo apt install git
```

## Pasos para hacer commit:

### 1. Verificar/Inicializar repositorio
```bash
cd /home/datf/Documents/SW4---Avance-main

# Si no hay repositorio git, inicializarlo:
git init

# Configurar el remote (si no está configurado):
git remote add origin https://github.com/11-Maro/SW4---Avance.git
```

### 2. Ver estado de los cambios
```bash
git status
```

### 3. Agregar archivos modificados
```bash
# Agregar archivos modificados
git add src/main/java/com/icesi/mio/Main.java
git add src/main/java/com/icesi/mio/graph/GraphPrinter.java

# O agregar todos los cambios:
git add .
```

### 4. Hacer commit
```bash
git commit -m "Implementación: Cálculo de velocidades promedio de arcos

- Agregado procesamiento de datos históricos (datagrams4history.csv)
- Agregado procesamiento de datos de streaming (datagrams4streaming.csv) - BONUS
- Actualizado Main.java para integrar cálculo de velocidades
- Actualizado GraphPrinter.java para mostrar velocidades promedio
- Implementado cálculo automático de velocidades para todos los arcos del grafo"
```

### 5. Push al repositorio (si es necesario)
```bash
# Verificar branch actual
git branch

# Push (ajustar branch según corresponda)
git push origin main
# o
git push origin master
```

## Archivos modificados en este commit:

1. **src/main/java/com/icesi/mio/Main.java**
   - Agregado procesamiento de datos históricos
   - Agregado procesamiento de streaming (BONUS)
   - Integración completa del flujo de cálculo de velocidades

2. **src/main/java/com/icesi/mio/graph/GraphPrinter.java**
   - Nuevo método `printAllGraphsWithSpeeds()`
   - Visualización mejorada con velocidades promedio
   - Estadísticas de velocidades

## Nota:
Si el repositorio ya existe y solo necesitas hacer commit de estos cambios, puedes saltar el paso 1 y comenzar desde el paso 2.

