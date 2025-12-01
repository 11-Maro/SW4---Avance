#!/bin/bash

# Script para sincronizar cambios locales con el repositorio remoto
# sin duplicar archivos

set -e  # Salir si hay error

echo "=========================================="
echo "Sincronización con Repositorio Remoto"
echo "=========================================="
echo ""

# Colores para output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Verificar si git está instalado
if ! command -v git &> /dev/null; then
    echo -e "${RED}Git no está instalado.${NC}"
    echo "Instalando git..."
    sudo apt install -y git
fi

# Ir al directorio del proyecto
cd "$(dirname "$0")"
PROJECT_DIR=$(pwd)
echo -e "${GREEN}Directorio del proyecto:${NC} $PROJECT_DIR"
echo ""

# Verificar si ya hay un repositorio git
if [ -d ".git" ]; then
    echo -e "${YELLOW}Ya existe un repositorio git local.${NC}"
    echo "Verificando estado..."
    git status
    echo ""
    read -p "¿Continuar? (s/n): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Ss]$ ]]; then
        exit 1
    fi
else
    echo -e "${GREEN}Inicializando repositorio git...${NC}"
    git init
fi

# Configurar remote si no existe
if ! git remote | grep -q "origin"; then
    echo -e "${GREEN}Agregando remote 'origin'...${NC}"
    git remote add origin https://github.com/11-Maro/SW4---Avance.git
else
    echo -e "${YELLOW}El remote 'origin' ya existe.${NC}"
    git remote -v
fi
echo ""

# Traer información del remoto
echo -e "${GREEN}Obteniendo información del repositorio remoto...${NC}"
git fetch origin
echo ""

# Detectar el branch principal
MAIN_BRANCH=""
if git ls-remote --heads origin main | grep -q "main"; then
    MAIN_BRANCH="main"
elif git ls-remote --heads origin master | grep -q "master"; then
    MAIN_BRANCH="master"
else
    echo -e "${RED}No se pudo detectar el branch principal (main/master)${NC}"
    echo "Branches disponibles en remoto:"
    git ls-remote --heads origin
    read -p "Ingresa el nombre del branch principal: " MAIN_BRANCH
fi

echo -e "${GREEN}Branch principal detectado:${NC} $MAIN_BRANCH"
echo ""

# Verificar si estamos en un branch
CURRENT_BRANCH=$(git branch --show-current 2>/dev/null || echo "")
if [ -z "$CURRENT_BRANCH" ]; then
    echo -e "${GREEN}Creando branch local desde remoto...${NC}"
    git checkout -b $MAIN_BRANCH origin/$MAIN_BRANCH 2>/dev/null || git checkout -b $MAIN_BRANCH
else
    echo -e "${YELLOW}Estás en el branch:${NC} $CURRENT_BRANCH"
    if [ "$CURRENT_BRANCH" != "$MAIN_BRANCH" ]; then
        echo -e "${YELLOW}Cambiando al branch $MAIN_BRANCH...${NC}"
        git checkout $MAIN_BRANCH 2>/dev/null || git checkout -b $MAIN_BRANCH
    fi
fi
echo ""

# Sincronizar con el remoto (sin sobrescribir cambios locales)
echo -e "${GREEN}Sincronizando con el remoto...${NC}"
echo "Esto traerá los archivos del remoto sin perder tus cambios locales."
echo ""

# Intentar hacer merge del remoto
if git pull origin $MAIN_BRANCH --no-edit --no-rebase 2>&1 | grep -q "conflict"; then
    echo -e "${RED}¡Hay conflictos!${NC}"
    echo "Archivos en conflicto:"
    git status | grep "both modified" || true
    echo ""
    echo "Debes resolver los conflictos manualmente antes de continuar."
    echo "Edita los archivos marcados y luego ejecuta:"
    echo "  git add <archivos_resueltos>"
    echo "  git commit"
    exit 1
fi

echo -e "${GREEN}✓ Sincronización completada${NC}"
echo ""

# Mostrar archivos modificados
echo -e "${GREEN}Archivos modificados localmente:${NC}"
git status --short
echo ""

# Agregar solo los archivos que modificamos
echo -e "${GREEN}¿Qué archivos quieres agregar al commit?${NC}"
echo "1. Solo los archivos modificados (Main.java y GraphPrinter.java)"
echo "2. Todos los cambios"
echo "3. Seleccionar manualmente"
read -p "Opción (1/2/3): " -n 1 -r
echo ""

case $REPLY in
    1)
        echo -e "${GREEN}Agregando solo archivos modificados...${NC}"
        git add src/main/java/com/icesi/mio/Main.java
        git add src/main/java/com/icesi/mio/graph/GraphPrinter.java
        ;;
    2)
        echo -e "${GREEN}Agregando todos los cambios...${NC}"
        git add .
        ;;
    3)
        echo -e "${YELLOW}Agrega los archivos manualmente con:${NC}"
        echo "  git add <archivo>"
        exit 0
        ;;
    *)
        echo -e "${RED}Opción inválida${NC}"
        exit 1
        ;;
esac

# Ver qué se va a commitear
echo ""
echo -e "${GREEN}Archivos que se van a commitear:${NC}"
git status --short
echo ""

# Hacer commit
read -p "¿Hacer commit? (s/n): " -n 1 -r
echo
if [[ $REPLY =~ ^[Ss]$ ]]; then
    echo -e "${GREEN}Haciendo commit...${NC}"
    git commit -m "Implementación: Cálculo de velocidades promedio de arcos

- Agregado procesamiento de datos históricos (datagrams4history.csv)
- Agregado procesamiento de datos de streaming (datagrams4streaming.csv) - BONUS
- Actualizado Main.java para integrar cálculo de velocidades
- Actualizado GraphPrinter.java para mostrar velocidades promedio
- Implementado cálculo automático de velocidades para todos los arcos del grafo"
    echo -e "${GREEN}✓ Commit realizado${NC}"
else
    echo -e "${YELLOW}Commit cancelado${NC}"
    exit 0
fi

# Push al remoto
echo ""
read -p "¿Hacer push al repositorio remoto? (s/n): " -n 1 -r
echo
if [[ $REPLY =~ ^[Ss]$ ]]; then
    echo -e "${GREEN}Haciendo push...${NC}"
    git push origin $MAIN_BRANCH
    echo -e "${GREEN}✓ Push completado${NC}"
else
    echo -e "${YELLOW}Push cancelado. Puedes hacerlo después con:${NC}"
    echo "  git push origin $MAIN_BRANCH"
fi

echo ""
echo -e "${GREEN}=========================================="
echo "Sincronización completada"
echo "==========================================${NC}"

