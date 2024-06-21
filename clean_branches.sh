#!/bin/bash

# Configura estas variables según tu repositorio y credenciales
GITHUB_REPO="itacyldev/formulio" # Reemplaza con tu repositorio
GITHUB_USER="itacyljenkins" # Reemplaza con tu usuario de GitHub
GITHUB_TOKEN="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" # Reemplaza con tu token de acceso personal

# Clona el repositorio
echo "Clonando el repositorio https://${GITHUB_USER}@github.com/${GITHUB_REPO}.git"
git clone https://${GITHUB_USER}:${GITHUB_TOKEN}@github.com/${GITHUB_REPO}.git
cd $(basename "$GITHUB_REPO")

# Obtener todas las ramas remotas excepto master
echo "Obteniendo todas las ramas remotas excepto 'master'"
branches=$(git branch -r | grep -v "origin/master" | grep -v "\->" | sed 's/origin\///')

echo "Ramas encontradas:"
echo "$branches"

# Iterar sobre las ramas y eliminarlas
for branch in $branches; do
    echo "Eliminando rama: $branch"
    git push origin --delete $branch
done

# Regresar al directorio anterior
cd ..

# Eliminar el repositorio clonado
rm -rf $(basename "$GITHUB_REPO")
echo "Proceso completado."