# Autenticacion JWT

API de autenticación construida con Spring Boot y Java.

## Tecnologías

- Java 17 · Spring Boot 4.1.2 (versión estable actual a noviembre de 2026) · Maven · JPA · H2 · Lombok · jjwt 0.12.6 · spring-security-crypto (BCrypt)

## Arquitectura por Capas

El proyecto sigue una arquitectura en capas para separar responsabilidades y facilitar el mantenimiento:

- **Controller:** expone los endpoints HTTP de registro y renovación de tokens.
- **Service:** contiene la lógica de negocio. `JwtService` genera, valida, inspecciona y renueva tokens JWT.
- **Repository:** abstrae el acceso a datos con Spring Data JPA.
- **Entity/Domain:** representa los datos persistidos; incluye las entidades de usuarios y roles.
- **DTO:** define los datos de entrada y salida de la API, separados del modelo de persistencia. Las respuestas de usuario no exponen la contraseña.
- **Config/Filter:** configura y aplica el filtro HTTP que valida JWT y agrega sus claims a las solicitudes, exceptuando las rutas de autenticación.

## Manual de usuario

### Requisitos y ejecución

- Tener instalado Java 17.
- Desde la raíz del proyecto, iniciar la aplicación con:

```bash
./mvnw spring-boot:run
```

La API estará disponible en `http://localhost:8080`.

### Estructura de respuesta

Las respuestas de la API usan el formato estándar:

```json
{
  "message": "Descripción del resultado",
  "data": {}
}
```

Cuando una operación no devuelve datos, `data` es `null`.

### Registrar un usuario

**POST** `http://localhost:8080/auth/register`

Enviar un cuerpo JSON con el nombre de usuario, contraseña, correo y el ID de un rol existente. La aplicación inicializa los roles `ADMIN` (ID 1) y `USER` (ID 2) si la tabla está vacía.

```json
{
  "username": "ana",
  "password": "mi-contrasena",
  "email": "ana@example.com",
  "rolId": 2
}
```

En un registro exitoso, la respuesta es **201 Created**:

```json
{
  "message": "usuario registrado correctamente",
  "data": {
    "id": 1,
    "username": "ana",
    "email": "ana@example.com",
    "rolId": 2
  }
}
```

La contraseña se almacena con BCrypt y no se incluye en la respuesta. Si el usuario o correo ya existe, o el rol no existe, el servicio devuelve el mensaje correspondiente y `data: null`.

### Renovar un token

**POST** `http://localhost:8080/auth/refresh`

Enviar el encabezado:

```text
Authorization: Bearer <token>
```

Si el token es válido y no ha expirado, la respuesta es **200 OK**:

```json
{
  "message": "Token refrescado correctamente",
  "data": {
    "jwt": "<nuevo-token>"
  }
}
```

Un encabezado ausente o inválido, o un token expirado, produce una respuesta **401 Unauthorized**. Este endpoint requiere un JWT existente; el endpoint de inicio de sesión no está implementado actualmente.

### Probar con Thunder Client o Postman

1. Inicia la aplicación y crea una petición nueva.
2. Selecciona el método `POST` e introduce la URL del endpoint.
3. En **Body**, selecciona JSON y pega el cuerpo de registro mostrado arriba.
4. Para renovar un token, selecciona **Headers** y agrega `Authorization` con el valor `Bearer <token>`. Deja el cuerpo vacío.
5. Envía la petición y revisa el código HTTP y el JSON de respuesta.
