# Autenticacion JWT

API de autenticación en desarrollo, construida con Spring Boot y Java.

## Tecnologías

- Java 17 · Spring Boot 4.1.2 (versión estable actual a noviembre de 2026) · Maven · JPA · H2 · Lombok · jjwt 0.12.6 · spring-security-crypto (BCrypt)

## Arquitectura por Capas

El proyecto sigue una arquitectura en capas para separar responsabilidades y facilitar el mantenimiento:

- **Controller:** expone los endpoints HTTP y coordina las solicitudes y respuestas. Se incorporará al desarrollar los endpoints de la API.
- **Service:** contiene la lógica de negocio. `JwtService` genera, valida, inspecciona y renueva tokens JWT.
- **Repository:** abstrae el acceso a datos con Spring Data JPA.
- **Entity/Domain:** representa los datos persistidos; incluye las entidades de usuarios y roles.
- **DTO:** define los datos de entrada y salida de la API, separados del modelo de persistencia. Se añadirá junto con los endpoints.
- **Config/Filter:** configura y aplica el filtro HTTP que valida JWT y agrega sus claims a las solicitudes, exceptuando las rutas de autenticación.

El manual de usuario se agregará cuando la API esté funcional.
