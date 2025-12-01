# Sincronizar con el Repositorio Remoto (sin duplicar)

## Situación
Descargaste el proyecto como ZIP y ahora quieres sincronizar tus cambios con el repositorio remoto sin duplicar archivos.

## Pasos para sincronizar correctamente:

### 1. Instalar git (si no está instalado)
```bash
sudo apt install git
```

### 2. Configurar git (si es primera vez)
```bash
git config --global user.name "Tu Nombre"
git config --global user.email "tu.email@example.com"
```

### 3. Inicializar y conectar con el repositorio remoto
```bash
cd /home/datf/Documents/SW4---Avance-main

# Inicializar repositorio local
git init

# Agregar el remote
git remote add origin https://github.com/11-Maro/SW4---Avance.git

# Verificar que se agregó correctamente
git remote -v
```

### 4. Traer el contenido del repositorio remoto (IMPORTANTE)
```bash
# Traer la información del remoto sin mergear aún
git fetch origin

# Ver qué branches existen en el remoto
git branch -r

# Hacer checkout del branch principal (probablemente main o master)
git checkout -b main origin/main
# O si el branch se llama master:
# git checkout -b master origin/master
```

### 5. Ver qué archivos son diferentes
```bash
# Ver qué archivos tienes localmente que no están en el remoto
git status

# Ver diferencias (si hay conflictos)
git diff
```

### 6. Agregar solo tus cambios nuevos
```bash
# Agregar solo los archivos que modificaste
git add src/main/java/com/icesi/mio/Main.java
git add src/main/java/com/icesi/mio/graph/GraphPrinter.java

# Si hay otros archivos nuevos que quieres agregar:
# git add nombre_archivo

# Ver qué se va a commitear
git status
```

### 7. Hacer commit de tus cambios
```bash
git commit -m "Implementación: Cálculo de velocidades promedio de arcos

- Agregado procesamiento de datos históricos (datagrams4history.csv)
- Agregado procesamiento de datos de streaming (datagrams4streaming.csv) - BONUS
- Actualizado Main.java para integrar cálculo de velocidades
- Actualizado GraphPrinter.java para mostrar velocidades promedio
- Implementado cálculo automático de velocidades para todos los arcos del grafo"
```

### 8. Sincronizar con el remoto (sin duplicar)
```bash
# Primero, traer cualquier cambio nuevo del remoto
git pull origin main --no-rebase

# Si hay conflictos, resolverlos manualmente
# Luego hacer push
git push origin main
```

## Si hay conflictos:

Si `git pull` muestra conflictos:
```bash
# Ver los archivos en conflicto
git status

# Editar los archivos con conflictos manualmente
# Buscar las marcas <<<<<<< ======= >>>>>>>

# Después de resolver conflictos:
git add archivo_resuelto.java
git commit -m "Resuelto conflicto en archivo_resuelto.java"
git push origin main
```

## Archivos que NO debes commitear (ya están en .gitignore):
- `target/` (compilados)
- Archivos grandes de datos (si están en .gitignore)
- Archivos temporales

## Verificación final:
```bash
# Ver el estado final
git status

# Ver el historial
git log --oneline -5
```

