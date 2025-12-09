# Directorio de Oficios en Telegram

Sistema de directorio de profesionales organizado en un monorepo con dos microservicios Spring Boot que funcionan como bots de Telegram.

## Arquitectura

- **Monorepo** con dos microservicios:
  - `management-bot`: Bot de gestión (CRUD completo)
  - `query-bot`: Bot de consulta pública (solo lectura)
- **Base de datos**: PostgreSQL
- **Stack**: Spring Boot 3 + Java 17

## Estructura del Proyecto

```
telegram-directory/
├── management-bot/          # Microservicio de gestión
├── query-bot/              # Microservicio de consulta
└── README.md
```

## Requisitos Previos

- Java 17 o superior instalado
- Maven instalado
- PostgreSQL instalado y corriendo
- Tokens de bots de Telegram (obtenidos desde @BotFather)

## Configuración

### 1. Crear la base de datos

Abre pgAdmin o tu cliente de PostgreSQL y crea la base de datos:

```sql
CREATE DATABASE telegram_directory;
```

### 2. Configurar credenciales y tokens

Edita los archivos `application.properties` de cada microservicio:

**management-bot/src/main/resources/application.properties:**
- Ajusta `spring.datasource.password` si tu contraseña de PostgreSQL es diferente
- Reemplaza `TU_TOKEN_DEL_BOT_DE_GESTION_AQUI` con el token real de tu bot de gestión
- Reemplaza `TU_USERNAME_DEL_BOT_DE_GESTION_AQUI` con el username real (ej: `@MiBotGestion`)

**query-bot/src/main/resources/application.properties:**
- Ajusta `spring.datasource.password` si tu contraseña de PostgreSQL es diferente
- Reemplaza `TU_TOKEN_DEL_BOT_DE_CONSULTA_AQUI` con el token real de tu bot de consulta
- Reemplaza `TU_USERNAME_DEL_BOT_DE_CONSULTA_AQUI` con el username real (ej: `@MiBotConsulta`)

### 3. Ejecutar los microservicios

Abre **dos terminales** y ejecuta en cada una:

**Terminal 1 - Management Bot:**
```bash
cd management-bot
mvn spring-boot:run
```

**Terminal 2 - Query Bot:**
```bash
cd query-bot
mvn spring-boot:run
```

¡Listo! Los bots deberían estar funcionando y conectados a Telegram.

## Comandos del Bot de Gestión (management-bot)

- `/add <oficio> | <nombre> | <ciudad>` - Agrega un profesional
- `/update <id> | <oficio> | <nombre> | <ciudad>` - Actualiza un profesional
- `/delete <id>` - Elimina un profesional
- `/list` - Lista todos los profesionales

**Ejemplo:**
```
/add electricista | Juan Pérez | Rosario
```

## Comandos del Bot de Consulta (query-bot)

- `/findTrade <oficio>` - Busca profesionales por oficio
- `/findCity <ciudad>` - Busca profesionales por ciudad
- `/find <oficio> | <ciudad>` - Busca por oficio y ciudad

**Ejemplos:**
```
/findTrade electricista
/findCity Rosario
/find electricista | Rosario
```

## Modelo de Datos

La tabla `professional` se crea automáticamente cuando ejecutas el management-bot por primera vez. Estructura:

```sql
CREATE TABLE professional (
    id BIGSERIAL PRIMARY KEY,
    trade VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL
);
```

## Verificar que todo funciona

- **Management Bot**: `http://localhost:8080/actuator/health`
- **Query Bot**: `http://localhost:8081/actuator/health`

Si ambos responden `{"status":"UP"}`, los servicios están corriendo correctamente.

## Notas

- El management-bot usa el puerto **8080**
- El query-bot usa el puerto **8081**
- La base de datos debe estar corriendo antes de iniciar los bots
- Los tokens de Telegram se obtienen desde @BotFather en Telegram
