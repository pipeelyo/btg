# Proyecto BTG

Este es un proyecto Spring Boot creado como base para aplicaciones web RESTful.

## Características

- Spring Boot 3.x
- Spring Web MVC para APIs REST
- Spring Data JPA para acceso a datos
- Base de datos H2 en memoria (para desarrollo)
- Configuración básica de JPA/Hibernate
- Ejemplo de controlador REST

## Requisitos

- Java 17 o superior
- Maven 3.6.3 o superior

## Configuración

1. Clona el repositorio
2. Configura las propiedades de la base de datos en `src/main/resources/application.properties`
3. Para desarrollo, la aplicación usa una base de datos H2 en memoria

## Ejecución

```bash
./mvnw spring-boot:run
```

La aplicación estará disponible en: http://localhost:8080

## Endpoints

- `GET /api/hello` - Endpoint de ejemplo que devuelve un saludo

## Base de datos H2 (solo desarrollo)

La consola H2 está disponible en: http://localhost:8080/h2-console

- JDBC URL: jdbc:h2:mem:btgdb
- User Name: sa
- Password: (dejar en blanco)
