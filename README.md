# AUREA Full Stack

Proyecto full-stack desarrollado como laboratorio profesional de EMD Tech Consulting.

## Arquitectura

Frontend:
- React
- Vite
- Axios
- React Router
- SweetAlert2

Backend:
- Java 21
- Spring Boot
- Spring Security
- JWT
- Spring Data JPA
- Hibernate
- Maven

Base de datos:
- MariaDB
- Flyway

Infraestructura:
- Docker
- Dockerfile
- Variables de entorno

## Flujo de arquitectura

React
→ HTTP / JSON
→ Controller
→ Service
→ Repository
→ JPA / Hibernate
→ MariaDB

## Funcionalidades principales

- Autenticación con JWT
- Gestión de usuarios y roles
- Creación de pedidos
- Edición de pedidos
- Eliminación de ítems
- Productos de catálogo
- Ítems manuales
- Estados de pedidos
- Delivery
- Paginación y filtros
- Manejo global de excepciones
- Migraciones de base de datos con Flyway
- Migración controlada desde Firebase
- Pruebas automatizadas

## Testing

Estado actual:

- 59 pruebas automatizadas
- 0 fallos
- 0 errores

## Seguridad

Los secretos y credenciales se gestionan mediante variables de entorno.

Los siguientes elementos no forman parte del repositorio:

- `.env`
- credenciales
- backups reales
- datos privados
- `node_modules`
- `target`

Se incluye `.env.example` como plantilla de configuración.

## Migraciones Flyway

- V1 - Esquema inicial
- V2 - Campos de sincronización Firebase
- V3 - Campos legacy de pagos

## Estructura del monorepo

aurea-fullstack
├── aurea
│   └── Backend Java / Spring Boot
├── aurea-frontend
│   └── Frontend React
├── .gitignore
└── README.md

## Autor

Javier Enriquez Morales
Director General 
EMD Tech Consulting


## Estado

Checkpoint Fase 1 - Java + Spring Boot + React + MariaDB
