# Script de PowerShell para ejecutar todos los tests
# Uso: .\run-tests.ps1

Write-Host "🧪 Ejecutando tests del Management Bot..." -ForegroundColor Cyan
cd telegram-directory/management-bot
mvn clean test

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Tests del Management Bot fallaron" -ForegroundColor Red
    exit 1
}

Write-Host "`n🧪 Ejecutando tests del Query Bot..." -ForegroundColor Cyan
cd ../query-bot
mvn clean test

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Tests del Query Bot fallaron" -ForegroundColor Red
    exit 1
}

Write-Host "`n✅ Todos los tests pasaron exitosamente!" -ForegroundColor Green
cd ..

