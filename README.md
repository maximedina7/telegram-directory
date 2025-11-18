# Directorio de Oficios en Telegram

Sistema de directorio de profesionales organizado en un monorepo con dos microservicios Spring Boot que funcionan como bots de Telegram.

## Arquitectura

- **Monorepo** con dos microservicios:
  - `management-bot`: Bot de gestión (CRUD completo)
  - `query-bot`: Bot de consulta pública (solo lectura)
- **Base de datos**: PostgreSQL
- **Orquestación**: Docker Compose
- **Stack**: Spring Boot 3 + Java 17

## Estructura del Proyecto

```
telegram-directory/
├── management-bot/          # Microservicio de gestión
├── query-bot/               # Microservicio de consulta
├── docker-compose.yml       # Orquestación de servicios
└── README.md
```

## Requisitos Previos

- Docker y Docker Compose instalados
- Tokens de bots de Telegram (obtenidos desde @BotFather)

## Configuración

1. Crea un archivo `.env` en la raíz del proyecto con las siguientes variables:

```env
MANAGEMENT_BOT_TOKEN=tu_token_del_bot_de_gestion
MANAGEMENT_BOT_USERNAME=tu_bot_de_gestion
QUERY_BOT_TOKEN=tu_token_del_bot_de_consulta
QUERY_BOT_USERNAME=tu_bot_de_consulta
```

2. Levanta todos los servicios:

```bash
docker-compose up -d
```

## Comandos del Bot de Gestión (management-bot)

- `/add <oficio> | <nombre> | <ciudad>` - Agrega un profesional
- `/update <id> | <oficio> | <nombre> | <ciudad>` - Actualiza un profesional
- `/delete <id>` - Elimina un profesional
- `/list` - Lista todos los profesionales

## Comandos del Bot de Consulta (query-bot)

- `/findTrade <oficio>` - Busca profesionales por oficio
- `/findCity <ciudad>` - Busca profesionales por ciudad
- `/find <oficio> | <ciudad>` - Busca por oficio y ciudad

## Modelo de Datos

```sql
CREATE TABLE professional (
    id BIGSERIAL PRIMARY KEY,
    trade VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL
);
```

## Desarrollo

Para desarrollar localmente sin Docker:

1. Asegúrate de tener PostgreSQL corriendo
2. Configura las variables de entorno en tu IDE o sistema
3. Ejecuta cada microservicio desde su directorio con `mvn spring-boot:run`

## Detener los Servicios

```bash
docker-compose down
```

Para eliminar también los volúmenes (datos de la base de datos):

```bash
docker-compose down -v
```

