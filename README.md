# Autenticacion JWT

API de autenticación construida con Spring Boot y Java.

## Tecnologías

- Java 17 · Spring Boot 4.1.2 · Maven · JPA · H2 · Lombok · jjwt 0.12.6 · spring-security-crypto (BCrypt)

## Arquitectura por Capas

El proyecto sigue una arquitectura en capas para separar responsabilidades y facilitar el mantenimiento:

- **Controller:** expone los endpoints HTTP de registro, inicio de sesión y renovación de tokens.
- **Service:** contiene la lógica de negocio de autenticación. `JwtService` genera, valida, inspecciona y renueva tokens JWT.
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

### Iniciar sesión

**POST** `http://localhost:8080/auth/login`

Enviar las credenciales de un usuario registrado:

```json
{
  "username": "ana",
  "password": "mi-contrasena"
}
```

Si las credenciales son correctas, la respuesta es **200 OK** y contiene un JWT:

```json
{
  "message": "Login exitoso",
  "data": {
    "jwt": "<token-jwt>"
  }
}
```

Si el usuario no existe o la contraseña no coincide, devuelve **401 Unauthorized** con el mensaje `Credenciales inválidas`.

### Renovar un token

**POST** `http://localhost:8080/auth/refresh`

Enviar el encabezado `Authorization`, usando el esquema `Bearer` seguido de un espacio y el JWT obtenido al iniciar sesión.

Si el token es válido y no ha expirado, la respuesta es **200 OK**:

```json
{
  "message": "Token refrescado correctamente",
  "data": {
    "jwt": "<nuevo-token>"
  }
}
```

Un encabezado ausente o inválido, o un token expirado, produce una respuesta **401 Unauthorized**.

### Probar con Thunder Client o Postman

1. Inicia la aplicación y crea una petición nueva.
2. Selecciona el método `POST` e introduce la URL del endpoint.
3. En **Body**, selecciona JSON y envía el cuerpo de registro o las credenciales de inicio de sesión.
4. Copia el JWT de la respuesta exitosa del login. Para renovar el token, agrega el encabezado `Authorization`, selecciona el esquema `Bearer` y pega el JWT como token. Deja el cuerpo vacío.
5. Envía cada petición y revisa el código HTTP y el JSON de respuesta.
