# Autenticacion JWT

API de autenticación en desarrollo, construida con Spring Boot y Java.

## Tecnologías

- Java 17
- Spring Boot 4.x
- H2 en memoria
- Spring Data JPA
- JWT con JJWT 0.12.6
- Lombok
- Spring Security Crypto, con BCrypt para el hash de contraseñas

## Arquitectura por Capas

El proyecto sigue una arquitectura en capas para separar responsabilidades y facilitar el mantenimiento:

- **Controller:** expone los endpoints HTTP y coordina las solicitudes y respuestas. Se incorporará al desarrollar los endpoints de la API.
- **Service:** contiene la lógica de negocio. `JwtService` genera, valida, inspecciona y renueva tokens JWT.
- **Repository:** abstrae el acceso a datos con Spring Data JPA.
- **Entity/Domain:** representa los datos persistidos; incluye las entidades de usuarios y roles.
- **DTO:** define los datos de entrada y salida de la API, separados del modelo de persistencia. Se añadirá junto con los endpoints.
- **Config/Filter:** centraliza la configuración de seguridad y los filtros HTTP para procesar tokens en las solicitudes protegidas. Se añadirá al implementar la autenticación HTTP.

El manual de usuario se agregará cuando la API esté funcional.
