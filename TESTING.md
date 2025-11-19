# Guía de Testing y Verificación Continua

Esta guía explica cómo ejecutar pruebas y configurar verificación continua para el proyecto.

## 📋 Índice

1. [Ejecutar Tests](#ejecutar-tests)
2. [Tipos de Tests](#tipos-de-tests)
3. [Estructura de Tests](#estructura-de-tests)
4. [Verificación Continua](#verificación-continua)
5. [Mejores Prácticas](#mejores-prácticas)

---

## 🚀 Ejecutar Tests

### Ejecutar todos los tests de un módulo

**Management Bot:**
```bash
cd telegram-directory/management-bot
mvn test
```

**Query Bot:**
```bash
cd telegram-directory/query-bot
mvn test
```

### Ejecutar tests de ambos módulos

Desde la raíz del proyecto:
```bash
cd telegram-directory/management-bot && mvn test
cd ../query-bot && mvn test
```

### Ejecutar un test específico

```bash
mvn test -Dtest=CategoryServiceTest
```

### Ejecutar tests con más información

```bash
mvn test -X  # Modo verbose
```

### Compilar sin ejecutar tests

```bash
mvn clean compile -DskipTests
```

---

## 🧪 Tipos de Tests

### 1. Tests Unitarios

Los tests unitarios prueban componentes individuales de forma aislada usando **mocks**.

**Ejemplos:**
- `CategoryServiceTest` - Prueba la lógica de negocio de categorías
- `ProfessionalServiceTest` - Prueba la lógica de negocio de profesionales

**Características:**
- Usan `@Mock` para simular dependencias
- Rápidos de ejecutar
- No requieren base de datos real

**Ejecutar solo tests unitarios:**
```bash
mvn test -Dtest="*ServiceTest"
```

### 2. Tests de Integración

Los tests de integración prueban la interacción con la base de datos usando **H2 en memoria**.

**Ejemplos:**
- `ProfessionalRepositoryTest` - Prueba consultas y persistencia

**Características:**
- Usan `@DataJpaTest` de Spring Boot
- Requieren configuración de base de datos (H2)
- Prueban queries y relaciones JPA

**Ejecutar solo tests de integración:**
```bash
mvn test -Dtest="*RepositoryTest"
```

---

## 📁 Estructura de Tests

```
telegram-directory/
├── management-bot/
│   └── src/
│       └── test/
│           ├── java/
│           │   └── com/telegram/directory/management/
│           │       ├── service/
│           │       │   ├── CategoryServiceTest.java       ← Tests unitarios
│           │       │   └── ProfessionalServiceTest.java
│           │       └── repository/
│           │           └── ProfessionalRepositoryTest.java ← Tests integración
│           └── resources/
│               └── application-test.properties             ← Config para tests
│
└── query-bot/
    └── src/
        └── test/
            ├── java/
            │   └── com/telegram/directory/query/
            │       ├── service/
            │       │   └── ProfessionalServiceTest.java
            │       └── repository/
            │           └── ProfessionalRepositoryTest.java
            └── resources/
                └── application-test.properties
```

---

## 🔄 Verificación Continua

### Configuración Manual (Script Bash/PowerShell)

**Windows PowerShell (`run-tests.ps1`):**
```powershell
Write-Host "🧪 Ejecutando tests del Management Bot..." -ForegroundColor Cyan
cd telegram-directory/management-bot
mvn clean test
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Tests del Management Bot fallaron" -ForegroundColor Red
    exit 1
}

Write-Host "🧪 Ejecutando tests del Query Bot..." -ForegroundColor Cyan
cd ../query-bot
mvn clean test
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Tests del Query Bot fallaron" -ForegroundColor Red
    exit 1
}

Write-Host "✅ Todos los tests pasaron exitosamente!" -ForegroundColor Green
```

**Linux/Mac Bash (`run-tests.sh`):**
```bash
#!/bin/bash

echo "🧪 Ejecutando tests del Management Bot..."
cd telegram-directory/management-bot
mvn clean test
if [ $? -ne 0 ]; then
    echo "❌ Tests del Management Bot fallaron"
    exit 1
fi

echo "🧪 Ejecutando tests del Query Bot..."
cd ../query-bot
mvn clean test
if [ $? -ne 0 ]; then
    echo "❌ Tests del Query Bot fallaron"
    exit 1
fi

echo "✅ Todos los tests pasaron exitosamente!"
```

**Ejecutar script:**
```bash
# PowerShell
.\run-tests.ps1

# Bash
chmod +x run-tests.sh
./run-tests.sh
```

### Git Hooks (Pre-commit)

Crea `.git/hooks/pre-commit` para ejecutar tests antes de cada commit:

```bash
#!/bin/bash
cd telegram-directory/management-bot && mvn test -q
if [ $? -ne 0 ]; then
    echo "❌ Tests fallaron. Commit cancelado."
    exit 1
fi
cd ../query-bot && mvn test -q
if [ $? -ne 0 ]; then
    echo "❌ Tests fallaron. Commit cancelado."
    exit 1
fi
```

**Hacer ejecutable:**
```bash
chmod +x .git/hooks/pre-commit
```

### CI/CD con GitHub Actions

Crea `.github/workflows/tests.yml`:

```yaml
name: Tests

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main, develop ]

jobs:
  test-management-bot:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
      - name: Test Management Bot
        run: |
          cd telegram-directory/management-bot
          mvn clean test

  test-query-bot:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
      - name: Test Query Bot
        run: |
          cd telegram-directory/query-bot
          mvn clean test
```

---

## ✅ Mejores Prácticas

### 1. Nomenclatura de Tests

Usa nombres descriptivos:
```java
@Test
@DisplayName("Debería crear una categoría exitosamente")
void testCreateCategory_Success() { ... }
```

### 2. Estructura AAA

Organiza tus tests con Arrange-Act-Assert:

```java
@Test
void testExample() {
    // Arrange - Preparar datos
    Category category = new Category("Nombre", "Descripción");
    
    // Act - Ejecutar acción
    Category result = service.create("Nombre", "Descripción");
    
    // Assert - Verificar resultado
    assertEquals("Nombre", result.getName());
}
```

### 3. Tests Independientes

Cada test debe ser independiente y poder ejecutarse en cualquier orden.

### 4. Mocks Limpios

Limpia mocks entre tests:
```java
@BeforeEach
void setUp() {
    reset(mockRepository);
}
```

### 5. Cobertura de Código

Genera reporte de cobertura:
```bash
mvn test jacoco:report
```

El reporte estará en: `target/site/jacoco/index.html`

---

## 📊 Verificar Cobertura de Tests

### Instalar Jacoco Plugin

Agrega al `pom.xml`:

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.10</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### Generar Reporte

```bash
mvn test jacoco:report
```

Abre `target/site/jacoco/index.html` en tu navegador.

---

## 🐛 Troubleshooting

### Error: "No tests found"

- Verifica que los tests estén en `src/test/java`
- Asegúrate de que los métodos tengan `@Test`

### Error: "Database connection failed"

- Los tests usan H2 en memoria, no PostgreSQL
- Verifica `application-test.properties`

### Tests muy lentos

- Evita tests que accedan a servicios externos
- Usa mocks para dependencias externas
- Limita tests de integración a lo esencial

---

## 📚 Recursos Adicionales

- [Spring Boot Testing](https://spring.io/guides/gs/testing-web/)
- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)

---

¡Feliz testing! 🎉

