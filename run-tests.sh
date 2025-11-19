#!/bin/bash

# Script bash para ejecutar todos los tests
# Uso: ./run-tests.sh

echo "🧪 Ejecutando tests del Management Bot..."
cd telegram-directory/management-bot
mvn clean test

if [ $? -ne 0 ]; then
    echo "❌ Tests del Management Bot fallaron"
    exit 1
fi

echo ""
echo "🧪 Ejecutando tests del Query Bot..."
cd ../query-bot
mvn clean test

if [ $? -ne 0 ]; then
    echo "❌ Tests del Query Bot fallaron"
    exit 1
fi

echo ""
echo "✅ Todos los tests pasaron exitosamente!"
cd ..

