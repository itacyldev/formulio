#!/bin/bash

# Configura estas variables según tu repositorio y credenciales
GITHUB_REPO="itacyldev/formulio" # Reemplaza con tu repositorio
GITHUB_USER="itacyljenkins" # Reemplaza con tu usuario de GitHub
GITHUB_TOKEN="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" # Reemplaza con tu token de acceso personal

# Clona el repositorio
echo "Clonando el repositorio https://${GITHUB_USER}@github.com/${GITHUB_REPO}.git"
git clone https://${GITHUB_USER}:${GITHUB_TOKEN}@github.com/${GITHUB_REPO}.git
cd $(basename "$GITHUB_REPO")

# Obtener todos los tags remotos
echo "Obteniendo todos los tags remotos"
tags=$(git ls-remote --tags origin | awk '{print $2}' | sed 's|refs/tags/||')

echo "Tags encontrados:"
echo "$tags"

# Iterar sobre los tags y eliminarlos
for tag in $tags; do
    echo "Eliminando tag: $tag"
    git push origin --delete tag $tag
done

# Regresar al directorio anterior
cd ..

# Eliminar el repositorio clonado
rm -rf $(basename "$GITHUB_REPO")
echo "Proceso completado."
